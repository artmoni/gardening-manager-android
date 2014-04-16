package org.gots.authentication;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

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
import org.gots.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ParrotAuthentication {
    // https://apiflowerpower.parrot.com//user/v1/authenticate?Accept-Language=fr&grant_type=password&client_id=sebastien.fleury@gmail.com&client_secret=arfkUnBAcTL99ynPXelq2u7msb7aMkOk2LgVZP7w4CANMFBZ&username=apiflowerpower.demo@parrot.com&password=api_demo
    private String baseName = "https://apiflowerpower.parrot.com";

    private String username = "";

    private String password = "";

    private String clientId = "";

    private String clientSecret = "";

    private String access_token = null;

    private String TAG = "ParrotAuthentication";

    private Context mContext;

    private Properties properties = new Properties();

    public ParrotAuthentication(Context context) {
        mContext = context;
        InputStream propertiesStream = null;
        try {
            propertiesStream = mContext.getResources().openRawResource(R.raw.config);
            properties.load(propertiesStream);
            clientId = properties.getProperty("parrot.clientid");
            clientSecret = properties.getProperty("parrot.clientsecret");
            username = properties.getProperty("parrot.username");
            password = properties.getProperty("parrot.password");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
       
    }

    private String api_json(String api_url, List<NameValuePair> params, String method, Map<String, String> headers)
            throws IOException {
        URL url = new URL(baseName + api_url);

        HttpClient httpclient = new DefaultHttpClient();

        String json = "";
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.addAll(params);
            String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");

            HttpGet httpGet = new HttpGet(url.toString() + "?" + paramString);

            httpGet.addHeader("Accept-Language", Locale.getDefault().getCountry().toLowerCase());
            if (headers != null)
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    httpGet.addHeader(header.getKey(), header.getValue());
                }

            HttpResponse response = httpclient.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                json = convertStreamToString(entity.getContent());
            } else {
                Log.e(TAG, "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            Log.w(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.w(TAG, e.getMessage(), e);
        }

        return json;
    }

    public String getToken() {
        String api_authentication = "/user/v1/authenticate";
        String token = null;
        try {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("grant_type", "password"));
            params.add(new BasicNameValuePair("client_id", clientId));
            params.add(new BasicNameValuePair("client_secret", clientSecret));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            JSONObject json = new JSONObject(api_json(api_authentication, params, "", null));
            token = json.getString("access_token");
            access_token = token;
        } catch (IOException e) {
            Log.w(TAG, e.getMessage(), e);
        } catch (JSONException e) {
            Log.w(TAG, e.getMessage(), e);
        }
        return token;
    }

    public JSONObject getJSON(String api_key) throws JSONException, IOException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        // params.add(new BasicNameValuePair("grant_type", "password"));

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + access_token);
        JSONObject json = new JSONObject(api_json(api_key, params, "", headers));
        return json;
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
