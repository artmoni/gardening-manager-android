package org.gots.weather.provider;

import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
import org.gots.weather.provider.previmeteo.PrevimeteoWeatherHandler;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract.Directory;
import android.util.Log;

public class WeatherCache {

	private String rootDirectory;
	private final int MAX_DOWNLOAD_TRY = 3;
	private static URL CURRENT_DOWNLOAD_URL;
	private static int currentDownloadTry = 0;

	public WeatherCache() {
		if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d("WeatherCache", "Sdcard was not mounted !!");

		} else {
			rootDirectory = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Gardening-Manager/cache/";

			File directory = new File(rootDirectory);
			directory.mkdirs();
		}
	}

	public InputStream getCacheByURL(URL url) {
		InputStream weatherXmlStream = null;
		String fileName = "";

		try {
			fileName = md5(url.toURI().toString());
			weatherXmlStream = getLocalCache(fileName);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("getweatherCache", e.getMessage());
			try {
				if (currentDownloadTry < MAX_DOWNLOAD_TRY || !url.equals(WeatherCache.CURRENT_DOWNLOAD_URL)) {
					WeatherCache.CURRENT_DOWNLOAD_URL = url;
					weatherXmlStream = downloadWeatherXML(url);
					setLocalCache(fileName, weatherXmlStream);
					currentDownloadTry++;

				} else {
					Log.w("WeatherCache", MAX_DOWNLOAD_TRY + " Max download retry reached");
				}
			} catch (Exception downloadException) {
				currentDownloadTry++;
				Log.w("WeatherCache", "Current try = " + currentDownloadTry + " / " + downloadException.getMessage()
						+ " / " + url.toString());
				
				//TODO a better way to store the error XML bad request data
				setLocalCache(fileName, null);


			}
		}
		return weatherXmlStream;
	}

	public void clean(URL url) {
		String fileName;
		try {
			fileName = md5(url.toURI().toString());
			cleanLocalCache(fileName);

		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private InputStream downloadWeatherXML(URL url) throws URISyntaxException, ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url.toURI());
		Log.i("downloadWeatherXML", url.toURI().toString());

		// create a response handler
		ResponseHandler<String> responseHandler = new BasicResponseHandler();

		String responseBody = httpclient.execute(httpget, responseHandler);
		// Log.d(DEBUG_TAG, "response from httpclient:n "+responseBody);

		ByteArrayInputStream is = new ByteArrayInputStream(responseBody.getBytes());

		return is;
	}

	private InputStream getLocalCache(String filePath) throws FileNotFoundException, ObsoletCacheException {
		File f = new File(rootDirectory + filePath);
		// FileReader reader = new FileReader(f);

		Calendar lastModDate = new GregorianCalendar();
		lastModDate.setTime(new Date(f.lastModified()));
		Calendar today = Calendar.getInstance();
		
		FileInputStream fileInputStream = new FileInputStream(f);

		if (lastModDate.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR))
			throw new ObsoletCacheException();


		Log.i("WeatherCache", "Open cache " + filePath);
		return fileInputStream;
	}

	private void setLocalCache(String filePath, InputStream weatherXmlStream) {
		File f = new File(rootDirectory + filePath);
		try {

			OutputStream os = new BufferedOutputStream(new FileOutputStream(f));

			byte buf[] = new byte[1024];
			int len;
			while ((len = weatherXmlStream.read(buf)) > 0)
				os.write(buf, 0, len);
			os.close();
			// weatherXmlStream.close();

			Log.i("WeatherCache", "Save data to file " + f.getAbsolutePath());

		} catch (Exception e) {
			Log.e("WeatherCache", "setLocalCache " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void cleanLocalCache(String fileName) {
		File f = new File(rootDirectory + fileName);
		// FileReader reader = new FileReader(f);
		f.delete();
		Log.d("WeatherCache", "Delete cache file "+f.getAbsolutePath());

	}

	private String md5(String s) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(s.getBytes());
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++)
				hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
			return hexString.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
