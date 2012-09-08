package org.gots.weather.provider;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherSet;
import org.gots.weather.exception.UnknownWeatherException;
import org.gots.weather.sql.WeatherDBHelper;

import android.content.Context;

public class DatabaseWeatherTask extends WeatherTask {

	private Context mContext;
	private Date weatherDate;

	public DatabaseWeatherTask(Context mContext, Date weatherDate) {
		this.mContext = mContext;
		this.weatherDate = weatherDate;
	}

	@Override
	protected WeatherConditionInterface doInBackground(Object... params) {
		Calendar weatherday = new GregorianCalendar();
		WeatherConditionInterface wc;

		weatherday.setTime(weatherDate);
		// if (params[0] != null)
//		weatherday.add(Calendar.DAY_OF_YEAR, this.i);

		WeatherDBHelper helper = new WeatherDBHelper(mContext);
		wc = helper.getWeatherByDayofyear(weatherday.get(Calendar.DAY_OF_YEAR));

		if (wc == null)
			wc = new WeatherCondition(weatherDate);
		// throw new UnknownWeatherException();

		return wc;
	}

}
