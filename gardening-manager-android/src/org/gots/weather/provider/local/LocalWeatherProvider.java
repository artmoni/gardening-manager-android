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

import java.util.Calendar;
import java.util.Date;

import org.gots.DatabaseHelper;
import org.gots.garden.GardenInterface;
import org.gots.utils.GotsDBHelper;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.exception.UnknownWeatherException;
import org.gots.weather.provider.previmeteo.WeatherProvider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

public class LocalWeatherProvider extends GotsDBHelper implements WeatherProvider {

    public static final short WEATHER_OK = 0;

    public static final short WEATHER_ERROR_CITY_UNKNOWN = 1;

    protected static final short WEATHER_ERROR_UNKNOWN = 2;

    public LocalWeatherProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public WeatherConditionInterface insertCondition(WeatherConditionInterface weatherCondition) {
        long rowid;
        ContentValues values = getWeatherContentValues(weatherCondition);

        rowid = bdd.insert(DatabaseHelper.WEATHER_TABLE_NAME, null, values);

        weatherCondition.setId((int) rowid);
        return weatherCondition;
    }

    // public WeatherConditionInterface updateWeather(WeatherConditionInterface weatherCondition) {
    // ContentValues values = getWeatherContentValues(weatherCondition);
    //
    // bdd.update(DatabaseHelper.WEATHER_TABLE_NAME, values,
    // DatabaseHelper.WEATHER_DAYOFYEAR + "=" + weatherCondition.getDayofYear(), null);
    //
    // Cursor cursor = bdd.query(DatabaseHelper.WEATHER_TABLE_NAME, null, DatabaseHelper.WEATHER_DAYOFYEAR + "='"
    // + weatherCondition.getDayofYear() + "'", null, null, null, null);
    //
    // if (cursor.moveToFirst()) {
    // int weatherid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_ID));
    // weatherCondition.setId(weatherid);
    // }
    // return weatherCondition;
    // }

    private ContentValues getWeatherContentValues(WeatherConditionInterface weatherCondition) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.WEATHER_CONDITION, weatherCondition.getSummary());
        values.put(DatabaseHelper.WEATHER_WINDCONDITION, weatherCondition.getWindCondition());
        Calendar cal = Calendar.getInstance();
        cal.setTime(weatherCondition.getDate());
        values.put(DatabaseHelper.WEATHER_DATE, cal.getTimeInMillis());
        values.put(DatabaseHelper.WEATHER_YEAR, cal.get(Calendar.YEAR));
        values.put(DatabaseHelper.WEATHER_DAYOFYEAR, weatherCondition.getDayofYear());
        values.put(DatabaseHelper.WEATHER_HUMIDITY, weatherCondition.getHumidity());
        values.put(DatabaseHelper.WEATHER_ICONURL, weatherCondition.getIconURL());
        values.put(DatabaseHelper.WEATHER_TEMPCELCIUSMIN, weatherCondition.getTempCelciusMin());
        values.put(DatabaseHelper.WEATHER_TEMPCELCIUSMAX, weatherCondition.getTempCelciusMax());
        values.put(DatabaseHelper.WEATHER_TEMPFAHRENHEIT, weatherCondition.getTempFahrenheit());
        values.put(DatabaseHelper.WEATHER_UUID, weatherCondition.getUUID());
        return values;
    }

    private WeatherConditionInterface cursorToWeather(Cursor cursor) {
        WeatherConditionInterface condition = new WeatherCondition();
        condition.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_ID)));
        condition.setSummary(cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_CONDITION)));
        condition.setWindCondition(cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_WINDCONDITION)));
        condition.setDayofYear(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_DAYOFYEAR)));
        condition.setIconURL((cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_ICONURL))));
        condition.setHumidity((cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.WEATHER_HUMIDITY))));
        condition.setTempCelciusMin((cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.WEATHER_TEMPCELCIUSMIN))));
        condition.setTempCelciusMax((cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.WEATHER_TEMPCELCIUSMAX))));
        condition.setTempFahrenheit((cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.WEATHER_TEMPFAHRENHEIT))));
        condition.setDate(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.WEATHER_DATE))));
        condition.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.WEATHER_UUID)));
        return condition;
    }

    @Override
    public WeatherConditionInterface getCondition(Date requestedDay) throws UnknownWeatherException {
        WeatherConditionInterface weatherCondition = null;
        Cursor cursor = null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(requestedDay);
        int dayofyear = cal.get(Calendar.DAY_OF_YEAR);
        int year = cal.get(Calendar.YEAR);
        try {
            cursor = bdd.query(DatabaseHelper.WEATHER_TABLE_NAME, null, DatabaseHelper.WEATHER_DAYOFYEAR + "="
                    + dayofyear + " AND " + DatabaseHelper.WEATHER_YEAR + "=" + year, null, null, null, null);

            if (cursor.moveToFirst()) {
                weatherCondition = cursorToWeather(cursor);
            } else
                throw new UnknownWeatherException();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return weatherCondition;
    }

    @Override
    public WeatherConditionInterface updateCondition(WeatherConditionInterface weatherCondition) {
        ContentValues values = getWeatherContentValues(weatherCondition);

        bdd.update(DatabaseHelper.WEATHER_TABLE_NAME, values,
                DatabaseHelper.WEATHER_DAYOFYEAR + "=" + weatherCondition.getDayofYear(), null);

        Cursor cursor = bdd.query(DatabaseHelper.WEATHER_TABLE_NAME, null, DatabaseHelper.WEATHER_DAYOFYEAR + "='"
                + weatherCondition.getDayofYear() + "'", null, null, null, null);

        if (cursor.moveToFirst()) {
            int weatherid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.WEATHER_ID));
            weatherCondition.setId(weatherid);
        }
        return weatherCondition;
    }

    @Override
    public short fetchWeatherForecast(GardenInterface gardenInterface) {
        // if database access is right, forecast can be fetch
        return bdd.isOpen() ? WEATHER_OK : WEATHER_ERROR_CITY_UNKNOWN;
    }

    @Override
    public long getNbConditionsHistory() {
        return DatabaseUtils.queryNumEntries(bdd, DatabaseHelper.WEATHER_TABLE_NAME);
    }

}
