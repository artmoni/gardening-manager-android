package org.gots.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

/**
 * Query service to apply calls
 */
public class QueryService extends IntentService {

    final protected HttpClient httpClient;

    // When posting something with parameters in header of the request
    protected Map<String, String> postParameters = new HashMap<String, String>();

    protected static final int timeout = 30000;

    protected String call = "";

    public QueryService() {
        super("Gardening Restful Service");
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
        httpClient = new DefaultHttpClient(httpParameters);
    }

    /**
     * Handle background process. An intent is an abstract description of an
     * operation to be performed. This is the broadcast receiver handler.
     */
    protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("RECEIVER");
        // Get URI to call
        String command = intent.getStringExtra("COMMAND");
        // Get parameters of the call
        HashMap<String, String> parameters = (HashMap<String, String>) intent.getSerializableExtra("PARAMETERS");
        // Get call type (POST, GET...)
        String type = intent.getStringExtra("TYPE");
        // Create a mapping from String values to various Parcelable types
        Bundle bundle = new Bundle();
        HttpEntity entity = null;
        // Pre-compute parameters if exist
        List<NameValuePair> nameValuePairs = preComputeParameters(parameters);
        try {
            // Encode parameters
            entity = new UrlEncodedFormEntity(nameValuePairs);
            ((UrlEncodedFormEntity) entity).setContentEncoding(HTTP.UTF_8);
            ((UrlEncodedFormEntity) entity).setContentType("application/x-www-form-urlencoded");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(
                    "Cannot encode parameters from command: " + command
                            + " with parameters: " + nameValuePairs, e);
        }
        // Loading intent process sending
        receiver.send(0, Bundle.EMPTY);
        try {
            String query = intent.getStringExtra("URL") + command;
            // Build complete query
            HttpRequestBase call = getHttpRequestBase(type, nameValuePairs,
                    entity, query);
            this.call = call.getURI().toString();
            // Set Timeout to 30 sec
            HttpConnectionParams.setConnectionTimeout(httpClient.getParams(),
                    timeout);
            HttpConnectionParams.setSoTimeout(httpClient.getParams(), timeout);
            HttpResponse response = httpClient.execute(call);
            StatusLine status = response.getStatusLine();
            // Handle http errors
            if (HttpStatus.SC_OK != status.getStatusCode()) {
                bundle.putString(Intent.EXTRA_TEXT, status.getStatusCode()
                        + ": " + status.getReasonPhrase());
                receiver.send(-2, bundle);
                return;
            }
            // Handle JSON response
            InputStream responseStream = response.getEntity().getContent();
            // Don't take it in account for logout api (returning xml not json)
            String stream = convertStreamToString(responseStream);
            bundle.putString(Intent.EXTRA_RETURN_RESULT, stream);
            bundle.putString(Intent.EXTRA_REFERRER, command);
            receiver.send(1, bundle);
        } catch (ClientProtocolException e) {
            handleExceptions(receiver, bundle, e);
        } catch (UnsupportedEncodingException e) {
            handleExceptions(receiver, bundle, e);
        } catch (ConnectTimeoutException e) {
            receiver.send(-2, bundle);
        } catch (IOException e) {
            handleExceptions(receiver, bundle, e);
        }
    }

    private void handleExceptions(ResultReceiver receiver, Bundle bundle,
            Exception e) {
        bundle.putString(Intent.EXTRA_TEXT, e.toString());
        receiver.send(-2, bundle);
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

    protected HttpRequestBase getHttpRequestBase(String type,
            List<NameValuePair> nameValuePairs, HttpEntity entity, String query)
            throws UnsupportedEncodingException {

        HttpRequestBase call;
        if ("POST".equals(type)) {
            HttpPost post = new HttpPost(query);
            if (entity instanceof InputStreamEntity) {
                for (String key : postParameters.keySet()) {
                    post.setHeader(key, postParameters.get(key));
                }
                post.setHeader("Accept-Encoding", "gzip, deflate");
            }
            post.setEntity(entity);
            call = post;
        } else if ("GET".equals(type)) {
            if (nameValuePairs.size() > 0) {
                String param = URLEncodedUtils.format(nameValuePairs,
                        HTTP.UTF_8);
                query += "?" + param;
            }
            HttpGet get = new HttpGet(query);
            call = get;
        } else if ("PUT".equals(type)) {
            query += "?" + URLEncodedUtils.format(nameValuePairs, HTTP.UTF_8);
            HttpPut put = new HttpPut(query);
            put.setEntity(entity);
            call = put;
        } else {
            // Should be Delete call type
            HttpDelete delete = new HttpDelete(query);
            call = delete;
        }
        return call;
    }

    /**
     * Precomputing parameters from intent service to NameValuePair structure
     */
    protected List<NameValuePair> preComputeParameters(
            HashMap<String, String> parameters) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Collection) {
                Collection<?> values = (Collection<?>) value;
                for (Object v : values) {
                    // This will add a parameter for each value in the
                    // Collection/List
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(),
                            v == null ? null : String.valueOf(v)));
                }
            } else {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(),
                        value == null ? null : String.valueOf(value)));
            }
        }
        return nameValuePairs;
    }
}
