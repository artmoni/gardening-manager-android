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
import org.gots.weather.provider.local.WeatherDBHelper;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

public class PrevimeteoWeatherProvider extends WeatherDBHelper implements WeatherProvider {
    private static final String TAG = "PrevimeteoWeatherProvider";

    protected URL url;

    private static String queryString;

    private WeatherSet ws;

    private boolean iserror;

    private WeatherCache cache;

    public PrevimeteoWeatherProvider(Context context) {
        super(context);

        try {
            Address address = GardenManager.getInstance().initIfNew(context).getCurrentGarden().getAddress();
            String weatherURL;

            if (GotsPreferences.isDevelopment())
                weatherURL = "http://www.gardening-manager.com/weather/weather-error.xml";
            else
                weatherURL = "http://api.previmeteo.com/" + GotsPreferences.getWeatherApiKey() + "/ig/api?weather="
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
    @Override
    public WeatherConditionInterface getCondition(Date requestedDay) {
        if (ws == null) {

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
                    ws = gwh.getWeatherSet();
                }
            } catch (Exception e) {
                Log.e(TAG, "PrevimeteoErrorHandler has return an error " + e.getMessage());
                iserror = true;
                return null;
            }
        }

        Calendar requestCalendar = Calendar.getInstance();
        requestCalendar.setTime(requestedDay);
        WeatherConditionInterface conditionInterface = new WeatherCondition(requestedDay);
        if (ws == null)
            conditionInterface = new WeatherCondition(requestedDay);
        else if (requestCalendar.get(Calendar.DAY_OF_YEAR) == Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
            conditionInterface = ws.getWeatherCurrentCondition();
        else if (requestCalendar.get(Calendar.DAY_OF_YEAR) > Calendar.getInstance().get(Calendar.DAY_OF_YEAR))
            conditionInterface = ws.getWeatherForecastConditions().get(
                    requestCalendar.get(Calendar.DAY_OF_YEAR) - Calendar.getInstance().get(Calendar.DAY_OF_YEAR));
        return updateCondition(conditionInterface, requestedDay);

    }

    @Override
    public WeatherConditionInterface updateCondition(WeatherConditionInterface condition, Date day) {
        WeatherConditionInterface conditionInterface = null;
        Calendar conditionDate = Calendar.getInstance();
        // conditionDate.add(Calendar.DAY_OF_YEAR, day);

        condition.setDate(day);
        condition.setDayofYear(conditionDate.get(Calendar.DAY_OF_YEAR));

        WeatherConditionInterface wc = super.getWeatherByDayofyear(conditionDate.get(Calendar.DAY_OF_YEAR));

        if (wc == null)
            conditionInterface = super.insertWeather(condition);
        else
            conditionInterface = super.updateWeather(condition);
        return conditionInterface;
    }
}
