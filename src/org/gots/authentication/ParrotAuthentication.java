package org.gots.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ParrotAuthentication {
    // https://apiflowerpower.parrot.com//user/v1/authenticate?Accept-Language=fr&grant_type=password&client_id=sebastien.fleury@gmail.com&client_secret=arfkUnBAcTL99ynPXelq2u7msb7aMkOk2LgVZP7w4CANMFBZ&username=apiflowerpower.demo@parrot.com&password=api_demo
    String baseName = "https://apiflowerpower.parrot.com";

    String username = "apiflowerpower.demo@parrot.com";

    String password = "api_demo";

    String clientId = "sebastien.fleury@gmail.com";

    String clientSecret = "arfkUnBAcTL99ynPXelq2u7msb7aMkOk2LgVZP7w4CANMFBZ";

    private String TAG = "ParrotAuthentication";

    public String api_json(String api_url, List<NameValuePair> params, String method, String headers)
            throws IOException {
        URL url = new URL(baseName + api_url);

        HttpClient httpclient = new DefaultHttpClient();
        StringBuilder builder = new StringBuilder();

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("Accept-Language", "fr"));
            nameValuePairs.addAll(params);
            String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
            HttpGet httppost = new HttpGet(url.toString() + "?" + paramString);

            // httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(TAG, "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }

        return builder.toString();
    }

    public String getToken() {
        String token = null;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            JSONObject json = new JSONObject(api_json("/user/v1/authenticate", params, "", ""));
            token = json.getString("access_token");
        } catch (IOException e) {
            Log.w(TAG, e.getMessage(), e);
        } catch (JSONException e) {
            Log.w(TAG, e.getMessage(), e);
        }
        return token;
    }

    protected String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
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
}
