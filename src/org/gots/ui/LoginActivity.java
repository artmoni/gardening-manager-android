package org.gots.ui;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.gots.R;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.util.Base64;
//import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;

public class LoginActivity extends AbstractActivity {
    protected static final String TAG = "LoginActivity";

    private TextView loginText;

    private TextView passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        //hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (this.getCurrentFocus()!=null) {            
            inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if (gotsPrefs.isConnectedToServer()) {
            buildLayoutConnected();
            return;
        }

        buildLayoutDisconnected();

    }

    protected void buildLayoutDisconnected() {
        loginText = (TextView) findViewById(R.id.edittextLogin);
        loginText.setText(gotsPrefs.getNuxeoLogin());
        passwordText = (TextView) findViewById(R.id.edittextPassword);
        passwordText.setText(gotsPrefs.getNuxeoPassword());

        LinearLayout buttonLayout = (LinearLayout) findViewById(R.id.idLayoutConnection);
        buttonLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.feature_unavalaible),
                        Toast.LENGTH_SHORT).show();

                // launchGoogle();
                // tokenNuxeoConnect();

                // finish();

            }

        });

        Button connect = (Button) findViewById(R.id.buttonConnect);
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, Session>() {
                    private ProgressDialog dialog;

                    private String login;

                    private String password;

                    @Override
                    protected void onPreExecute() {
                        login = loginText.getText().toString();
                        password = passwordText.getText().toString();

                        if ("".equals(login) || "".equals(password)) {
                            Toast.makeText(LoginActivity.this,
                                    getResources().getString(R.string.login_missinginformation), Toast.LENGTH_SHORT).show();
                            cancel(true);
                        }
                        dialog = ProgressDialog.show(LoginActivity.this, "", "Loading. Please wait...", true);
                        dialog.setCanceledOnTouchOutside(true);
                        dialog.show();
                    };

                    @Override
                    protected Session doInBackground(Void... params) {
                        Session session = null;
                        if (basicNuxeoConnect(login, password)) {

                            try {
                                session = nuxeoManager.getSession();

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
                        if (dialog.isShowing())
                            dialog.dismiss();
                        if (result == null) {
                            Toast.makeText(LoginActivity.this, "Error logging", Toast.LENGTH_SHORT).show();
                            LoginActivity.this.findViewById(R.id.textConnectError).setVisibility(View.VISIBLE);
                            gotsPrefs.setConnectedToServer(false);
                        } else {
                            LoginActivity.this.findViewById(R.id.textConnectError).setVisibility(View.GONE);
                            gotsPrefs.setConnectedToServer(true);
                        }
                        onResume();

                    };

                    @Override
                    protected void onCancelled(Session result) {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }

                }.execute();

                // finish();

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
                gotsPrefs.setConnectedToServer(false);
                findViewById(R.id.layoutConnect).setVisibility(View.VISIBLE);
                findViewById(R.id.layoutDisconnect).setVisibility(View.GONE);
                onResume();
            }
        });
    }

    protected boolean basicNuxeoConnect(String login, String password) {
        String device_id = getDeviceID();
        gotsPrefs.setDeviceId(device_id);

        String token = request_basicauth_token(false);
        if (token == null) {
            return false;
        } else {
            gotsPrefs.setToken(token);
            gotsPrefs.setNuxeoLogin(login);
            gotsPrefs.setNuxeoPassword(password);
            return true;
        }
    }

    protected String getDeviceID() {
        String device_id = Secure.getString(getContentResolver(), Secure.ANDROID_ID);
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
                    Documents docs = (Documents) session.newRequest("Document.Email").setHeader(
                            Constants.HEADER_NX_SCHEMAS, "*").set("email", email).execute();

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

    public String request_basicauth_token(boolean revoke) {

        // AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {
        String token = null;

        // @Override
        // protected String doInBackground(Object... objects) {
        try {
            String uri = gotsPrefs.getNuxeoAuthenticationURI();

            List<NameValuePair> params = new LinkedList<NameValuePair>();
            params.add(new BasicNameValuePair("deviceId", gotsPrefs.getDeviceId()));
            params.add(new BasicNameValuePair("applicationName", gotsPrefs.getGardeningManagerAppname()));
            params.add(new BasicNameValuePair("deviceDescription", Build.MODEL + "(" + Build.MANUFACTURER + ")"));
            params.add(new BasicNameValuePair("permission", "ReadWrite"));
            params.add(new BasicNameValuePair("revoke", "false"));

            String paramString = URLEncodedUtils.format(params, "utf-8");
            uri += paramString;
            URL url = new URL(uri);

            URLConnection urlConnection;
            urlConnection = url.openConnection();

            urlConnection.addRequestProperty("X-User-Id", loginText.getText().toString());
            urlConnection.addRequestProperty("X-Device-Id", gotsPrefs.getDeviceId());
            urlConnection.addRequestProperty("X-Application-Name", gotsPrefs.getGardeningManagerAppname());
            urlConnection.addRequestProperty(
                    "Authorization",
                    "Basic "
                            + Base64.encodeToString(
                                    (loginText.getText().toString() + ":" + passwordText.getText().toString()).getBytes(),
                                    Base64.NO_WRAP));

            // urlConnection.addRequestProperty(
            // "Authorization",
            // "Basic "
            // + Base64.encodeBase64((loginText.getText().toString() +
            // ":" + passwordText.getText()
            // .toString()).getBytes()));
            // TODO urlConnection.setConnectTimeout
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            try {
                // readStream(in);
                StringBuilder builder = new StringBuilder();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                token = builder.toString();
                Log.d("LoginActivity", "Token acquired: " + token);

            } finally {
                in.close();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return token;
        // }
        // }.execute(new Object());

        // String tokenAcquired = null;
        // try {
        // tokenAcquired = task.get();
        // } catch (InterruptedException e) {
        // Log.e("LoginActivity", e.getMessage(), e);
        // } catch (ExecutionException e) {
        // Log.e("LoginActivity", e.getMessage(), e);
        // }
        // return tokenAcquired;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            finish();
            return true;

            // case R.id.help:
            // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
            // Uri.parse(HelpUriBuilder.getUri(getClass()
            // .getSimpleName())));
            // startActivity(browserIntent);
            //
            // return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    void launchGoogle() {
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
        // String url = manager.getAuthenticationUrl("google",
        // successUrl);
        //
        // // Store in session
        // // session.setAttribute("authManager", manager);
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

}
