package org.gots.seed.providers.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

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

import android.os.AsyncTask;

public class SimpleNetwork extends AsyncTask<String, Integer, InputStream> {
	String result;
	private static final String GOTS_APIKEY = "PY0XHE11WE4VQNJ18DXUQFZ7OJR5YVBR";
	private String HOST = "services.gardening-manager.com";
	private String PATH = "/seeds/";
	private String URL = "http://" + HOST + PATH;
	private InputStream instream;

	private String urlFormatter() {
		String lang = Locale.getDefault().getLanguage();
		String filename = "seed";
		String fileextension = ".xml";
		if ("fr".equals(lang))
			filename += "-" + lang + fileextension;
		else
			filename += fileextension;
		
		return URL+filename ;
	}

	@Override
	protected InputStream doInBackground(String... params) {

		CredentialsProvider credProvider = new BasicCredentialsProvider();
		credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
				new UsernamePasswordCredentials(GOTS_APIKEY, ""));

		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.setCredentialsProvider(credProvider);

		HttpResponse response;
		InputStream is = null;
		try {

			HttpGet httpGet = new HttpGet(urlFormatter());

			response = httpClient.execute(httpGet);

			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				is = response.getEntity().getContent();

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
		// return null;
	}
}
