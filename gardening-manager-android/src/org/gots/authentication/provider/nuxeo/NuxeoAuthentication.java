package org.gots.authentication.provider.nuxeo;

import android.content.Context;
import android.os.Build;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.List;

public class NuxeoAuthentication {

    protected String TAG = "NuxeoAuthentication";

    GotsPreferences gotsPrefs;

    private Context mContext;

    public NuxeoAuthentication(Context context) {
        this.mContext = context;
        gotsPrefs = getGotsContext().getServerConfig();
    }

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    public String request_basicauth_token(String login, String password, boolean revoke) throws IOException {

        String token = null;

        String uri = gotsPrefs.getNuxeoAuthenticationURI();

        List<NameValuePair> params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("deviceId", gotsPrefs.getDeviceId()));
        params.add(new BasicNameValuePair("applicationName", gotsPrefs.getGardeningManagerAppname()));
        params.add(new BasicNameValuePair("deviceDescription", Build.MODEL + "(" + Build.MANUFACTURER + ")"));
        params.add(new BasicNameValuePair("permission", "ReadWrite"));
        params.add(new BasicNameValuePair("revoke", String.valueOf(revoke)));

        String paramString = URLEncodedUtils.format(params, "utf-8");
        uri += paramString;
        URL url = new URL(uri);

        URLConnection urlConnection;
        urlConnection = url.openConnection();

