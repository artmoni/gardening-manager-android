package org.gots.weather.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WeatherUpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
