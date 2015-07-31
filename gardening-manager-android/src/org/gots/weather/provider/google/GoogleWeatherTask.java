/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.weather.provider.google;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.bean.Address;
import org.gots.preferences.GotsPreferences;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherSet;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;
import android.util.Log;

public class GoogleWeatherTask extends AsyncTask<Object, Integer, WeatherConditionInterface> {
    protected URL url;

    private Date requestedDay;

    private boolean force = false;

    private static String queryString;

    private static int today;

    private static WeatherSet ws;

    public GoogleWeatherTask(Address address, Date requestedDay) {
        this.requestedDay = requestedDay;

        try {
            String weatherURL;

            if (GotsPreferences.isDevelopment())
                weatherURL = "http://92.243.19.29/weather.xml";
            else
                weatherURL = "http://www.google.com/ig/api?weather=" + address.getLocality() + ","
                        + address.getCountryName();

            if (today != Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
                force = true;
            if (queryString != weatherURL)
                force = true;

            today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            queryString = weatherURL;

            url = new URL(queryString.replace(" ", "%20"));
        } catch (Exception e) {
            Log.e("WeatherTask", e.getMessage());
        }
    }

    @Override
    protected WeatherConditionInterface doInBackground(Object... arg0) {
        if (force || ws == null) {

            try {

                // android.os.Debug.waitForDebugger();
                /*************/

                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url.toURI());

                // create a response handler
                ResponseHandler<String> responseHandler = new BasicResponseHandler();

                String responseBody = httpclient.execute(httpget, responseHandler);
                // Log.d(DEBUG_TAG, "response from httpclient:n "+responseBody);

                ByteArrayInputStream is = new ByteArrayInputStream(responseBody.getBytes());

                /* Get a SAXParser from the SAXPArserFactory. */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();

                /* Get the XMLReader of the SAXParser we created. */
                XMLReader xr = sp.getXMLReader();

                /* Create a new ContentHandler and apply it to the XML-Reader */
                GoogleWeatherHandler gwh = new GoogleWeatherHandler();
                xr.setContentHandler(gwh);

                // InputSource is = new InputSource(url.openStream());
                /* Parse the xml-data our URL_CLASSNAME-call returned. */
                xr.parse(new InputSource(is));

                /* Our Handler now provides the parsed weather-data to us. */
                ws = gwh.getWeatherSet();
            } catch (Exception e) {
                Log.e("WeatherManager", "WeatherQueryError", e);

            }
            force = false;
        }
        Calendar requestCalendar = Calendar.getInstance();
        requestCalendar.setTime(requestedDay);
        if (ws == null)
            return new WeatherCondition(requestedDay);
        else if (requestCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
            return ws.getWeatherCurrentCondition();
        else if (requestCalendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
            return ws.getWeatherForecastConditions().get(
                    requestCalendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        return new WeatherCondition(requestedDay);

    }

    @Override
    protected void onPostExecute(WeatherConditionInterface result) {
        if (force)
            Log.i("GoogleWeatherTask", "Use cache");
        else
            Log.i("GoogleWeatherTask", "executing request " + queryString);

        super.onPostExecute(result);
    }

}
