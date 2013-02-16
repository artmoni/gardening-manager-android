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
import java.util.List;

import org.gots.weather.provider.DatabaseWeatherTask;
import org.gots.weather.provider.MoonCalculation;
import org.gots.weather.provider.WeatherTask;

import android.content.Context;
import android.util.Log;

public class WeatherManager {

	private static WeatherManager instance;

	private WeatherSet ws;
	private Integer temperatureLimitHot;
	private Integer temperatureLimitCold;
	private Integer runningLimit;
	// private Date today;
	private Context mContext;


	public WeatherManager(Context context) {
		this.mContext = context;

		MoonCalculation moon = new MoonCalculation();
		Log.d("Moon phase", moon.phaseName(moon.moonPhase(2012, 12, 27)));



		
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

	
}
