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
package org.gots.weather.provider.previmeteo;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpResponseException;
import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherSet;
import org.gots.weather.exception.UnknownWeatherException;
import org.gots.weather.provider.WeatherCache;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class PrevimeteoWeatherProvider extends LocalWeatherProvider {
    private static final String TAG = "PrevimeteoWeatherProvider";

    private static String queryString;

    private WeatherSet weatherSet;

    private boolean iserror;

    private WeatherCache cache;

    private GotsPreferences gotsPreferences;

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    public PrevimeteoWeatherProvider(Context context) {
        super(context);
        gotsPreferences = getGotsContext().getServerConfig();
    }

    protected URL buildUriFromAddress(String forecastLocality) throws MalformedURLException {
        String weatherURL;

        weatherURL = "http://api.previmeteo.com/" + gotsPreferences.getWeatherApiKey() + "/ig/api?weather="
                + forecastLocality + "," + mContext.getResources().getConfiguration().locale.getCountry() + "&hl=fr";

        queryString = weatherURL;

        Log.d(TAG, "Weather request on " + weatherURL);
        return new URL(queryString.replace(" ", "%20"));
    }

    @Override
    public short fetchWeatherForecast(String forecastLocality) {
        cache = new WeatherCache(mContext);
        PrevimeteoErrorHandler error = new PrevimeteoErrorHandler();

        try {
            URL url = buildUriFromAddress(forecastLocality);

            InputStream is = cache.getCacheByURL(url);
            /* Get a SAXParser from the SAXPArserFactory. */
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp;
            sp = spf.newSAXParser();

            /* Get the XMLReader of the SAXParser we created. */
            XMLReader xr = sp.getXMLReader();

            xr.setContentHandler(error);
            xr.parse(new InputSource(is));
            iserror = error.isError();

            if (!iserror) {
                is = cache.getCacheByURL(url);

                /*
                 * Create a new ContentHandler and apply it to the
                 * XML-Reader
                 */
                PrevimeteoWeatherHandler gwh = new PrevimeteoWeatherHandler();
                xr.setContentHandler(gwh);

                // InputSource is = new InputSource(url.openStream());
                /* Parse the xml-data our URL-call returned. */
                xr.parse(new InputSource(is));

                /* Our Handler now provides the parsed weather-data to us. */
                weatherSet = gwh.getWeatherSet();
            }
            GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
            tracker.trackEvent("Weather", "Match", gotsPreferences.getWeatherApiKey(), 0);
        } catch (HttpResponseException httpResponseException) {
            Log.w(TAG,
                    "PrevimeteoErrorHandler has return an HttpResponseException " + httpResponseException.getMessage());
            if (httpResponseException.getStatusCode() == 400) {
                GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
                if (httpResponseException.getMessage() == null)
                    tracker.trackEvent("Weather",
                            "Empty message - httpResponseStatus " + httpResponseException.getStatusCode(), ""
                                    + gotsPreferences.getWeatherApiKey(), 0);
                else
                    tracker.trackEvent("Weather", httpResponseException.getMessage(),
                            "" + gotsPreferences.getWeatherApiKey(), 0);
                return WEATHER_ERROR_CITY_UNKNOWN;
            }
        } catch (Exception e) {
            Log.e(TAG, "PrevimeteoErrorHandler (" + error.getMessage() + ") ");
            iserror = true;
            if (e.getMessage() != null) {
                GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
                tracker.trackEvent("Weather", e.getMessage(), "" + gotsPreferences.getWeatherApiKey(), 0);
            }
            return WEATHER_ERROR_UNKNOWN;
        }
        return WEATHER_OK;
    }

    // @Override
    // protected WeatherConditionInterface doInBackground(Object... arg0) {
    /*
     * (non-Javadoc)
     * @see org.gots.weather.provider.previmeteo.WeatherProvider#getCondition(java.util.Date)
     */
    public WeatherConditionInterface getCondition(Date requestedDay) throws UnknownWeatherException {
        Calendar requestCalendar = Calendar.getInstance();
        requestCalendar.setTime(requestedDay);

        int dayRequested = requestCalendar.get(Calendar.DAY_OF_YEAR);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        Log.d(TAG, "getCondition " + DateFormat.format("MM/dd/yy h:mmaa", requestedDay));

        WeatherConditionInterface weatherCondition = new WeatherCondition(requestedDay);
        try {
            if (weatherSet == null)
                weatherCondition = new WeatherCondition(requestedDay);
            else if (dayRequested == today)
                weatherCondition = weatherSet.getWeatherCurrentCondition();
            else if (dayRequested > today)
                weatherCondition = weatherSet.getWeatherForecastConditions().get(dayRequested - today);
            else if (dayRequested < today
                    && requestCalendar.get(Calendar.YEAR) > Calendar.getInstance().get(Calendar.YEAR))
                weatherCondition = weatherSet.getWeatherForecastConditions().get(dayRequested + 365 - today);
        } catch (Exception e) {
            weatherCondition = super.getCondition(requestedDay);
        }
        if (weatherCondition != null) {
            weatherCondition.setDate(requestCalendar.getTime());
            weatherCondition.setDayofYear(requestCalendar.get(Calendar.DAY_OF_YEAR));
        }

        return weatherCondition;
    }

    public WeatherConditionInterface updateCondition(WeatherConditionInterface weatherCondition) {
        // WeatherConditionInterface conditionInterface = null;
        // Calendar conditionDate = Calendar.getInstance();
        // conditionDate.setTime(day);
        //
        // if (weatherCondition == null)
        // return null;
        //
        // weatherCondition.setDate(day);
        // weatherCondition.setDayofYear(conditionDate.get(Calendar.DAY_OF_YEAR));
        //
        // if (weatherCondition == null || weatherCondition.getSummary() == null)
        // conditionInterface = super.insertCondition(weatherCondition);
        // else
        // conditionInterface = super.updateCondition(weatherCondition);
        Log.d(TAG, "updateCondition() is not implemented here");
        return null;
    }
}
