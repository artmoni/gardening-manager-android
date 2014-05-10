package org.gots.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.analytics.GotsAnalytics;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class AuthenticationActivity extends AccountAuthenticatorActivity {
    public static final String PARAM_AUTHTOKEN_TYPE = "auth.token";

    public static final String PARAM_USER_PASS = "user.pass";

    private String TAG = "AuthenticationActivity";

    private String mAuthTokenType;

    private String mAccountType;

    public static final String PARAM_CREATE = "create";

    public static final int REQ_CODE_CREATE = 1;

    public static final int REQ_CODE_UPDATE = 2;

    public static final String EXTRA_REQUEST_CODE = "req.code";

    public static final int RESP_CODE_SUCCESS = 0;

    public static final int RESP_CODE_ERROR = 1;

    public static final int RESP_CODE_CANCEL = 2;

    public static final String ARG_ACCOUNT_TYPE = "type.account";

    public static final String ARG_AUTH_TYPE = "type.auth";

    public static final String ARG_IS_ADDING_NEW_ACCOUNT = "new";

    private static final String AUTH_TOKEN_TYPE = "token.nuxeo";

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.setContentView(R.layout.first_launch);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            mAuthTokenType = extras.getString(ARG_AUTH_TYPE);
            mAccountType = extras.getString(ARG_ACCOUNT_TYPE);

        }

        Button buttonCreateProfile = (Button) findViewById(R.id.buttonCreate);
        buttonCreateProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(AuthenticationActivity.this, AuthenticationActivity.class);
                startActivityForResult(intent, 1);

            }

        });

        View connect = (View) findViewById(R.id.buttonConnect);
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(FirstLaunchActivity.this, LoginActivity.class);
                // startActivityForResult(intent, 2);
                launchGoogle();
            }

        });

        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

    }

    public void onCancelClick(View v) {
        this.finish();

    }

    @Override
    protected void onDestroy() {
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
        super.onDestroy();
    }

    void launchGoogle() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        final List<Account> usableAccounts = new ArrayList<Account>();
        List<String> items = new ArrayList<String>();
        for (Account account : accounts) {
            if ("com.google".equals(account.type)) {
                usableAccounts.add(account);
                items.add(String.format("%s (%s)", account.name, account.type));
            }
        }

        new AlertDialog.Builder(this).setTitle("Action").setItems(items.toArray(new String[items.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int item) {

                        new AsyncTask<String, Integer, Intent>() {

                            protected void onPreExecute() {
                                // setActionRefresh(true);
                                findViewById(R.id.textViewError).setVisibility(View.GONE);

                            };

                            @Override
                            protected Intent doInBackground(String... params) {
                                GotsSocialAuthentication authentication = new GoogleAuthentication(
                                        getApplicationContext());
                                String googleToken = null;
                                String nuxeoToken = null;
                                final Intent res = new Intent();

                                try {
                                    googleToken = authentication.getToken(params[0]);
                                    if (googleToken != null) {
                                        NuxeoAuthentication nuxeoAuthentication = new NuxeoAuthentication(
                                                getApplicationContext());
                                        nuxeoToken = nuxeoAuthentication.request_oauth2_token(googleToken);

                                        res.putExtra(AccountManager.KEY_ACCOUNT_NAME, usableAccounts.get(item).name);
                                        res.putExtra(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                                        res.putExtra(AccountManager.KEY_AUTHTOKEN, nuxeoToken);
                                        res.putExtra(PARAM_USER_PASS, "");
                                    }
                                } catch (UserRecoverableAuthException e) {
                                    startActivityForResult(e.getIntent(), 0);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (GoogleAuthException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }
                                return res;
                            }

                            @Override
                            protected void onPostExecute(Intent resultToken) {
                                if (resultToken != null) {
                                    // gotsPrefs.setNuxeoLogin(usableAccounts.get(item).name);
                                    // gotsPrefs.setToken(resultToken);
                                    // gotsPrefs.setConnectedToServer(true);
                                    // Toast.makeText(
                                    // getApplicationContext(),
                                    // getResources().getString(R.string.login_connect_description).replace(
                                    // "_ACCOUNT_", gotsPrefs.getNuxeoLogin()), Toast.LENGTH_SHORT).show();
                                    //
                                    // Intent intent = new Intent(FirstLaunchActivity.this, DashboardActivity.class);
                                    // startActivity(intent);
                                    // finish();
                                    finishLogin(resultToken);
                                } else {
                                    // Toast.makeText(getApplicationContext(),
                                    // "Error requesting GoogleAuthUtil.getToken",
                                    // Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.textViewError).setVisibility(View.VISIBLE);
                                }
                                // setActionRefresh(false);
                                super.onPostExecute(resultToken);
                            }
                        }.execute(usableAccounts.get(item).name);
                    }

                }).show();

    }

    private void finishLogin(Intent intent) {
        AccountManager mAccountManager = AccountManager.get(this);
        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));
        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, null);
            mAccountManager.setAuthToken(account, AUTH_TOKEN_TYPE, authtoken);
        } else {
            mAccountManager.setPassword(account, accountPassword);
        }
        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }
    // public void onSaveClick(View v) {
    // TextView tvUsername;
    // TextView tvPassword;
    // TextView tvApiKey;
    // String username;
    // String password;
    // String apiKey;
    // boolean hasErrors = false;
    //
    // rkgroundColor(Color.WHITE);
    //
    // username = tvUsername.getText().toString();
    // password = tvPassword.getText().toString();
    // apiKey = tvApiKey.getText().toString();
    //
    // if (username.length() < 3) {
    // hasErrors = true;
    // tvUsername.setBackgroundColor(Color.MAGENTA);
    // }
    // if (password.length() < 3) {
    // hasErrors = true;
    // tvPassword.setBackgroundColor(Color.MAGENTA);
    // }
    // if (apiKey.length() < 3) {
    // hasErrors = true;
    // tvApiKey.setBackgroundColor(Color.MAGENTA);
    // }
    //
    // if (hasErrors) {
    // return;
    // }
    //
    // // Now that we have done some simple "client side" validation it
    // // is time to check with the server
    //
    // // ... perform some network activity here
    //
    // // finished
    //
    // String accountType = this.getIntent().getStringExtra(PARAM_AUTHTOKEN_TYPE);
    // if (accountType == null) {
    // accountType = AccountAuthenticator.ACCOUNT_TYPE;
    // }
    //
    // AccountManager accMgr = AccountManager.get(this);
    //
    // if (hasErrors) {
    //
    // // handel errors
    //
    // } else {
    //
    // // This is the magic that addes the account to the Android Account Manager
    // final Account account = new Account(username, accountType);
    // accMgr.addAccountExplicitly(account, password, null);
    //
    // // Now we tell our caller, could be the Andreoid Account Manager or even our own application
    // // that the process was successful
    //
    // final Intent intent = new Intent();
    // intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, username);
    // intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType);
    // intent.putExtra(AccountManager.KEY_AUTHTOKEN, accountType);
    // this.setAccountAuthenticatorResult(intent.getExtras());
    // this.setResult(RESULT_OK, intent);
    // this.finish();
    //
    // }
    // }

}
