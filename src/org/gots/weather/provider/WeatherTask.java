package org.gots.weather.provider;


import org.gots.weather.WeatherConditionInterface;

import android.os.AsyncTask;

public abstract class WeatherTask extends AsyncTask<Object, Integer, WeatherConditionInterface> {
	

	public WeatherTask() {
		super();
	}

	
}