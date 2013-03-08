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
import org.gots.preferences.GotsPreferences;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherSet;
import org.gots.weather.provider.WeatherCache;
import org.gots.weather.provider.WeatherTask;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;
import android.util.Log;

public class PrevimeteoWeatherTask extends WeatherTask {
	private static final String TAG = "PrevimeteoWeatherTask";
	protected URL url;
	private Date requestedDay;
	private Context mContext;

	private static String queryString;
	private WeatherSet ws;
	private boolean iserror;

	private static int i = 0;
	private WeatherCache cache;

	public PrevimeteoWeatherTask(Context context, Address address, Date requestedDay) {
		this.requestedDay = requestedDay;
		mContext = context;

		try {
			String weatherURL;

			if (GotsPreferences.getInstance().isDEVELOPPEMENT())
				weatherURL = "http://www.gardening-manager.com/weather/weather-error.xml";
			else
				weatherURL = "http://api.previmeteo.com/" + GotsPreferences.getInstance().getWeatherApiKey()
						+ "/ig/api?weather=" + address.getLocality() + "," + address.getCountryName() + "&hl=fr";
			// weatherURL = "http://services.gardening-manager.com/previmeteo/"
			// + "/ig/api?weather="
			// + address.getLocality() + "," + address.getCountryName() +
			// "&hl=fr";

			queryString = weatherURL;

			url = new URL(queryString.replace(" ", "%20"));
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	@Override
	protected WeatherConditionInterface doInBackground(Object... arg0) {
		if (ws == null) {

			try {
				Log.d("WeatherConditionInterface", "" + (++i));
				cache = new WeatherCache();

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
				Log.e(TAG, "PrevimeteoErrorHandler has return an error " + e.getStackTrace().toString());

				iserror = true;

				return null;
			}
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

		if (iserror) {
			// Toast.makeText(mContext,
			// mContext.getResources().getString(R.string.weather_citynotfound),
			// 30).show();
			Log.w(TAG, "Error updating weather");

			// cache.clean(url);
		} else
			Log.d(TAG, "Weather updated from " + queryString);

		super.onPostExecute(result);
	}

}