        urlConnection.addRequestProperty("X-User-Id", login);
        urlConnection.addRequestProperty("X-Device-Id", gotsPrefs.getDeviceId());
        urlConnection.addRequestProperty("X-Application-Name", gotsPrefs.getGardeningManagerAppname());
        urlConnection.addRequestProperty("Authorization",
                "Basic " + Base64.encodeToString((login + ":" + password).getBytes(), Base64.NO_WRAP));

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
            Log.d(TAG, "Token acquired: " + token);
            GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Success", 0);

        } finally {
            in.close();
        }
        return token;

    }

    public String basicNuxeoConnect(String login, String password) throws IOException {
        String device_id = getDeviceID();
        gotsPrefs.setDeviceId(device_id);
        String token = request_basicauth_token(login, password, false);
        if (token == null) {
            return null;
        } else {
            gotsPrefs.setNuxeoLogin(login);
            gotsPrefs.setNuxeoPassword(password);
            gotsPrefs.setToken(token);
            return token;
        }
    }

    protected String getDeviceID() {
        String device_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        return device_id;
    }

    /*
     * Request a Nuxeo Token providing an oAuth2Token Access
     */
    public String request_oauth2_token(String oAuth2Token) throws IOException {

        String device_id = getDeviceID();
        gotsPrefs.setDeviceId(device_id);
        String token = null;
        InputStream in = null;
        try {

            String uri = gotsPrefs.getNuxeoAuthenticationURI();

            List<NameValuePair> params = new LinkedList<NameValuePair>();

            params.add(new BasicNameValuePair("deviceId", gotsPrefs.getDeviceId()));
            params.add(new BasicNameValuePair("applicationName", gotsPrefs.getGardeningManagerAppname()));
            params.add(new BasicNameValuePair("deviceDescription", Build.MODEL + "(" + Build.MANUFACTURER + ")"));
            params.add(new BasicNameValuePair("permission", "ReadWrite"));
            params.add(new BasicNameValuePair("token", oAuth2Token));
            params.add(new BasicNameValuePair("provider", "GoogleOpenIDConnect"));

            // params.add(new BasicNameValuePair("revoke", String.valueOf(revoke)));

            String paramString = URLEncodedUtils.format(params, "utf-8");
            uri += paramString;
            URL url = new URL(uri);

            URLConnection urlConnection;
            urlConnection = url.openConnection();

            // urlConnection.addRequestProperty("X-User-Id", login);
            urlConnection.addRequestProperty("X-Device-Id", gotsPrefs.getDeviceId());
            urlConnection.addRequestProperty("X-Application-Name", gotsPrefs.getGardeningManagerAppname());
            urlConnection.addRequestProperty("provider", "GoogleOpenIDConnect");
            urlConnection.addRequestProperty("token", oAuth2Token);

            // TODO urlConnection.setConnectTimeout
            in = new BufferedInputStream(urlConnection.getInputStream());
            // readStream(in);
            StringBuilder builder = new StringBuilder();
            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }

            token = builder.toString();
            Log.d(TAG, "Token acquired: " + token);
            GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Success", 0);

        } catch (MalformedURLException e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            if (in != null)
                in.close();
        }

        return token;
    }
    // // TODO currently not used
    // protected void tokenNuxeoConnect() {
    // String device_id = getDeviceID();
    // gotsPrefs.setDeviceId(device_id);
    //
    // String tmp_token = request_temporaryauth_token(false);
    // if (tmp_token == null) {
    // Toast.makeText(mContext, "Authentication ", Toast.LENGTH_SHORT).show();
    // } else {
    // Toast.makeText(mContext, tmp_token, Toast.LENGTH_SHORT).show();
    // }
    // }

    // // TODO currently not used
    // public String request_temporaryauth_token(boolean revoke) {
    //
    // AsyncTask<Object, Void, String> task = new AsyncTask<Object, Void, String>() {
    // String token = null;
    //
    // @Override
    // protected String doInBackground(Object... objects) {
    // try {
    // String email = "toto.tata@gmail.com";
    // Session session = nuxeoManager.getSession();
    // Documents docs = (Documents) session.newRequest("Document.Email").setHeader(
    // Constants.HEADER_NX_SCHEMAS, "*").set("email", email).execute();
    //
    // // String uri =
    // // GotsPreferences.getInstance(getApplicationContext())
    // // .getGardeningManagerNuxeoAuthentication();
    // //
    // // List<NameValuePair> params = new
    // // LinkedList<NameValuePair>();
    // // params.add(new BasicNameValuePair("deviceId",
    // // GotsPreferences.getInstance(getApplicationContext())
    // // .getDeviceId()));
    // // params.add(new BasicNameValuePair("applicationName",
    // // GotsPreferences.getInstance(
    // // getApplicationContext()).getGardeningManagerAppname()));
    // // params.add(new BasicNameValuePair("deviceDescription",
    // // Build.MODEL + "(" + Build.MANUFACTURER +
    // // ")"));
    // // params.add(new BasicNameValuePair("permission",
    // // "ReadWrite"));
    // // params.add(new BasicNameValuePair("revoke", "false"));
    // //
    // // String paramString = URLEncodedUtils.format(params,
    // // "utf-8");
    // // uri += paramString;
    // // URL_CLASSNAME url = new URL_CLASSNAME(uri);
    // //
    // // URLConnection urlConnection;
    // // urlConnection = url.openConnection();
    // //
    // // urlConnection.addRequestProperty("X-User-Id",
    // // loginText.getText().toString());
    // // urlConnection.addRequestProperty("X-Device-Id",
    // // GotsPreferences
    // // .getInstance(getApplicationContext()).getDeviceId());
    // // urlConnection.addRequestProperty("X-Application-Name",
    // // GotsPreferences.getInstance(getApplicationContext()).getGardeningManagerAppname());
    // // urlConnection.addRequestProperty(
    // // "Authorization",
    // // "Basic "
    // // + Base64.encodeToString((loginText.getText().toString() +
    // // ":" + passwordText
    // // .getText().toString()).getBytes(), Base64.NO_WRAP));
    //
    // // urlConnection.addRequestProperty(
    // // "Authorization",
    // // "Basic "
    // // + Base64.encodeBase64((loginText.getText().toString() +
    // // ":" + passwordText.getText()
    // // .toString()).getBytes()));
    //
    // // InputStream in = new
    // // BufferedInputStream(urlConnection.getInputStream());
    // // try {
    // // // readStream(in);
    // // StringBuilder builder = new StringBuilder();
    // // String line;
    // // BufferedReader reader = new BufferedReader(new
    // // InputStreamReader(in, "UTF-8"));
    // // while ((line = reader.readLine()) != null) {
    // // builder.append(line);
    // // }
    // //
    // // token = builder.toString();
    // // Log.d("LoginActivity", "Token acquired: " + token);
    // //
    // // } finally {
    // // in.close();
    // // }
    // } catch (IOException e) {
    // Log.e("LoginActivity", e.getMessage(), e);
    // return null;
    //
    // } catch (Exception e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // return token;
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
    //
    // }
}
