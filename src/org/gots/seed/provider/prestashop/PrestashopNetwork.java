package org.gots.seed.provider.prestashop;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;

public class PrestashopNetwork {
    String result;

    private static final String PRESTASHOP_APIKEY = "PY0XHE11WE4VQNJ18DXUQFZ7OJR5YVBR";

    private String URL = "http://" + PRESTASHOP_APIKEY + "@192.168.10.203/prestashop/api/";

    public InputStream callWebService2(String q) {

        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(PRESTASHOP_APIKEY, ""));
        //
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.setCredentialsProvider(credProvider);

        HttpResponse response;
        InputStream is = null;
        try {
            // Authenticator.setDefault(new Authenticator() {
            // protected PasswordAuthentication getPasswordAuthentication() {
            // return new PasswordAuthentication(PRESTASHOP_APIKEY, "".toCharArray());
            //
            // }
            // });

            HttpGet httpGet = new HttpGet(URL + q);
            // httpGet.setHeader("Authorization", "Basic "+Base64.encodeBytes((PRESTASHOP_APIKEY+":").getBytes()));

            response = httpClient.execute(httpGet);

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                // ByteArrayOutputStream out = new ByteArrayOutputStream();
                is = response.getEntity().getContent();
                // out.close();
                // is = response.getEntity().getContent();
                // String responseString = out.toString();
                // Log.i("WebService2",responseString);
                // ..more logic
            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return is;
    }

}
