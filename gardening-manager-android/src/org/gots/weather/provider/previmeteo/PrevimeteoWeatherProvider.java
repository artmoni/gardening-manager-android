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
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.gots.bean.Address;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherSet;
import org.gots.weather.provider.WeatherCache;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

public class PrevimeteoWeatherProvider extends LocalWeatherProvider {
    private static final String TAG = "PrevimeteoWeatherProvider";

    protected URL url;

    private static String queryString;

    private WeatherSet weatherSet;

    private boolean iserror;

    private WeatherCache cache;

    private GotsPreferences gotsPreferences;

    public PrevimeteoWeatherProvider(Context context) {
        super(context);
        gotsPreferences = GotsPreferences.getInstance().initIfNew(context);

        try {
            Address address = GardenManager.getInstance().initIfNew(context).getCurrentGarden().getAddress();
            String weatherURL;

            weatherURL = "http://api.previmeteo.com/" + gotsPreferences.getWeatherApiKey() + "/ig/api?weather="
                    + address.getLocality() + "," + context.getResources().getConfiguration().locale.getCountry()
                    + "&hl=fr";

            queryString = weatherURL;

            url = new URL(queryString.replace(" ", "%20"));
            Log.i(TAG, "Weather request on " + url.toString());
        } catch (Exception e) {
            Log.e(TAG, "" + e.getMessage());
        }

    }

    // @Override
    // protected WeatherConditionInterface doInBackground(Object... arg0) {
    /*
     * (non-Javadoc)
     * @see org.gots.weather.provider.previmeteo.WeatherProvider#getCondition(java.util.Date)
     */
    public WeatherConditionInterface getCondition(Date requestedDay) {
        Calendar requestCalendar = Calendar.getInstance();
        requestCalendar.setTime(requestedDay);

        int dayRequested = requestCalendar.get(Calendar.DAY_OF_YEAR);
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        if (weatherSet == null) {

            try {
                Log.d(TAG, "getCondition " + DateFormat.format("MM/dd/yy h:mmaa", requestedDay));
                cache = new WeatherCache(mContext);

                InputStream is = cache.getCacheByURL(url);

                /* Get a SAXParser from the SAXPArserFactory. */
                SAXParserFactory spf = SAXParserFactory.newInstance();
                SAXParser sp = spf.newSAXParser();

                /* Get the XMLReader of the SAXParser we created. */
                XMLReader xr = sp.getXMLReader();

                PrevimeteoErrorHandler error = new PrevimeteoErrorHandler();
                xr.setContentHandler(error);
                xr.parse(new InputSource(is));
                iserror = error.isError();

                if (!iserror) {
                    // TODO we should not need to get the cache file again, a
                    // better way exists

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
            } catch (Exception e) {
                Log.e(TAG, "PrevimeteoErrorHandler has return an error " + e.getMessage());
                iserror = true;
                return super.getCondition(requestedDay);
            }
        }

        WeatherConditionInterface remoteCondition = new WeatherCondition(requestedDay);

        if (weatherSet == null)
            remoteCondition = new WeatherCondition(requestedDay);
        else if (dayRequested == today)
            remoteCondition = weatherSet.getWeatherCurrentCondition();
        else if (dayRequested > today)
            remoteCondition = weatherSet.getWeatherForecastConditions().get(dayRequested - today);
        else if (dayRequested < today && requestCalendar.get(Calendar.YEAR) > Calendar.getInstance().get(Calendar.YEAR))
            remoteCondition = weatherSet.getWeatherForecastConditions().get(dayRequested + 365 - today);

        remoteCondition.setDate(requestCalendar.getTime());
        remoteCondition.setDayofYear(requestCalendar.get(Calendar.DAY_OF_YEAR));

        // TODO take care of new year

        // WeatherConditionInterface localCondition = super.getWeatherByDayofyear(dayRequested);
        // if (remoteCondition.getCondition() != null) {
        // remoteCondition.setDate(requestCalendar.getTime());
        // remoteCondition.setDayofYear(requestCalendar.get(Calendar.DAY_OF_YEAR));
        //
        // if (localCondition != null && localCondition.getId() > 0) {
        // remoteCondition.setId(localCondition.getId());
        // remoteCondition = super.updateWeather(remoteCondition);
        // } else {
        // remoteCondition = super.insertWeather(remoteCondition);
        // }
        // } else if (localCondition != null && localCondition.getCondition() != null)
        // remoteCondition = localCondition;
            return remoteCondition;

    }

    public WeatherConditionInterface updateCondition(WeatherConditionInterface weatherCondition, Date day) {
        WeatherConditionInterface conditionInterface = null;
        Calendar conditionDate = Calendar.getInstance();
        conditionDate.setTime(day);

        if (weatherCondition == null)
            return null;

        weatherCondition.setDate(day);
        weatherCondition.setDayofYear(conditionDate.get(Calendar.DAY_OF_YEAR));

        if (weatherCondition == null || weatherCondition.getSummary() == null)
            conditionInterface = super.insertCondition(weatherCondition);
        else
            conditionInterface = super.updateCondition(weatherCondition, day);
        return conditionInterface;
    }
}
