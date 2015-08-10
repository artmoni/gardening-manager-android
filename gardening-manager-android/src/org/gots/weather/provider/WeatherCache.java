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
import org.gots.preferences.GotsPreferences;

import android.content.Context;
import android.util.Log;

public class WeatherCache {

    private File cacheDirectory;

    private static String TAG = "WeatherCache";

    public WeatherCache(Context mContext) {
        cacheDirectory = mContext.getCacheDir();
    }

    public InputStream getCacheByURL(URL url) throws URISyntaxException, ClientProtocolException, IOException {
        InputStream weatherXmlStream = null;
        String fileName = "";
        try {
            fileName = md5(url.toURI().toString());
            weatherXmlStream = getLocalCache(fileName);
        } catch (ObsoleteCacheException | FileNotFoundException e) {
            Log.w(TAG, "getLocalCache " + e.getMessage());
            weatherXmlStream = getRemoteFile(url);
            setLocalCache(fileName, weatherXmlStream);
        }
        return weatherXmlStream;
    }

    protected InputStream getRemoteFile(URL url) throws ClientProtocolException, URISyntaxException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url.toURI());
        Log.d(TAG, "getRemoteFile " + url.toURI().toString());

        // create a response handler
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httpget, responseHandler);
        // Log.d(DEBUG_TAG, "response from httpclient:n "+responseBody);

        ByteArrayInputStream is = new ByteArrayInputStream(responseBody.getBytes());

        return is;
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

    private InputStream getLocalCache(String filePath) throws ObsoleteCacheException, FileNotFoundException {
        File f = new File(cacheDirectory, filePath);

        Calendar lastModDate = new GregorianCalendar();
        lastModDate.setTime(new Date(f.lastModified()));
        Calendar today = Calendar.getInstance();

        FileInputStream fileInputStream = null;
        fileInputStream = new FileInputStream(f);

        if (lastModDate.get(Calendar.DAY_OF_YEAR) < today.get(Calendar.DAY_OF_YEAR))
            throw new ObsoleteCacheException();

        Log.d(TAG, "Found cache file " + f.getAbsolutePath());
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

            Log.d(TAG, "Creating cache file " + f.getAbsolutePath());

        } catch (Exception e) {
            Log.e(TAG, "Error creating cache file " + e.getMessage());
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
