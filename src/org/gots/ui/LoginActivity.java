package org.gots.ui;

import java.io.IOException;
<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

=======
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
>>>>>>> 486e08bac9d53078b0bc7c8d3448e5dcc6cb8c11
import org.gots.R;
import org.gots.authentication.GoogleAuthentication;
import org.gots.authentication.NuxeoAuthentication;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;
<<<<<<< HEAD
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
=======
>>>>>>> 486e08bac9d53078b0bc7c8d3448e5dcc6cb8c11

public class LoginActivity extends AbstractActivity {
    protected static final String TAG = "LoginActivity";

    private Spinner loginSpinner;

    private TextView passwordText;

    private ActionBar bar;

<<<<<<< HEAD
    private Menu mMenu;

    private NuxeoAuthentication nuxeoAuthentication;

    protected int AUTHTOKEN_CODE_RESULT = 1;

    Account selectedAccount = null;
=======
    private String gname;
>>>>>>> 486e08bac9d53078b0bc7c8d3448e5dcc6cb8c11

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.registerReceiver(seedBroadcastReceiver, new IntentFilter(
                BroadCastMessages.CONNECTION_SETTINGS_CHANGED));

        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.app_name);
        nuxeoAuthentication = new NuxeoAuthentication(this);

        // credential = GoogleAccountCredential.usingOAuth2(context, scopes)
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())) {
                refreshConnectionState();
            }
        }

    };

    protected void refreshConnectionState() {
        if (mMenu == null)
            return;
        MenuItem connectionItem = mMenu.findItem(R.id.connection);
        if (gotsPrefs.isConnectedToServer()) {
            connectionItem.setIcon(getResources().getDrawable(
                    R.drawable.garden_connected));
        } else {
            connectionItem.setIcon(getResources().getDrawable(
                    R.drawable.garden_disconnected));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus() != null) {
            inputManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if (gotsPrefs.isConnectedToServer()) {
            buildLayoutConnected();
        } else
            buildLayoutDisconnected();

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();

    }

    public List<String> getAccounts(String account_type) {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        List<String> accountString = new ArrayList<String>();
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i].type.equals(account_type))
                accountString.add(accounts[i].name);
        }

        return accountString;
    }

    protected void buildLayoutDisconnected() {

        if (GotsPreferences.isDevelopment()) {
            findViewById(R.id.tableDebug).setVisibility(View.VISIBLE);

        }
        loginSpinner = (Spinner) findViewById(R.id.spinnerLogin);
        ArrayAdapter<String> account_name_adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                getAccounts("com.google"));
        loginSpinner.setAdapter(account_name_adapter);
        passwordText = (TextView) findViewById(R.id.edittextPassword);
        passwordText.setText(gotsPrefs.getNuxeoPassword());

        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.idLayoutOAuth2);
        buttonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Toast.makeText(LoginActivity.this,
                // getResources().getString(R.string.feature_unavalaible),
                // Toast.LENGTH_SHORT).show();
                // GoogleAnalyticsTracker.getInstance().trackEvent("Login",
                // "GoogleAuthentication",
                // "Request this new feature", 0);

                selectAccount();
                // tokenNuxeoConnect();

                // finish();

            }

        });

        Button buttoncreate = (Button) findViewById(R.id.buttonCreate);
        buttoncreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
<<<<<<< HEAD
                GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Request account", 0);
                sendEmail();
