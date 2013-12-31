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
package org.gots.weather.provider.local;

import java.util.Date;

import org.gots.DatabaseHelper;
import org.gots.garden.provider.local.GardenSQLite;
import org.gots.utils.GotsDBHelper;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class LocalWeatherProvider extends GotsDBHelper {

    public LocalWeatherProvider(Context mContext) {
        super(mContext);
    }

    public WeatherConditionInterface insertWeather(WeatherConditionInterface weatherCondition) {
        long rowid;
        ContentValues values = getWeatherContentValues(weatherCondition);

        rowid = bdd.insert(DatabaseHelper.WEATHER_TABLE_NAME, null, values);

        weatherCondition.setId((int) rowid);
        return weatherCondition;
    }

    public WeatherConditionInterface updateWeather(WeatherConditionInterface weatherCondition) {
        long rowid;
        ContentValues values = getWeatherContentValues(weatherCondition);

        rowid = bdd.update(DatabaseHelper.WEATHER_TABLE_NAME, values, DatabaseHelper.WEATHER_DAYOFYEAR + "="
                + weatherCondition.getDayofYear(), null);

        Cursor cursor = bdd.query(DatabaseHelper.WEATHER_TABLE_NAME, null, DatabaseHelper.WEATHER_DAYOFYEAR + "='"
                + weatherCondition.getDayofYear() + "'", null, null, null, null);

        if (cursor.moveToFirst()) {
            int weatherid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_ID));
            weatherCondition.setId(weatherid);
        }
        return weatherCondition;
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
        condition.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_ID)));
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
        Cursor cursor = null;
        try {
            cursor = bdd.query(DatabaseHelper.WEATHER_TABLE_NAME, null, DatabaseHelper.WEATHER_DAYOFYEAR + "="
                    + dayofyear, null, null, null, null);

            if (cursor.moveToFirst()) {
                weatherCondition = cursorToWeather(cursor);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return weatherCondition;
    }

}
