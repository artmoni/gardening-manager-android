package org.gots.authentication.provider.parrot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.gots.R;
import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ParrotAuthentication {
    // https://apiflowerpower.parrot.com//user/v1/authenticate?Accept-Language=fr&grant_type=password&client_id=sebastien.fleury@gmail.com&client_secret=arfkUnBAcTL99ynPXelq2u7msb7aMkOk2LgVZP7w4CANMFBZ&username=apiflowerpower.demo@parrot.com&password=api_demo
    private String baseName = "https://apiflowerpower.parrot.com";

    // private String username = "";
    //
    // private String password = "";

    private String clientId = "";

    private String clientSecret = "";

    // private String access_token = null;

    private String TAG = "ParrotAuthentication";

    private Context mContext;

    private Properties properties = new Properties();

    private GotsPreferences gotsPref;

    private static ParrotAuthentication instance;
    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }
    private ParrotAuthentication(Context context) {
        mContext = context;
        InputStream propertiesStream = null;
        try {
            propertiesStream = mContext.getResources().openRawResource(R.raw.config);
            properties.load(propertiesStream);
            clientId = properties.getProperty("parrot.clientid");
            clientSecret = properties.getProperty("parrot.clientsecret");
            // username = properties.getProperty("parrot.username");
            // password = properties.getProperty("parrot.password");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        gotsPref = getGotsContext().getServerConfig();
        enableHttpResponseCache();

    }

    public static ParrotAuthentication getInstance(Context context) {
        if (instance == null) {
            instance = new ParrotAuthentication(context);
        }
        return instance;
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(mContext.getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache").getMethod("install", File.class, long.class).invoke(
                    null, httpCacheDir, httpCacheSize);
            // HttpResponseCache.install(httpCacheDir, httpCacheSize);
            // Log.i(TAG, "getHitCount=" + HttpResponseCache.getInstalled().getHitCount() + " getRequestCount="
            // + HttpResponseCache.getInstalled().getRequestCount() + " getNetworkCount="
            // + HttpResponseCache.getInstalled().getNetworkCount());

        } catch (Exception httpResponseCacheNotAvailable) {
            Log.i(TAG, httpResponseCacheNotAvailable.getMessage());
        }
    }

    private String api_json(String api_url, List<NameValuePair> params, String method, Map<String, String> headers)
            throws IOException {

        // HttpClient httpclient = new DefaultHttpClient();

        String json = "";
        try {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.addAll(params);
            String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");

            // HttpGet httpGet = new HttpGet(url.toString() + "?" + paramString);
            URL url = new URL(baseName + api_url + "?" + paramString);

            // Proxy proxy = new Proxy(Proxy.Type.HTTP , new InetSocketAddress("", 80));
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setUseCaches(true);
            con.setRequestProperty("Accept-Language", Locale.getDefault().getCountry().toLowerCase());

            /*
             * CACHE CONTROL
             */
            int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
            con.addRequestProperty("Cache-Control", "max-stale=" + maxStale);

            // con.addRequestProperty("Cache-Control", "only-if-cached");

            // httpGet.addHeader("Accept-Language", Locale.getDefault().getCountry().toLowerCase());
            if (headers != null)
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    // httpGet.addHeader(header.getKey(), header.getValue());
                    con.setRequestProperty(header.getKey(), header.getValue());

                }
            // readStream(con.getInputStream());

            // HttpResponse response = httpclient.execute(httpGet);
            // StatusLine statusLine = response.getStatusLine();
            // int statusCode = statusLine.getStatusCode();
            if (con.getResponseCode() == 200) {

                // HttpEntity entity = response.getEntity();
                json = convertStreamToString(con.getInputStream());
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

    // private String api_json(String api_url, List<NameValuePair> params, String method, Map<String, String> headers)
    // throws IOException {
    // URL url = new URL(baseName + api_url);
    //
    // HttpClient httpclient = new DefaultHttpClient();
    //
    // String json = "";
    // try {
    // List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    // nameValuePairs.addAll(params);
    // String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
    //
    // HttpGet httpGet = new HttpGet(url.toString() + "?" + paramString);
    //
    // httpGet.addHeader("Accept-Language", Locale.getDefault().getCountry().toLowerCase());
    // if (headers != null)
    // for (Map.Entry<String, String> header : headers.entrySet()) {
    // httpGet.addHeader(header.getKey(), header.getValue());
    // }
    //
    // HttpResponse response = httpclient.execute(httpGet);
    // StatusLine statusLine = response.getStatusLine();
    // int statusCode = statusLine.getStatusCode();
    // if (statusCode == 200) {
    // HttpEntity entity = response.getEntity();
    // json = convertStreamToString(entity.getContent());
    // } else {
    // Log.e(TAG, "Failed to download file");
    // }
    // } catch (ClientProtocolException e) {
    // Log.w(TAG, e.getMessage(), e);
    // } catch (IOException e) {
    // Log.w(TAG, e.getMessage(), e);
    // }
    //
    // return json;
    // }

    public String getToken(String username, String password) {
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
            // access_token = token;
            gotsPref.setParrotToken(token);
        } catch (IOException e) {
            Log.w(TAG, e.getMessage(), e);
        } catch (JSONException e) {
            Log.w(TAG, e.getMessage(), e);
        }
        return token;
    }

    public JSONObject getJSON(String api_key, List<NameValuePair> params) throws JSONException, IOException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + gotsPref.getParrotToken());
        if (params == null)
            params = new ArrayList<NameValuePair>();
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