=======
                GoogleAnalyticsTracker.getInstance().trackEvent("Login",
                        "SimpleAuthentication", "Request account", 0);

                // send mail
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("message/rfc822");
                i.putExtra(Intent.EXTRA_EMAIL,
                        new String[] { "account@gardening-manager.com" });
                i.putExtra(Intent.EXTRA_SUBJECT,
                        "Gardening Manager / Account / Ask for new account");
                i.putExtra(
                        Intent.EXTRA_TEXT,
                        "Hello,\n\nI want to participate to the Gardening Manager beta version.\n\nMy Google account is: "
                                + loginSpinner.getSelectedItem().toString()
                                + "\n\nI know I will receive my password quickly.\n\n");
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(LoginActivity.this,
                            "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }

>>>>>>> 486e08bac9d53078b0bc7c8d3448e5dcc6cb8c11
            }

        });

        Button connect = (Button) findViewById(R.id.buttonConnect);
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                connect();

            }

        });
    }

    protected void sendEmail() {
        // send mail
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[] { "account@gardening-manager.com" });
        i.putExtra(Intent.EXTRA_SUBJECT, "Gardening Manager / Account / Ask for new account");
        i.putExtra(Intent.EXTRA_TEXT,
                "Hello,\n\nI want to participate to the Gardening Manager beta version.\n\nMy Google account is: "
                        + loginSpinner.getSelectedItem().toString()
                        + "\n\nI know I will receive my password quickly.\n\n");
        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(LoginActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    protected void buildLayoutConnected() {
        findViewById(R.id.layoutConnect).setVisibility(View.GONE);
        View disconnectLayout = findViewById(R.id.layoutDisconnect);
        disconnectLayout.setVisibility(View.VISIBLE);

        Button buttonDisconnect = (Button) findViewById(R.id.buttonDisconnect);
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                disconnect();
            }

        });
    }

    protected void connect() {
        new AsyncTask<Void, Integer, Session>() {
            private ProgressDialog dialog;

            private String login;

            private String password;

            @Override
            protected void onPreExecute() {

                login = String.valueOf(loginSpinner.getSelectedItem());
                if (GotsPreferences.isDevelopment()) {
                    EditText logindebug = (EditText) findViewById(R.id.edittextLoginDebug);
                    login = logindebug.getText().toString();
                }
                password = passwordText.getText().toString();

                if ("".equals(login) || "".equals(password)) {
                    Toast.makeText(
                            LoginActivity.this,
                            getResources().getString(
                                    R.string.login_missinginformation),
                            Toast.LENGTH_SHORT).show();
                    cancel(true);
                } else {
                    dialog = ProgressDialog.show(LoginActivity.this, "",
                            getResources().getString(R.string.gots_loading),
                            true);
                    dialog.setCanceledOnTouchOutside(true);
                    // dialog.show();
                }
            };

            @Override
            protected Session doInBackground(Void... params) {
                Session session = null;
                try {
                    if (nuxeoAuthentication.basicNuxeoConnect(login, password)) {

                        nuxeoManager.shutdown();
                        session = nuxeoManager.getSession();

                        if ("Guest".equals(session.getLogin().getUsername())) {
                            return null;
                        }
                    } else
                        cancel(true);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                    cancel(true);
                }
                return session;
            }

            @Override
            protected void onPostExecute(Session result) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
                if (result == null) {
                    Toast.makeText(LoginActivity.this, "Error logging",
                            Toast.LENGTH_SHORT).show();
                    LoginActivity.this.findViewById(R.id.textConnectError).setVisibility(
                            View.VISIBLE);
                    gotsPrefs.setConnectedToServer(false);
                    gotsPrefs.setNuxeoLogin(null);
                    gotsPrefs.setLastSuccessfulNuxeoLogin(null);
                    GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Failure", 0);

                } else {
                    LoginActivity.this.findViewById(R.id.textConnectError).setVisibility(
                            View.GONE);
                    gotsPrefs.setConnectedToServer(true);
                    gotsPrefs.setLastSuccessfulNuxeoLogin(login);
                    gardenManager.getMyGardens(true);
                    GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Success", 0);
                }

                onResume();
            };

            @Override
            protected void onCancelled(Session result) {
                if (dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }

        }.execute();
        onResume();
    }

    protected void disconnect() {
        gotsPrefs.setNuxeoLogin(null);
        gotsPrefs.setNuxeoPassword("");
        gotsPrefs.setConnectedToServer(false);
        findViewById(R.id.layoutConnect).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutDisconnect).setVisibility(View.GONE);
        onResume();
    }

