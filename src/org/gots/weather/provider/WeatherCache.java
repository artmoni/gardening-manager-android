package org.gots.weather.provider;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class WeatherCache {

    private File cacheDirectory;

    private final int MAX_DOWNLOAD_TRY = 3;

    private static URL CURRENT_DOWNLOAD_URL;

    private static int currentDownloadTry = 0;

    private static String TAG = "WeatherCache";

    public WeatherCache(Context mContext) {
        cacheDirectory = mContext.getCacheDir();
    }

    public InputStream getCacheByURL(URL url) {
        InputStream weatherXmlStream = null;
        String fileName = "";
        try {
            fileName = md5(url.toURI().toString());
            weatherXmlStream = getLocalCache(fileName);

        } catch (URISyntaxException e) {
            Log.w(TAG, e.getMessage(), e);
        } catch (ObsoleteCacheException e) {
            Log.w(TAG, e.getMessage());
            weatherXmlStream = getRemoteWeather(url, weatherXmlStream, fileName);
        }
        return weatherXmlStream;
    }

    protected InputStream getRemoteWeather(URL url, InputStream weatherXmlStream, String fileName) {
        try {
            if (currentDownloadTry < MAX_DOWNLOAD_TRY || !url.equals(WeatherCache.CURRENT_DOWNLOAD_URL)) {
                WeatherCache.CURRENT_DOWNLOAD_URL = url;
                weatherXmlStream = downloadWeatherXML(url);
                setLocalCache(fileName, weatherXmlStream);
                currentDownloadTry++;

                GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
                tracker.trackEvent("Weather", "Download", url.getPath(), currentDownloadTry);

            } else {
                Log.w(TAG, MAX_DOWNLOAD_TRY + " Max download retry reached");
            }
        } catch (Exception downloadException) {
            currentDownloadTry++;
            Log.w(TAG, "[" + currentDownloadTry + "/" + MAX_DOWNLOAD_TRY + "] " + downloadException.getMessage()
                    + " / " + url.toString());

            // TODO a better way to store the error XML bad request data
            fileName = md5("errorfile");
            if (weatherXmlStream != null)
                setLocalCache(fileName, weatherXmlStream);

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
        Log.i(TAG, "Get URI " + url.toURI().toString());

        // create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpget, responseHandler);
        // Log.d(DEBUG_TAG, "response from httpclient:n "+responseBody);

        ByteArrayInputStream is = new ByteArrayInputStream(responseBody.getBytes());

        return is;
    }

    private InputStream getLocalCache(String filePath) throws ObsoleteCacheException {
        File f = new File(cacheDirectory, filePath);

        Calendar lastModDate = new GregorianCalendar();
        lastModDate.setTime(new Date(f.lastModified()));
        Calendar today = Calendar.getInstance();

        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(f);

            if (lastModDate.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR))
                throw new ObsoleteCacheException();

            Log.i(TAG, "Open cache " + f.getAbsolutePath());
        } catch (FileNotFoundException e) {
            Log.w(TAG, e.getMessage(), e);
            throw new ObsoleteCacheException();
        }
        return fileInputStream;
    }

    private void setLocalCache(String filePath, InputStream weatherXmlStream) {
        File f = new File(cacheDirectory, filePath);
        try {

            OutputStream os = new BufferedOutputStream(new FileOutputStream(f));

            byte buf[] = new byte[1024];
            int len;
            while ((len = weatherXmlStream.read(buf)) > 0)
                os.write(buf, 0, len);
            os.close();
            // weatherXmlStream.close();

            Log.i(TAG, "Creating cache file " + f.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Error creating cache file " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cleanLocalCache(String fileName) {
        File f = new File(cacheDirectory, fileName);
        f.delete();

        Log.d(TAG, "Deleting cache file " + f.getAbsolutePath());
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
