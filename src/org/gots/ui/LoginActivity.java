package org.gots.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.AsyncLoadTasks;
import org.gots.utils.ClientCredentials;
import org.gots.utils.GoogleKeyInitializer;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Base64;
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
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.accounts.GoogleAccountManager;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.tasks.Tasks;

//import android.util.Base64;

public class LoginActivity extends AbstractActivity {

    // This must be the exact string, and is a special for alias OAuth 2 scope
    // "https://www.googleapis.com/auth/tasks"
    private static final String AUTH_TOKEN_TYPE = "Manage your tasks";

    private static final int REQUEST_AUTHENTICATE = 0;

    final HttpTransport transport = AndroidHttp.newCompatibleTransport();

    static final String PREF_AUTH_TOKEN = "authToken";

    GoogleAccountManager accountManager;

    SharedPreferences settings;

    String accountName;

    private boolean received401;

    protected static final String TAG = "LoginActivity";

    private Spinner loginSpinner;

    private TextView passwordText;

    private ActionBar bar;

    GoogleCredential credential;

    public Tasks service;

    private static final String PREF_ACCOUNT_NAME = "accountName";

    final JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        this.registerReceiver(seedBroadcastReceiver, new IntentFilter(
                BroadCastMessages.CONNECTION_SETTINGS_CHANGED));

        bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.app_name);
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())) {
                refreshConnectionState();
            }
        }

    };

    private Menu mMenu;

    protected void refreshConnectionState() {
        if (mMenu == null)
            return;
        MenuItem connectionItem = (MenuItem) mMenu.findItem(R.id.connection);
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
            return;
        }

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
        // loginText = (TextView) findViewById(R.id.edittextLogin);
        // loginText.setText(gotsPrefs.getLastSuccessfulNuxeoLogin());
        passwordText = (TextView) findViewById(R.id.edittextPassword);
        passwordText.setText(gotsPrefs.getNuxeoPassword());
        // gotsPrefs.setNuxeoLogin(null);

        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.idLayoutConnection);
        buttonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,
                        getResources().getString(R.string.feature_unavalaible),
                        Toast.LENGTH_SHORT).show();
                GoogleAnalyticsTracker.getInstance().trackEvent("Login",
                        "GoogleAuthentication", "Request this new feature", 0);

                launchGoogle();
                // tokenNuxeoConnect();

                // finish();

            }

        });

        Button buttoncreate = (Button) findViewById(R.id.buttonCreate);
        buttoncreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
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
                if (basicNuxeoConnect(login, password)) {

                    try {
                        nuxeoManager.shutdown();
                        session = nuxeoManager.getSession();

                        if ("Guest".equals(session.getLogin().getUsername())) {
                            return null;
                        }
                    } catch (Exception nao) {
                        if (nao != null) {
                            Log.e(TAG, "" + nao.getMessage());
                            Log.d(TAG, "" + nao.getMessage(), nao);
                        }
                        cancel(true);
                    }
                } else
                    cancel(true);
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

                } else {
                    LoginActivity.this.findViewById(R.id.textConnectError).setVisibility(
                            View.GONE);
                    gotsPrefs.setConnectedToServer(true);
                    gotsPrefs.setLastSuccessfulNuxeoLogin(login);
                    gardenManager.getMyGardens(true);
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

        // finish();
    }

    protected void disconnect() {
        request_basicauth_token(gotsPrefs.getNuxeoLogin(),
                gotsPrefs.getNuxeoPassword(), true);
        gotsPrefs.setNuxeoLogin(null);
        gotsPrefs.setNuxeoPassword("");
        gotsPrefs.setConnectedToServer(false);
        findViewById(R.id.layoutConnect).setVisibility(View.VISIBLE);
        findViewById(R.id.layoutDisconnect).setVisibility(View.GONE);
        onResume();
    }

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

    void launchGoogle() {
        // Google Accounts
        ClientCredentials.errorIfNotSpecified();
        service = com.google.api.services.tasks.Tasks.builder(transport,
                jsonFactory).setApplicationName("Google-TasksAndroidSample/1.0").setHttpRequestInitializer(
                credential).setJsonHttpRequestInitializer(
                new GoogleKeyInitializer(ClientCredentials.KEY)).build();
        settings = getPreferences(MODE_PRIVATE);
        accountName = settings.getString(PREF_ACCOUNT_NAME, null);
        credential.setAccessToken(settings.getString(PREF_AUTH_TOKEN, null));
        accountManager = new GoogleAccountManager(this);
        gotAccount();

    }

    void gotAccount() {
        Account account = accountManager.getAccountByName(accountName);
        if (account == null) {
            chooseAccount();
            return;
        }
        if (credential.getAccessToken() != null) {
            onAuthToken();
            return;
        }
        accountManager.getAccountManager().getAuthToken(account,
                AUTH_TOKEN_TYPE, true, new AccountManagerCallback<Bundle>() {

                    public void run(AccountManagerFuture<Bundle> future) {
                        try {
                            Bundle bundle = future.getResult();
                            if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                                Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                                intent.setFlags(intent.getFlags()
                                        & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivityForResult(intent,
                                        REQUEST_AUTHENTICATE);
                            } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                                setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                                onAuthToken();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, null);
    }

    private void chooseAccount() {
        accountManager.getAccountManager().getAuthTokenByFeatures(
                GoogleAccountManager.ACCOUNT_TYPE, AUTH_TOKEN_TYPE, null,
                LoginActivity.this, null, null,
                new AccountManagerCallback<Bundle>() {

                    public void run(AccountManagerFuture<Bundle> future) {
                        Bundle bundle;
                        try {
                            bundle = future.getResult();
                            setAccountName(bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
                            setAuthToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
                            onAuthToken();
                        } catch (OperationCanceledException e) {
                            // user canceled
                        } catch (AuthenticatorException e) {
                            Log.e(TAG, e.getMessage(), e);
                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage(), e);
                        }
                    }
                }, null);
    }

    void setAccountName(String accountName) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_ACCOUNT_NAME, accountName);
        editor.commit();
        this.accountName = accountName;
    }

    void setAuthToken(String authToken) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(PREF_AUTH_TOKEN, authToken);
        editor.commit();
        credential.setAccessToken(authToken);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case REQUEST_AUTHENTICATE:
            if (resultCode == RESULT_OK) {
                gotAccount();
            } else {
                chooseAccount();
            }
            break;
        }
    }

    void onAuthToken() {
        new AsyncLoadTasks(this).execute();
    }

    public void onRequestCompleted() {
        received401 = false;
    }

    public void handleGoogleException(IOException e) {
        if (e instanceof GoogleJsonResponseException) {
            GoogleJsonResponseException exception = (GoogleJsonResponseException) e;
            if (exception.getStatusCode() == 401 && !received401) {
                received401 = true;
                accountManager.invalidateAuthToken(credential.getAccessToken());
                credential.setAccessToken(null);
                SharedPreferences.Editor editor2 = settings.edit();
                editor2.remove(PREF_AUTH_TOKEN);
                editor2.commit();
                gotAccount();
                return;
            }
        }
        Log.e(TAG, e.getMessage(), e);
    }

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
