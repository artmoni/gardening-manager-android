package org.gots.justvisual;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sfleury on 13/07/15.
 */
public class ImageRecognition {
    private static String API_KEY = "sJso7xjF2GO94sA3lWCkwUarL1P3hCWe";
    private static String API_ID = "940";
    private static String SERVER_URL = "garden.vsapi01.com";
    private String TAG = ImageRecognition.class.getSimpleName();

    public String getURL(String imageUrl) {
        String url = "http://" + SERVER_URL + "/api-search/by-url?url=" + imageUrl + "&apiid=" + API_ID + "&apikey=" + API_KEY;
        return url;

    }

    public Map<String, List<JustVisualResult>> queryRecognition(final String url, Document document) {
        ImageRecognition imageRecognition = new ImageRecognition();
        Map<String, List<JustVisualResult>> species = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            response = httpclient.execute(new HttpGet(imageRecognition.getURL(url)));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();

                species = parseJSON(responseString, document);
//                        Log.d(TAG, responseString);
                out.close();
                //..more logic
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return species;
//    priv
    }

    private Map<String, List<JustVisualResult>> parseJSON(String responseString, Document document) throws JSONException {
        Map<String, List<JustVisualResult>> species = new HashMap<>();
        JSONObject json = new JSONObject(responseString);
        JSONArray images = json.getJSONArray("images");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        for (int i = 0; i < images.length(); i++) {
            try {
                JSONObject image = images.getJSONObject(i);
                JustVisualResult result = gson.fromJson(image.toString(), JustVisualResult.class);
                result.setUuid(document.getId());
                if (species.get(result.getPlantNames()) == null)
                    species.put(result.getPlantNames(), new ArrayList<JustVisualResult>());
                species.get(result.getPlantNames()).add(result);
                Log.d(TAG, result.toString());

            } catch (JSONException jsonException) {
                Log.w(TAG, jsonException.getMessage() + ": " + responseString);
            }

        }
        return species;
    }
}
