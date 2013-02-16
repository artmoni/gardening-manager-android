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
package org.gots.weather.sql;

import java.util.Date;

import org.gots.DatabaseHelper;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDBHelper {

	private DatabaseHelper databaseSQLite;
	private SQLiteDatabase bdd;
	Context mContext;

	public WeatherDBHelper(Context mContext) {
		databaseSQLite = new DatabaseHelper(mContext);
		this.mContext = mContext;
	}

	public void open() {
		// on ouvre la BDD en écriture
		bdd = databaseSQLite.getWritableDatabase();
	}

	public void close() {
		// on ferme l'accès à la BDD
		bdd.close();
	}

	public WeatherConditionInterface insertWeather(WeatherConditionInterface weatherCondition) {
		long rowid;
		open();
		ContentValues values = getWeatherContentValues(weatherCondition);

		try {

			rowid = bdd.insert(DatabaseHelper.WEATHER_TABLE_NAME, null, values);
		} finally {
			close();
		}

		weatherCondition.setId((int) rowid);
		return weatherCondition;
	}

	public long updateWeather(WeatherConditionInterface weatherCondition) {
		long rowid;
		open();
		ContentValues values = getWeatherContentValues(weatherCondition);

		try {

			rowid = bdd.update(DatabaseHelper.WEATHER_TABLE_NAME, values, DatabaseHelper.WEATHER_DAYOFYEAR + "="
					+ weatherCondition.getDayofYear(),null);
		} finally {
			close();
		}

		return rowid;
	}

	private ContentValues getWeatherContentValues(WeatherConditionInterface weatherCondition) {
		ContentValues values = new ContentValues();
		values.put(DatabaseHelper.WEATHER_CONDITION, weatherCondition.getCondition());
		values.put(DatabaseHelper.WEATHER_WINDCONDITION, weatherCondition.getWindCondition());
		values.put(DatabaseHelper.WEATHER_DATE, weatherCondition.getDate().getTime());
		values.put(DatabaseHelper.WEATHER_YEAR, weatherCondition.getDate().getYear());
		values.put(DatabaseHelper.WEATHER_DAYOFYEAR, weatherCondition.getDayofYear());
		values.put(DatabaseHelper.WEATHER_HUMIDITY, weatherCondition.getHumidity());
		values.put(DatabaseHelper.WEATHER_ICONURL, weatherCondition.getIconURL());
		values.put(DatabaseHelper.WEATHER_TEMPCELCIUSMIN, weatherCondition.getTempCelciusMin());
		values.put(DatabaseHelper.WEATHER_TEMPCELCIUSMAX, weatherCondition.getTempCelciusMax());
		values.put(DatabaseHelper.WEATHER_TEMPFAHRENHEIT, weatherCondition.getTempFahrenheit());
		return values;
	}

	private WeatherConditionInterface cursorToWeather(Cursor cursor) {
		WeatherConditionInterface condition = new WeatherCondition();
		condition.setCondition(cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_CONDITION)));
		condition.setWindCondition(cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_WINDCONDITION)));
		condition.setDayofYear(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_DAYOFYEAR)));
		condition.setIconURL((cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_ICONURL))));
		condition.setHumidity((cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_HUMIDITY))));
		condition.setTempCelciusMin((cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_TEMPCELCIUSMIN))));
		condition.setTempCelciusMax((cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_TEMPCELCIUSMAX))));
		condition.setTempFahrenheit((cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_TEMPFAHRENHEIT))));
		condition.setDate(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WEATHER_DATE))));
		return condition;
	}

	public WeatherConditionInterface getWeatherByDayofyear(int dayofyear) {
		WeatherConditionInterface weatherCondition = null;

		open();
		try {
			Cursor cursor = bdd.query(DatabaseHelper.WEATHER_TABLE_NAME, null, DatabaseHelper.WEATHER_DAYOFYEAR + "="
					+ dayofyear, null, null, null, null);

			if (cursor.moveToFirst()) {
				weatherCondition = cursorToWeather(cursor);
			}
			cursor.close();
		} finally {
			close();
		}
		return weatherCondition;
	}

}