<<<<<<< HEAD
=======
    protected boolean basicNuxeoConnect(String login, String password) {
        String device_id = getDeviceID();
        gotsPrefs.setDeviceId(device_id);

        String token = request_basicauth_token(login, password, false);
        if (token == null) {
            return false;
        } else {
            gotsPrefs.setNuxeoLogin(login);
            gotsPrefs.setNuxeoPassword(password);
            gotsPrefs.setToken(token);
            return true;
        }
    }

    protected String getDeviceID() {
        String device_id = Secure.getString(getContentResolver(),
                Secure.ANDROID_ID);
        return device_id;
    }

    // TODO currently not used
    protected void tokenNuxeoConnect() {
        String device_id = getDeviceID();
        gotsPrefs.setDeviceId(device_id);

        String tmp_token = request_temporaryauth_token(false);
        if (tmp_token == null) {
            Toast.makeText(this, "Authentication ", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, tmp_token, Toast.LENGTH_SHORT).show();
        }
    }

    // TODO currently not used
    public String request_temporaryauth_token(boolean revoke) {

        AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {
            String token = null;

            @Override
            protected String doInBackground(Object... objects) {
                try {
                    String email = "toto.tata@gmail.com";
                    Session session = nuxeoManager.getSession();
                    Documents docs = (Documents) session.newRequest(
                            "Document.Email").setHeader(
                            Constants.HEADER_NX_SCHEMAS, "*").set("email",
                            email).execute();

                    // String uri =
                    // GotsPreferences.getInstance(getApplicationContext())
                    // .getGardeningManagerNuxeoAuthentication();
                    //
                    // List<NameValuePair> params = new
                    // LinkedList<NameValuePair>();
                    // params.add(new BasicNameValuePair("deviceId",
                    // GotsPreferences.getInstance(getApplicationContext())
                    // .getDeviceId()));
                    // params.add(new BasicNameValuePair("applicationName",
                    // GotsPreferences.getInstance(
                    // getApplicationContext()).getGardeningManagerAppname()));
                    // params.add(new BasicNameValuePair("deviceDescription",
                    // Build.MODEL + "(" + Build.MANUFACTURER +
                    // ")"));
                    // params.add(new BasicNameValuePair("permission",
                    // "ReadWrite"));
                    // params.add(new BasicNameValuePair("revoke", "false"));
                    //
                    // String paramString = URLEncodedUtils.format(params,
                    // "utf-8");
                    // uri += paramString;
                    // URL url = new URL(uri);
                    //
                    // URLConnection urlConnection;
                    // urlConnection = url.openConnection();
                    //
                    // urlConnection.addRequestProperty("X-User-Id",
                    // loginText.getText().toString());
                    // urlConnection.addRequestProperty("X-Device-Id",
                    // GotsPreferences
                    // .getInstance(getApplicationContext()).getDeviceId());
                    // urlConnection.addRequestProperty("X-Application-Name",
                    // GotsPreferences.getInstance(getApplicationContext()).getGardeningManagerAppname());
                    // urlConnection.addRequestProperty(
                    // "Authorization",
                    // "Basic "
                    // + Base64.encodeToString((loginText.getText().toString() +
                    // ":" + passwordText
                    // .getText().toString()).getBytes(), Base64.NO_WRAP));

                    // urlConnection.addRequestProperty(
                    // "Authorization",
                    // "Basic "
                    // + Base64.encodeBase64((loginText.getText().toString() +
                    // ":" + passwordText.getText()
                    // .toString()).getBytes()));

                    // InputStream in = new
                    // BufferedInputStream(urlConnection.getInputStream());
                    // try {
                    // // readStream(in);
                    // StringBuilder builder = new StringBuilder();
                    // String line;
                    // BufferedReader reader = new BufferedReader(new
                    // InputStreamReader(in, "UTF-8"));
                    // while ((line = reader.readLine()) != null) {
                    // builder.append(line);
                    // }
                    //
                    // token = builder.toString();
                    // Log.d("LoginActivity", "Token acquired: " + token);
                    //
                    // } finally {
                    // in.close();
                    // }
                } catch (IOException e) {
                    Log.e("LoginActivity", e.getMessage(), e);
                    return null;

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return token;
            }
        }.execute(new Object());
        String tokenAcquired = null;
        try {
            tokenAcquired = task.get();
        } catch (InterruptedException e) {
            Log.e("LoginActivity", e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e("LoginActivity", e.getMessage(), e);
        }
        return tokenAcquired;

    }

    public String request_basicauth_token(String login, String password,
            boolean revoke) {

        // AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void,
        // String>() {
        String token = null;

        // @Override
        // protected String doInBackground(Object... objects) {
        try {
            String uri = gotsPrefs.getNuxeoAuthenticationURI();

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("deviceId",
                    gotsPrefs.getDeviceId()));
            params.add(new BasicNameValuePair("applicationName",
                    gotsPrefs.getGardeningManagerAppname()));
            params.add(new BasicNameValuePair("deviceDescription", Build.MODEL
                    + "(" + Build.MANUFACTURER + ")"));
            params.add(new BasicNameValuePair("permission", "ReadWrite"));
            params.add(new BasicNameValuePair("revoke", String.valueOf(revoke)));

            String paramString = URLEncodedUtils.format(params, "utf-8");
            uri += paramString;
            URL url = new URL(uri);

            URLConnection urlConnection;
            urlConnection = url.openConnection();

            urlConnection.addRequestProperty("X-User-Id", login);
            urlConnection.addRequestProperty("X-Device-Id",
                    gotsPrefs.getDeviceId());
            urlConnection.addRequestProperty("X-Application-Name",
                    gotsPrefs.getGardeningManagerAppname());
            urlConnection.addRequestProperty(
                    "Authorization",
                    "Basic "
                            + Base64.encodeToString(
                                    (login + ":" + password).getBytes(),
                                    Base64.NO_WRAP));

            // TODO urlConnection.setConnectTimeout
            InputStream in = new BufferedInputStream(
                    urlConnection.getInputStream());
            try {
                // readStream(in);
                StringBuilder builder = new StringBuilder();
                String line;
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(in, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                token = builder.toString();
                Log.d("LoginActivity", "Token acquired: " + token);
                GoogleAnalyticsTracker.getInstance().trackEvent(
                        "Authentication", "Login", "Success", 0);

            } finally {
                in.close();
            }
        } catch (IOException e) {
            GoogleAnalyticsTracker.getInstance().trackEvent("Authentication",
                    "Login", "Failure", 0);
            Log.e(TAG, e.getMessage(), e);
        }
        return token;

    }

>>>>>>> 486e08bac9d53078b0bc7c8d3448e5dcc6cb8c11
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            finish();
            return true;
        case R.id.help:
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL,
                    getClass().getSimpleName());
            startActivity(browserIntent);
            return true;
        case R.id.connection:
            if (gotsPrefs.isConnectedToServer())
                disconnect();
            else
                connect();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AUTHTOKEN_CODE_RESULT == requestCode) {
            if (resultCode == Activity.RESULT_OK)
                requestOAuth2Token(selectedAccount);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void requestOAuth2Token(final Account account) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {

                GoogleAuthentication authentication = new GoogleAuthentication(getApplicationContext());
                String token = null;
                try {
                    token = authentication.getToken(params[0]);
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), AUTHTOKEN_CODE_RESULT);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (GoogleAuthException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                if (token != null)
                    authentication.getUserFriends(token, authentication.getUserID(token));
                return token;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null)
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(LoginActivity.this, "Error requesting GoogleAuthUtil.getToken", Toast.LENGTH_SHORT).show();

                super.onPostExecute(result);
            }
        }.execute(account.name);
    }

    void selectAccount() {
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
<<<<<<< HEAD
        if (usableAccounts.size() > 1)
            new AlertDialog.Builder(this).setTitle("Action").setItems(items.toArray(new String[items.size()]),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            selectedAccount = usableAccounts.get(item);
                            requestOAuth2Token(selectedAccount);
                        }

                    }).show();
        else if (usableAccounts.get(0) != null) {
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
=======
        new AlertDialog.Builder(this).setTitle("Action").setItems(
                items.toArray(new String[items.size()]),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        LoginActivity.this.gname = usableAccounts.get(item).name;
                        getAPIToken(LoginActivity.this.gname);
                    }
                }).show();

        // // if (isChecked) {
        // // loginBox.setVisibility(View.VISIBLE);
        // //
        // // // Create an instance of SocialAuthConfgi object
        // SocialAuthConfig config = SocialAuthConfig.getDefault();
        // //
        // // // load configuration. By default load the configuration
        // // // from oauth_consumer.properties.
        // // // You can also pass input stream, properties object or
        // // // properties file name.
        // try {
        // config.load();
        //
        // // Create an instance of SocialAuthManager and set
        // // config
        // SocialAuthManager manager = new SocialAuthManager();
        // manager.setSocialAuthConfig(config);
        //
        // // URL of YOUR application which will be called after
        // // authentication
        // String successUrl =
        // "http://srv2.gardening-manager.com:8090/nuxeo/nxstartup.faces?provider=GoogleOpenIDConnect";
        //
        // // get Provider URL to which you should redirect for
        // // authentication.
        // // id can have values "facebook", "twitter", "yahoo"
        // // etc. or the OpenID URL
        // String url = manager.getAuthenticationUrl("google", successUrl);
        //
        // // Store in session
        // // session.setAttribute("authManager", manager);
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    protected void getAPIToken(String gname) {
        new AsyncTask<String, Integer, String>() {
            @Override
            protected String doInBackground(String... params) {
                String token = null;
                try {
                    final String G_PLUS_SCOPE = "oauth2:https://www.googleapis.com/auth/plus.me";
                    final String USERINFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";
                    // final String SPECIAL_SCOPE =
                    // "oauth2:server:client_id:473239775303-4cf1omsjdf231kp7picdbaefcb0nnm1u.apps.googleusercontent.com:api_scope:https%3A%2F%2Fwww.googleapis.com/auth/plus.login";

                    final String SCOPES = G_PLUS_SCOPE;
                    token = GoogleAuthUtil.getToken(LoginActivity.this,
                            params[0], SCOPES);
                    // if (server indicates token is invalid) {
                    // // invalidate the token that we found is
                    // bad so that GoogleAuthUtil won't
                    // // return it next time (it may have
                    // cached it)
                    // GoogleAuthUtil.invalidateToken(Context,
                    // String)(context, token);
                    // // consider retrying
                    // getAndUseTokenBlocking() once more
                    // return;
                    // }

                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), 0);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (GoogleAuthException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return token;
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    getRefreshAccessToken(result);
                }
                /*
                 * if (result != null) Toast.makeText(LoginActivity.this,
                 * result, Toast.LENGTH_SHORT).show(); else
                 * Toast.makeText(LoginActivity.this,
                 * "Error requesting GoogleAuthUtil.getToken",
                 * Toast.LENGTH_SHORT).show(); ;
                 */
                super.onPostExecute(result);
            }
        }.execute(gname);
    }

    protected void getRefreshAccessToken(String token) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    HttpClient httpclient = new DefaultHttpClient();

                    // Add your data
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("code", params[0]));
                    nameValuePairs.add(new BasicNameValuePair("client_id",
                            "473239775303-4cf1omsjdf231kp7picdbaefcb0nnm1u.apps.googleusercontent.com"));
                    nameValuePairs.add(new BasicNameValuePair("client_secret",
                            "h-LjtDOOYBssYJu8onIl3IqB"));
