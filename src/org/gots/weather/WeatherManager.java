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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.weather.provider.DatabaseWeatherTask;
import org.gots.weather.provider.MoonCalculation;
import org.gots.weather.provider.WeatherTask;
import org.gots.weather.provider.previmeteo.PrevimeteoWeatherTask;
import org.gots.weather.sql.WeatherDBHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class WeatherManager {

	private static WeatherManager instance;

	private WeatherSet ws;
	private Integer temperatureLimitHot;
	private Integer temperatureLimitCold;
	private Integer runningLimit;
	// private Date today;
	private Context mContext;
	private static SharedPreferences preferences;
	private Calendar weatherday;

	private boolean isConnected = true;

	public WeatherManager(Context context) {
		this.mContext = context;

		MoonCalculation moon = new MoonCalculation();
		Log.d("Moon phase", moon.phaseName(moon.moonPhase(2012, 12, 27)));

		weatherday = new GregorianCalendar();

		preferences = mContext.getSharedPreferences("org.gots.preference", 0);

		GardenDBHelper helper = new GardenDBHelper(mContext);
		GardenInterface garden = helper.getGarden(preferences.getInt("org.gots.preference.gardenid", 0));
		// getWeatherFromWebService(garden);
	}

	public void getWeatherFromWebService(GardenInterface garden) {

		try {
			for (int forecastDay = 0; forecastDay < 4; forecastDay++) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DAY_OF_YEAR, forecastDay);

				// GoogleWeatherTask(garden.getAddress(), cal.getTime());
				WeatherTask wt = new PrevimeteoWeatherTask(mContext, garden.getAddress(), cal.getTime());
				WeatherConditionInterface conditionInterface = wt.execute().get();

				if (conditionInterface != null)
					updateCondition(conditionInterface, forecastDay);
				else {
					// Toast.makeText(mContext,
					// mContext.getResources().getString(R.string.weather_citynotfound),
					// 50)
					// .show();
					Log.d("getWeather",
							garden.getLocality() + " : "
									+ mContext.getResources().getString(R.string.weather_citynotfound));
					isConnected = false;
					break;
				}

			}

		} catch (Exception e) {
			if (e.getMessage() != null)
				Log.e("WeatherManager", e.getMessage());
			Toast.makeText(mContext, "Try another nearest city", 50).show();
		}

	}

	private void updateCondition(WeatherConditionInterface condition, int day) {
		WeatherDBHelper helper = new WeatherDBHelper(mContext);

		// weatherday.add(Calendar.DAY_OF_YEAR, day);
		Calendar conditionDate = Calendar.getInstance();
		conditionDate.setTime(weatherday.getTime());
		conditionDate.add(Calendar.DAY_OF_YEAR, day);

		condition.setDate(conditionDate.getTime());
		condition.setDayofYear(conditionDate.get(Calendar.DAY_OF_YEAR));

		WeatherConditionInterface wc = helper.getWeatherByDayofyear(conditionDate.get(Calendar.DAY_OF_YEAR));

		if (wc == null)
			helper.insertWeather(condition);
		else
			helper.updateWeather(condition);
		return;

	}

	public Integer getTemperatureLimitHot() {
		return temperatureLimitHot;
	}

	public void setTemperatureLimitHot(Integer temperatureLimitHot) {
		this.temperatureLimitHot = temperatureLimitHot;
	}

	public Integer getTemperatureLimitCold() {
		return temperatureLimitCold;
	}

	public void setTemperatureLimitCold(Integer temperatureLimitCold) {
		this.temperatureLimitCold = temperatureLimitCold;
	}

	public Integer getRunningLimit() {
		return runningLimit;
	}

	public void setRunningLimit(Integer runningLimit) {
		this.runningLimit = runningLimit;
	}

	/*
	 * GetCondition from today until passed argument (-i or +i)
	 */
	public WeatherConditionInterface getCondition(int i) {
		WeatherConditionInterface conditionInterface;

		Calendar weatherCalendar = Calendar.getInstance();
		weatherCalendar.add(Calendar.DAY_OF_YEAR, i);

		Date weatherDate = weatherCalendar.getTime();

		return getCondition(weatherDate);
	}

	public WeatherConditionInterface getCondition(Date weatherDate) {
		WeatherConditionInterface conditionInterface;
		try {
			WeatherTask wt = new DatabaseWeatherTask(mContext, weatherDate);
			conditionInterface = wt.execute().get();
		} catch (Exception e) {
			conditionInterface = new WeatherCondition(weatherDate);

		}
		return conditionInterface;
	}

	public List<WeatherConditionInterface> getConditionSet(int nbDays) {
		List<WeatherConditionInterface> conditions = new ArrayList<WeatherConditionInterface>();
		for (int i = -nbDays; i <= nbDays; i++) {

			try {

				conditions.add(getCondition(i));
			} catch (Exception e) {
				conditions.add(new WeatherCondition());

				e.printStackTrace();
			}
		}
		return conditions;
	}

	public boolean isConnected() {
		return isConnected;
	}
}
