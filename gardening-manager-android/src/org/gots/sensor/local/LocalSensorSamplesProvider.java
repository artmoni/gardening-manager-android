package org.gots.sensor.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gots.sensor.GotsSensorSamplesProvider;
import org.gots.sensor.SensorSQLiteHelper;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocalSensorSamplesProvider implements GotsSensorSamplesProvider {

    private static final String TAG = null;

    private SensorSQLiteHelper dbHelper;

    private SQLiteDatabase database;

    private String locationIdentifier;

    public LocalSensorSamplesProvider(Context context, String locationId) {
        dbHelper = new SensorSQLiteHelper(context, locationId);
        database = dbHelper.getWritableDatabase();
        locationIdentifier = locationId;
    }

    @Override
    protected void finalize() throws Throwable {
        if (database != null && database.isOpen())
            dbHelper.close();
        super.finalize();
    }

    // public void open() throws SQLException {
    // database = dbHelper.getWritableDatabase();
    // }

    public void close() {
        dbHelper.close();
    }

    public List<ParrotSampleTemperature> getSamplesTemperature(Date from, Date to) {

        List<ParrotSampleTemperature> temperatures = new ArrayList<ParrotSampleTemperature>();
        String fromStr = new String();
        String toStr = new String();
        if (from != null) {
            fromStr = SensorSQLiteHelper.TEMPERATURE_capture_ts + ">=" + from.getTime();
        }

        if (to != null) {
            toStr = SensorSQLiteHelper.TEMPERATURE_capture_ts + "<=" + to.getTime();
        }
        String selection = null;
        if (!"".equals(fromStr) && !"".equals(toStr))
            selection = fromStr + " AND " + toStr;
        else if (!"".equals(toStr))
            selection = toStr;
        else if (!"".equals(fromStr))
            selection = fromStr;
        Cursor cursor = database.query(SensorSQLiteHelper.TABLE_TEMPERATURE, null, selection, null, null, null, null);
        while (cursor.moveToNext()) {
            ParrotSampleTemperature temperature = cursorToTemperature(cursor);
            temperatures.add(temperature);
        }

        cursor.close();
        close();
        return temperatures;
    }

    public List<ParrotSampleFertilizer> getSamplesFertilizer(Date from, Date to) {
        // open();
        List<ParrotSampleFertilizer> fertilizers = new ArrayList<ParrotSampleFertilizer>();
        String fromStr = new String();
        String toStr = new String();
        if (from != null) {
            fromStr = SensorSQLiteHelper.FERTILIZER_watering_cycle_start_date_time_utc + ">=" + from.getTime();
        }

        if (to != null) {
            toStr = SensorSQLiteHelper.FERTILIZER_watering_cycle_end_date_time_utc + "<=" + to.getTime();
        }
        String selection = null;
        if (!"".equals(fromStr) && !"".equals(toStr))
            selection = fromStr + " AND " + toStr;
        else if (!"".equals(toStr))
            selection = toStr;
        else if (!"".equals(fromStr))
            selection = fromStr;
        Cursor cursor = database.query(SensorSQLiteHelper.TABLE_FERTILIZER, null, selection, null, null, null, null);
        while (cursor.moveToNext()) {
            ParrotSampleFertilizer fertilizer = cursorToFertilizer(cursor);
            fertilizers.add(fertilizer);
        }

        cursor.close();
        // close();
        return fertilizers;
    }

    public List<ParrotLocation> getLocations() {
        return null;
    }

    public List<ParrotLocationsStatus> getStatus() {
        return null;
    }

    public List<ParrotSensor> getSensors() {
        return null;
    }

    @Override
    public void insertSampleTemperature(ParrotSampleTemperature parrotSampleTemperature) {
        ContentValues values = temperatureToValues(parrotSampleTemperature);
        // open();
        Cursor cursor = database.query(SensorSQLiteHelper.TABLE_TEMPERATURE, null,
                SensorSQLiteHelper.TEMPERATURE_capture_ts + "=" + parrotSampleTemperature.getCapture_ts().getTime(),
                null, null, null, null);
        if (cursor.getCount() == 0) {
            long insertId = database.insert(SensorSQLiteHelper.TABLE_TEMPERATURE, null, values);
            Log.d(TAG, parrotSampleTemperature + " has been inserted in database");
        } else {
            Log.i(TAG, parrotSampleTemperature + " is already inserted in database");
        }
        cursor.close();
        // close();
    }

    @Override
    public long insertSampleFertilizer(ParrotSampleFertilizer parrotSampleFertilizer) {
        ContentValues values = fertilizerToValues(parrotSampleFertilizer);
        // open();
        long insertId = -1;
        Cursor cursor = database.query(SensorSQLiteHelper.TABLE_FERTILIZER, null,
                SensorSQLiteHelper.FERTILIZER_REMOTE_ID + "=" + parrotSampleFertilizer.getId(), null, null, null, null);
        if (cursor.getCount() == 0) {
            insertId = database.insert(SensorSQLiteHelper.TABLE_FERTILIZER, null, values);
            parrotSampleFertilizer.setId((int) insertId);
            Log.d(TAG, "[" + locationIdentifier + "]" + parrotSampleFertilizer + " has been inserted in database");

        } else {
            Log.i(TAG, "[" + locationIdentifier + "]" + parrotSampleFertilizer + " is already inserted in database");

        }
        cursor.close();
        return insertId;
    }

    protected ContentValues fertilizerToValues(ParrotSampleFertilizer parrotSampleFertilizer) {
        ContentValues values = new ContentValues();
        values.put(SensorSQLiteHelper.FERTILIZER_REMOTE_ID, parrotSampleFertilizer.getId());
        values.put(SensorSQLiteHelper.FERTILIZER_LEVEL, parrotSampleFertilizer.getFertilizer_level());
        values.put(SensorSQLiteHelper.FERTILIZER_watering_cycle_end_date_time_utc,
                parrotSampleFertilizer.getWatering_cycle_end_date_time_utc().getTime());
        values.put(SensorSQLiteHelper.FERTILIZER_watering_cycle_start_date_time_utc,
                parrotSampleFertilizer.getWatering_cycle_start_date_time_utc().getTime());
        return values;
    }

    private ParrotSampleFertilizer cursorToFertilizer(Cursor cursor) {
        ParrotSampleFertilizer fertilizer = new ParrotSampleFertilizer();
        fertilizer.setId(cursor.getInt(cursor.getColumnIndex(SensorSQLiteHelper.FERTILIZER_REMOTE_ID)));
        fertilizer.setFertilizer_level(cursor.getDouble(cursor.getColumnIndex(SensorSQLiteHelper.FERTILIZER_LEVEL)));
        fertilizer.setWatering_cycle_end_date_time_utc(new Date(
                cursor.getLong(cursor.getColumnIndex(SensorSQLiteHelper.FERTILIZER_watering_cycle_end_date_time_utc))));
        fertilizer.setWatering_cycle_start_date_time_utc(new Date(
                cursor.getLong(cursor.getColumnIndex(SensorSQLiteHelper.FERTILIZER_watering_cycle_start_date_time_utc))));
        return fertilizer;
    }

    protected ContentValues temperatureToValues(ParrotSampleTemperature parrotSampleTemperature) {
        ContentValues values = new ContentValues();
        values.put(SensorSQLiteHelper.TEMPERATURE_air_temperature_celsius,
                parrotSampleTemperature.getAir_temperature_celsius());
        values.put(SensorSQLiteHelper.TEMPERATURE_capture_ts, parrotSampleTemperature.getCapture_ts().getTime());
        values.put(SensorSQLiteHelper.TEMPERATURE_par_umole_m2s, parrotSampleTemperature.getPar_umole_m2s());
        values.put(SensorSQLiteHelper.TEMPERATURE_vwc_percent, parrotSampleTemperature.getVwc_percent());
        return values;
    }

    private ParrotSampleTemperature cursorToTemperature(Cursor cursor) {
        ParrotSampleTemperature temperature = new ParrotSampleTemperature();
        temperature.setAir_temperature_celsius(cursor.getDouble(cursor.getColumnIndex(SensorSQLiteHelper.TEMPERATURE_air_temperature_celsius)));
        temperature.setCapture_ts(new Date(
                cursor.getLong(cursor.getColumnIndex(SensorSQLiteHelper.TEMPERATURE_capture_ts))));
        temperature.setPar_umole_m2s(cursor.getDouble(cursor.getColumnIndex(SensorSQLiteHelper.TEMPERATURE_par_umole_m2s)));
        temperature.setVwc_percent(cursor.getDouble(cursor.getColumnIndex(SensorSQLiteHelper.TEMPERATURE_vwc_percent)));
        return temperature;
    }

    public ParrotSampleTemperature getLastSampleTemperature() {
        // open();
        ParrotSampleTemperature temperature = null;
        String query = "SELECT * from " + SensorSQLiteHelper.TABLE_TEMPERATURE + " order by "
                + SensorSQLiteHelper.TEMPERATURE_capture_ts + " DESC limit 1";
        Cursor cursor = database.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            temperature = cursorToTemperature(cursor);
        }

        cursor.close();
        // close();
        return temperature;
    }
}
