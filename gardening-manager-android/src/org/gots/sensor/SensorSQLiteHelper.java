package org.gots.sensor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SensorSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_FERTILIZER = "fertilizer";

    public static final String FERTILIZER_ID = "_id";

    public static final String FERTILIZER_REMOTE_ID = "remote_id";

    public static final String FERTILIZER_watering_cycle_start_date_time_utc = "watering_cycle_start_date_time_utc";

    public static final String FERTILIZER_watering_cycle_end_date_time_utc = "watering_cycle_end_date_time_utc";

    public static final String FERTILIZER_LEVEL = "fertilizer_level";

    private static final String DATABASE_NAME = "sensor.db";

    //@formatter:off
    private static final String FERTILIZER_CREATE = "create table " + TABLE_FERTILIZER + "(" 
            + FERTILIZER_ID + " INTEGER primary key autoincrement,"
            + FERTILIZER_REMOTE_ID + " INTEGER ,"
            + FERTILIZER_LEVEL + " REAL,"
            + FERTILIZER_watering_cycle_end_date_time_utc + " INTEGER,"
            + FERTILIZER_watering_cycle_start_date_time_utc + " INTEGER);";
    //@formatter:on

    public static final String TABLE_TEMPERATURE = "temperature";

    public static final String TEMPERATURE_ID = "_id";

    public static final String TEMPERATURE_capture_ts = "capture_ts";

    public static final String TEMPERATURE_air_temperature_celsius = "air_temperature_celsius";

    public static final String TEMPERATURE_par_umole_m2s = "par_umole_m2s";

    public static final String TEMPERATURE_vwc_percent = "vwc_percent";

    //@formatter:off
    private static final String TEMPERATURE_CREATE = "create table " + TABLE_TEMPERATURE + "(" 
            + TEMPERATURE_ID + " INTEGER primary key autoincrement,"
            + TEMPERATURE_capture_ts + " INTEGER,"
            + TEMPERATURE_air_temperature_celsius + " REAL,"
            + TEMPERATURE_vwc_percent + " REAL,"
            + TEMPERATURE_par_umole_m2s + " REAL);";
    //@formatter:on

    public SensorSQLiteHelper(Context context, String locationId) {
        super(context, DATABASE_NAME + "-" + locationId, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(FERTILIZER_CREATE);
        database.execSQL(TEMPERATURE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SensorSQLiteHelper.class.getName(), "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FERTILIZER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMPERATURE);
        onCreate(db);
    }

}
