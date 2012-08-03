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
package org.gots.weather;

import java.io.ByteArrayInputStream;
import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.bean.Address;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.os.AsyncTask;
import android.util.Log;

public class WeatherTask extends AsyncTask<URL, Integer, WeatherSet> {
	private WeatherSet ws;
	private URL url;

	public WeatherTask(Address address) {
		try {
//			String queryString = "http://92.243.19.29/weather.xml";

			String queryString = "http://www.google.com/ig/api?weather=" + address.getLocality() + ","+ address.getCountryName();
			Log.i("WeatherTask", queryString);
			url = new URL(queryString.replace(" ", "%20"));
		} catch (Exception e) {
			Log.e("WeatherTask", e.getMessage());
		}
	}

	@Override
	protected WeatherSet doInBackground(URL... arg0) {
		try {

			// android.os.Debug.waitForDebugger();
			/*************/

			HttpClient httpclient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(url.toURI());
			Log.d("TEST", "executing request " + httpget.getURI());
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
			/* Parse the xml-data our URL-call returned. */
			xr.parse(new InputSource(is));

			/* Our Handler now provides the parsed weather-data to us. */
			ws = gwh.getWeatherSet();
		} catch (Exception e) {
			Log.e("WeatherManager", "WeatherQueryError", e);

		}

		return ws;
	}

}