//                    nameValuePairs.add(new BasicNameValuePair("redirect_uri",
//                            "urn:ietf:wg:oauth:2.0:oob"));
                    nameValuePairs.add(new BasicNameValuePair("grant_type",
                            "authorization_code"));
                    HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);
                    HttpPost httppost = new HttpPost(
                            "https://accounts.google.com/o/oauth2/token");
                    // ((UrlEncodedFormEntity)
                    // entity).setContentEncoding(HTTP.UTF_8);
                    ((UrlEncodedFormEntity) entity).setContentType("application/x-www-form-urlencoded");
                    // ((UrlEncodedFormEntity)
                    // entity).setContentType("application/x-www-form-urlencoded");
                    httppost.setEntity(entity);
                    // httppost.setHeader("Content-Type",
                    // "application/x-www-form-urlencoded");

                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    StatusLine serverCode = response.getStatusLine();
                    int code = serverCode.getStatusCode();
                    if (code == 200) {
                        InputStream is = response.getEntity().getContent();
                        JSONArray jsonArray = new JSONArray(
                                convertStreamToString(is));
                        String refreshToken = (String) jsonArray.opt(4);
                        String accessToken = (String) jsonArray.opt(0);
                        return accessToken;
                        // bad token, invalidate and get a new one
                    } else if (code == 401) {
                        GoogleAuthUtil.invalidateToken(LoginActivity.this,
                                params[0]);
                        Log.e(TAG,
                                "Server auth error: "
                                        + response.getStatusLine());
                        return null;
                        // unknown error, do something else
                    } else {
                        InputStream is = response.getEntity().getContent();
                        String error = convertStreamToString(is);
                        Log.e("Server returned the following error code: "
                                + serverCode, "");
                        return null;
                    }
                } catch (MalformedURLException e) {
                } catch (IOException e) {
                } catch (JSONException e) {
                } finally {
                }
                return null;
            }

            @Override
            protected void onPostExecute(String accessToken) {
                if (accessToken != null) {
                    Log.d("AccessToken", accessToken);
                }
            }
        }.execute(token);
    }

    protected String convertStreamToString(InputStream inputStream)
            throws IOException {
        if (inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                inputStream.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            getAPIToken(LoginActivity.this.gname);
        }
    }
>>>>>>> 486e08bac9d53078b0bc7c8d3448e5dcc6cb8c11

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_login, menu);
        mMenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        refreshConnectionState();
        return super.onPrepareOptionsMenu(menu);
    }
}
