package org.gots.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.authentication.AuthenticationActivity;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.provider.google.GoogleAuthentication;
import org.gots.authentication.provider.nuxeo.NuxeoAuthentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

//import android.util.Base64;

public class FirstLaunchActivity extends AbstractActivity {
    private String TAG = "FirstLaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_launch);

        ActionBar bar = getSupportActionBar();
        // bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.app_name);

//        requestToken();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button buttonCreateProfile = (Button) findViewById(R.id.buttonCreate);
        buttonCreateProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstLaunchActivity.this, ProfileCreationActivity.class);
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

                        new AsyncTask<String, Integer, String>() {

                            protected void onPreExecute() {
                                setProgressRefresh(true);
                                findViewById(R.id.textViewError).setVisibility(View.GONE);

                            };

                            @Override
                            protected String doInBackground(String... params) {
                                GotsSocialAuthentication authentication = new GoogleAuthentication(
                                        getApplicationContext());
                                String googleToken = null;
                                String nuxeoToken = null;
                                try {
                                    googleToken = authentication.getToken(params[0]);
                                    if (googleToken != null) {
                                        NuxeoAuthentication nuxeoAuthentication = new NuxeoAuthentication(
                                                getApplicationContext());
                                        nuxeoToken = nuxeoAuthentication.request_oauth2_token(googleToken);
                                    }
                                } catch (UserRecoverableAuthException e) {
                                    startActivityForResult(e.getIntent(), 0);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (GoogleAuthException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }
                                return nuxeoToken;
                            }

                            @Override
                            protected void onPostExecute(String resultToken) {
                                if (resultToken != null) {
                                    gotsPrefs.setNuxeoLogin(usableAccounts.get(item).name);
                                    gotsPrefs.setToken(resultToken);
                                    gotsPrefs.setConnectedToServer(true);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getResources().getString(R.string.login_connect_description).replace(
                                                    "_ACCOUNT_", gotsPrefs.getNuxeoLogin()), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(FirstLaunchActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // Toast.makeText(getApplicationContext(),
                                    // "Error requesting GoogleAuthUtil.getToken",
                                    // Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.textViewError).setVisibility(View.VISIBLE);
                                }
                                setProgressRefresh(false);
                                super.onPostExecute(resultToken);
                            }
                        }.execute(usableAccounts.get(item).name);
                    }

                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (requestCode == 2) {
        // Intent intent = new Intent(FirstLaunchActivity.this, DashboardActivity.class);
        // startActivity(intent);
        // }
        if (gotsPrefs.isConnectedToServer() || gotsPrefs.getCurrentGardenId() > -1)
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_firstlaunch, menu);
        // refreshConnectionState();
        return super.onCreateOptionsMenu(menu);
    }

    private static final int ACCOUNT_CODE = 1601;

    private void chooseAccount() {
        // use https://github.com/frakbot/Android-AccountChooser for
        // compatibility with older devices
        Intent intent = AccountManager.newChooseAccountIntent(null, null, new String[] { "gardening-manager" }, false,
                null, null, null, null);
        startActivityForResult(intent, ACCOUNT_CODE);
    }

    private void requestToken() {
        Account userAccount = null;
        String user = gotsPrefs.getNuxeoLogin();
        AccountManager accountManager = AccountManager.get(this);
        for (Account account : accountManager.getAccountsByType("gardening-manager")) {
            if (account.name.equals(user)) {
                userAccount = account;

                break;
            }
        }

        accountManager.getAuthToken(userAccount, AuthenticationActivity.AUTH_TOKEN_TYPE, null, this,
                new OnTokenAcquired(), null);
    }

    private static final int AUTHORIZATION_CODE = 1993;

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {

        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                Bundle bundle = result.getResult();

                Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
                if (launch != null) {
                    startActivityForResult(launch, AUTHORIZATION_CODE);
                } else {
                    String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);

                    gotsPrefs.setToken(token);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
