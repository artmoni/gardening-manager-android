package org.gots.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.provider.google.GoogleAuthentication;
import org.gots.authentication.provider.nuxeo.NuxeoAuthentication;
import org.gots.broadcast.BroadCastMessages;
import org.gots.provider.ActionsContentProvider;
import org.gots.provider.GardenContentProvider;
import org.gots.provider.SeedsContentProvider;
import org.gots.provider.WeatherContentProvider;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class LoginDialogFragment extends AbstractDialogFragment {
    protected static final String TAG = "LoginActivity";

    protected int AUTHTOKEN_CODE_RESULT = 1;

    Account selectedAccount = null;

    private View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.login, container, false);
        if (gotsPrefs.isConnectedToServer()) {
            getDialog().setTitle(R.string.login_connect_state);
            buildLayoutConnected();
        } else {
            getDialog().setTitle(R.string.login_disconnect_state);
            buildLayoutDisconnected();
        }

        return v;
    }

    // public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context context, Intent intent) {
    // if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())) {
    // refreshConnectionState();
    // }
    // }
    //
    // };
    //
    // protected void refreshConnectionState() {
    // if (mMenu == null)
    // return;
    // MenuItem connectionItem = mMenu.findItem(R.id.connection);
    // if (gotsPrefs.isConnectedToServer()) {
    // connectionItem.setIcon(getResources().getDrawable(R.drawable.garden_connected));
    // } else {
    // connectionItem.setIcon(getResources().getDrawable(R.drawable.garden_disconnected));
    //
    // }
    // }

    // @Override
    // protected void onResume() {
    // super.onResume();
    //
    // // hide keyboard
    // InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    // if (this.getCurrentFocus() != null) {
    // inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
    // InputMethodManager.HIDE_NOT_ALWAYS);
    // }
    //
    // if (gotsPrefs.isConnectedToServer()) {
    // buildLayoutConnected();
    // return;
    // }
    //
    // buildLayoutDisconnected();
    //
    // }

    @Override
    public void onDestroy() {
        // unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();

    }

    public List<String> getAccounts(String account_type) {
        AccountManager manager = (AccountManager) getActivity().getSystemService(FragmentActivity.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        List<String> accountString = new ArrayList<String>();
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].type.equals(account_type))
                accountString.add(accounts[i].name);
        }

        return accountString;
    }

    protected void buildLayoutDisconnected() {
        v.findViewById(R.id.idLayoutOAuthDisconnect).setVisibility(View.GONE);

        View buttonLayout = (View) v.findViewById(R.id.idLayoutOAuthGoogle);
        buttonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(LoginActivity.this, getResources().getString(R.string.feature_unavalaible),
                // Toast.LENGTH_SHORT).show();
                // GoogleAnalyticsTracker.getInstance().trackEvent("Login", "GoogleAuthentication",
                // "Request this new feature", 0);
                // final OAuth oauth = new OAuth(getActivity());
                // oauth.initialize("hddzfo5DILozOV03icYS1XUpnnI");
                // oauth.popup("google", new OAuthCallback() {
                //
                // @Override
                // public void onFinished(OAuthData oAuthData) {
                //
                // Log.w(TAG, oAuthData.toString());
                // if ("success".equals(oAuthData.status)) {
                // // serverConnection.execute(oAuthData.token);
                // StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
                //
                // oAuthData.http("/plus/v1/people/me", new OAuthRequest() {
                // private URL url;
                //
                // private URLConnection con;
                //
                // @Override
                // public void onSetURL(String _url) {
                // try {
                // url = new URL(_url);
                // con = url.openConnection();
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                //
                // @Override
                // public void onSetHeader(String header, String value) {
                // con.addRequestProperty(header, value);
                //
                // }
                //
                // @Override
                // public void onReady() {
                // try {
                // BufferedReader r = new BufferedReader(new InputStreamReader(
                // con.getInputStream()));
                // StringBuilder total = new StringBuilder();
                // String line;
                // while ((line = r.readLine()) != null) {
                // total.append(line);
                // }
                // JSONObject result = new JSONObject(total.toString());
                // } catch (Exception e) {
                // e.printStackTrace();
                // }
                // }
                //
                // });
                //
                // }
                // }
                // });
                selectAccount("com.google");

                // selectAccount();
                GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Request account", 0);

                // tokenNuxeoConnect();

                // finish();

            }

        });

        View buttonLayoutFacebook = (View) v.findViewById(R.id.idLayoutOAuthFacebook);
        buttonLayoutFacebook.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(LoginActivity.this, getResources().getString(R.string.feature_unavalaible),
                // Toast.LENGTH_SHORT).show();
                // GoogleAnalyticsTracker.getInstance().trackEvent("Login", "GoogleAuthentication",
                // "Request this new feature", 0);
                // final OAuth oauth = new OAuth(getActivity());
                // oauth.initialize("hddzfo5DILozOV03icYS1XUpnnI");
                // oauth.popup("facebook", new OAuthCallback() {
                //
                // @Override
                // public void onFinished(OAuthData oAuthData) {
                //
                // Log.w(TAG, oAuthData.toString());
                // }
                // });

                // selectAccount("com.facebook.auth.login");

                GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Request facebook", 0);
                Toast.makeText(getActivity(), getResources().getString(R.string.feature_unavalaible),
                        Toast.LENGTH_SHORT).show();
                ;
                // tokenNuxeoConnect();

                // finish();

            }

        });

    }

    protected void buildLayoutConnected() {

        v.findViewById(R.id.idLayoutOAuthGoogle).setVisibility(View.GONE);
        v.findViewById(R.id.idLayoutOAuthFacebook).setVisibility(View.GONE);

        View buttonDisconnect = (View) v.findViewById(R.id.idLayoutOAuthDisconnect);
        buttonDisconnect.setVisibility(View.VISIBLE);
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                disconnect();
                getActivity().sendBroadcast(new Intent(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
                getDialog().dismiss();
            }

        });
    }

    protected void disconnect() {
        AccountManager manager = (AccountManager) getActivity().getSystemService(FragmentActivity.ACCOUNT_SERVICE);
        manager.invalidateAuthToken("com.google", gotsPrefs.getToken());
        gotsPrefs.setNuxeoLogin(null);
        gotsPrefs.setNuxeoPassword("");
        gotsPrefs.setConnectedToServer(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AUTHTOKEN_CODE_RESULT == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                requestOAuth2Token(selectedAccount);
                getDialog().dismiss();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void requestOAuth2Token(final Account account) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {

                // GotsSocialAuthentication authentication = new GoogleAuthentication(getActivity());
                GotsSocialAuthentication authentication = new GoogleAuthentication(getActivity());

                String authToken = null;
                String nuxeoToken = null;
                String accountName = params[0];
                try {
                    authToken = authentication.getToken(accountName);
                    if (authToken != null) {
                        NuxeoAuthentication nuxeoAuthentication = new NuxeoAuthentication(getActivity());
                        nuxeoToken = nuxeoAuthentication.request_oauth2_token(authToken);
                    }
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), AUTHTOKEN_CODE_RESULT);
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
                    gotsPrefs.setNuxeoLogin(account.name);
                    gotsPrefs.setToken(resultToken);
                    gotsPrefs.setConnectedToServer(true);
                    if (isAdded())
                        Toast.makeText(
                                getActivity(),
                                getResources().getString(R.string.login_connect_description).replace("_ACCOUNT_",
                                        gotsPrefs.getNuxeoLogin()), Toast.LENGTH_LONG).show();
                    // onResume();
                    Account newAccount = gotsPrefs.getUserAccount();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

                    ContentResolver.setSyncAutomatically(newAccount, SeedsContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, SeedsContentProvider.AUTHORITY, bundle);
                    ContentResolver.setSyncAutomatically(newAccount, GardenContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, GardenContentProvider.AUTHORITY, bundle);
                    ContentResolver.setSyncAutomatically(newAccount, ActionsContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, ActionsContentProvider.AUTHORITY, bundle);
                    ContentResolver.setSyncAutomatically(newAccount, WeatherContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, WeatherContentProvider.AUTHORITY, bundle);
                    getDialog().dismiss();
                } else {
                    if (isAdded())
                        Toast.makeText(getActivity(), "Please check your internet connection or try later",
                                Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(resultToken);
            }
        }.execute(account.name);
    }

    void selectAccount(String accountType) {
        AccountManager manager = (AccountManager) getActivity().getSystemService(FragmentActivity.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        final List<Account> usableAccounts = new ArrayList<Account>();
        List<String> items = new ArrayList<String>();
        for (Account account : accounts) {
            if (account.type.equals(accountType)) {
                usableAccounts.add(account);
                items.add(String.format("%s (%s)", account.name, account.type));
            }
        }
        if (usableAccounts.size() > 1)
            new AlertDialog.Builder(getActivity()).setTitle("Action").setItems(items.toArray(new String[items.size()]),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            selectedAccount = usableAccounts.get(item);
                            requestOAuth2Token(selectedAccount);
                        }

                    }).show();
        else if (usableAccounts.size() == 1 && usableAccounts.get(0) != null) {
            selectedAccount = usableAccounts.get(0);
            requestOAuth2Token(usableAccounts.get(0));
        }

    }

    // protected void getRefreshAccessToken(String token) {
    // new AsyncTask<String, Void, String>() {
    // @Override
    // protected String doInBackground(String... params) {
    // try {
    // HttpClient httpclient = new DefaultHttpClient();
    // HttpPost httppost = new HttpPost("https://accounts.google.com/o/oauth2/token");
    // httppost.setHeader("Content-type", "application/x-www-form-urlencoded");
    // // Add your data
    // List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    // nameValuePairs.add(new BasicNameValuePair("code", params[0]));
    // nameValuePairs.add(new BasicNameValuePair("client_id", CLIENT_ID));
    // nameValuePairs.add(new BasicNameValuePair("client_secret", CLIENT_SECRET));
    // nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
    //
    // UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nameValuePairs);
    // httppost.setEntity(entity);
    // HttpResponse response = httpclient.execute(httppost);
    // StatusLine serverCode = response.getStatusLine();
    // int code = serverCode.getStatusCode();
    // if (code == 200) {
    // InputStream is = response.getEntity().getContent();
    // JSONArray jsonArray = new JSONArray(convertStreamToString(is));
    // String refreshToken = (String) jsonArray.opt(4);
    // String accessToken = (String) jsonArray.opt(0);
    // return accessToken;
    // // bad token, invalidate and get a new one
    // } else if (code == 401) {
    // GoogleAuthUtil.invalidateToken(LoginActivity.this, params[0]);
    // Log.e(TAG, "Server auth error: " + response.getStatusLine());
    // return null;
    // // unknown error, do something else
    // } else {
    // InputStream is = response.getEntity().getContent();
    // String error = convertStreamToString(is);
    // Log.e("Server returned the following error code: " + serverCode, "");
    // return null;
    // }
    // } catch (MalformedURLException e) {
    // } catch (IOException e) {
    // } catch (JSONException e) {
    // } finally {
    // }
    // return null;
    // }
    //
    // @Override
    // protected void onPostExecute(String accessToken) {
    // Log.d("AccessToken", " " + accessToken);
    // }
    // }.execute(token);
    // }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // MenuInflater inflater = getSupportMenuInflater();
    // inflater.inflate(R.menu.menu_login, menu);
    // mMenu = menu;
    // return super.onCreateOptionsMenu(menu);
    // }

    // @Override
    // public boolean onPrepareOptionsMenu(Menu menu) {
    // refreshConnectionState();
    // return super.onPrepareOptionsMenu(menu);
    // }
}
