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
package org.gots.garden.provider.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import org.gots.action.AbstractActionSeed;

import java.util.ArrayList;

public class GardenSQLite extends SQLiteOpenHelper {
    // ************************ DATABASE **************
    private static final int DATABASE_VERSION = 17;

    private static String DATABASE_NAME = "garden";

    private static GardenSQLite helper;

    public final static String AUTHORITY = "org.gots.providers.garden";

    private static final String TAG = "GardenDatabase";

    // ************************ GARDEN TABLE **************
    public static final String GARDEN_TABLE_NAME = "garden";

    public static final String GARDEN_ID = "_id";

    public static final String GARDEN_UUID = "_uuid";

    public static final String GARDEN_LATITUDE = "latitude";

    public static final String GARDEN_LONGITUDE = "longitude";

    public static final String GARDEN_ALTITUDE = "altitude";

    public static final String GARDEN_LOCALITY = "locality";

    public static final String GARDEN_LOCALITY_FORECAST = "locality_forecast";

    public static final String GARDEN_ADMINAREA = "adminarea";

    public static final String GARDEN_COUNTRYNAME = "countryname";

    public static final String GARDEN_NAME = "name";

    public static final String GARDEN_LAST_SYNCHRO = "last_synchro";

    //@formatter:off
		public static final String CREATE_TABLE_GARDEN = "CREATE TABLE " + GARDEN_TABLE_NAME 
				+ " (" + GARDEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ GARDEN_UUID + " STRING,"
				+ GARDEN_NAME + " STRING,"
				+ GARDEN_LOCALITY + " STRING,"
				+ GARDEN_LOCALITY_FORECAST + " STRING,"
				+ GARDEN_ADMINAREA + " STRING,"
				+ GARDEN_COUNTRYNAME + " STRING,"
				+ GARDEN_ALTITUDE+ " INTEGER,"		
				+ GARDEN_LATITUDE + " INTEGER,"		
				+ GARDEN_LONGITUDE + " INTEGER,"
				+ GARDEN_LAST_SYNCHRO + " INTEGER"
				+ ");";
	//@formatter:on

    // ************************ ACTION TABLE **************
    public static final String ACTION_TABLE_NAME = "action";

    // public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
    // + "/action");
    // public static final String CONTENT_TYPE =
    // "vnd.android.cursor.dir/vnd.gots.action";

    // public static final String GROWINGSEED_ID = "growingseed_id";
    public static final String ACTION_ID = "_id";

    public static final String ACTION_UUID = "uuid";

    public static final String ACTION_NAME = "name";

    public static final String ACTION_DESCRIPTION = "description";

    public static final String ACTION_DURATION = "duration";

    //@formatter:off
		public static final String CREATE_TABLE_ACTION = "CREATE TABLE " + ACTION_TABLE_NAME 
				+ " (" + ACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ ACTION_UUID + " STRING,"
				+ ACTION_NAME + " STRING,"
				+ ACTION_DESCRIPTION + " STRING,"
				+ ACTION_DURATION + " INTEGER"			
				+ ");";
	//@formatter:on

    // ************************ FAMILY TABLE **************
    public static final String FAMILY_TABLE_NAME = "family";

    public static final String FAMILY_ID = "family_id";

    public static final String FAMILY_NAME = "family_name";

    //@formatter:off
    private static final String CREATE_TABLE_FAMILY = "CREATE TABLE " + FAMILY_TABLE_NAME
            + " ("+ FAMILY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + FAMILY_NAME + " VARCHAR(255)"

            + ");";
    //@formatter:on

    // ************************ SPECIE TABLE **************

    public static final String SPECIE_TABLE_NAME = "specie";

    public static final String SPECIE_ID = "specie_id";

    public static final String SPECIE_FAMILY_ID = "specie_family_id";

    public static final String SPECIE_NAME = "specie_name";

    //@formatter:off
    private static final String CREATE_TABLE_SPECIE = "CREATE TABLE " + SPECIE_TABLE_NAME
            + " ("+ SPECIE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SPECIE_FAMILY_ID + " INTEGER,"
            + SPECIE_NAME + " VARCHAR(255)"
            + ");";
    //@formatter:on
    // ************************ SEEDS TABLE **************
    public static final String SEEDS_TABLE_NAME = "seeds";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/seeds");

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.gots.seeds";

    public static final String SEED_ID = "_id";

    public static final String SEED_UUID = "uuid";

    public static final String SEED_BARECODE = "barcode";

    public static final String SEED_NAME = "name";

    public static final String SEED_DESCRIPTION_GROWTH = "description_growth";

    public static final String SEED_DESCRIPTION_CULTIVATION = "description_cultivation";

    public static final String SEED_DESCRIPTION_DISEASES = "description_diseases";

    public static final String SEED_DESCRIPTION_HARVEST = "description_harvest";

    public static final String SEED_ORDER = "botanicorder";

    public static final String SEED_FAMILY = "botanicfamily";

    public static final String SEED_GENUS = "botanicgenus";

    public static final String SEED_SPECIE = "botanicspecie";

    public static final String SEED_VARIETY = "variety";

    public static final ArrayList<AbstractActionSeed> actionToDo = new ArrayList<AbstractActionSeed>();

    public static final ArrayList<AbstractActionSeed> actionDone = new ArrayList<AbstractActionSeed>();

    public static final String SEED_DATESOWINGMIN = "datesowingmin";

    public static final String SEED_DATESOWINGMAX = "datesowingmax";

    public static final String SEED_DURATIONMIN = "durationmin";

    public static final String SEED_DURATIONMAX = "durationmax";

    public static final String SEED_URLDESCRIPTION = "urldescription";

    public static final String SEED_ACTION1 = "action1";

    public static final String SEED_NBSACHET = "nbsachet";

    public static final String SEED_LANGUAGE = "language";

    public static final String SEED_LIKE_COUNT = "likenumber";

    public static final String SEED_LIKE_STATUS = "likestatus";

    public static final String SEED_STATE = "state";

    //@formatter:off

    public static final String CREATE_TABLE_SEEDS = "CREATE TABLE " + SEEDS_TABLE_NAME
            + " (" + SEED_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + SEED_NAME + " VARCHAR(255),"
            + SEED_UUID + " VARCHAR(255),"
            + SEED_SPECIE + " VARCHAR(255),"
            + SEED_DESCRIPTION_GROWTH + " VARCHAR(255),"
            + SEED_DESCRIPTION_DISEASES + " VARCHAR(255),"
            + SEED_DESCRIPTION_CULTIVATION + " VARCHAR(255),"
            + SEED_DESCRIPTION_HARVEST + " VARCHAR(255),"
            + SEED_BARECODE + " VARCHAR(255),"
            + SEED_FAMILY + " VARCHAR(255),"
            + SEED_GENUS + " VARCHAR(255),"
            + SEED_ORDER + " VARCHAR(255),"
            + SEED_ACTION1 + " VARCHAR(255),"
            + SEED_VARIETY + " VARCHAR(255),"
            + SEED_URLDESCRIPTION + " VARCHAR(255),"
            + SEED_DATESOWINGMIN + " INTEGER,"
            + SEED_DATESOWINGMAX + " INTEGER,"
            + SEED_DURATIONMIN + " INTEGER,"
            + SEED_DURATIONMAX + " INTEGER,"
            + SEED_LANGUAGE + " STRING,"
            + SEED_NBSACHET + " INTEGER,"
            + SEED_LIKE_COUNT + " INTEGER,"
            + SEED_LIKE_STATUS + " INTEGER,"
            + SEED_STATE + " VARCHAR(255)"
            + ");";
    //@formatter:on

    private GardenSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized GardenSQLite getInstance(Context context) {
        if (helper == null) {
            helper = new GardenSQLite(context);
        }

        return helper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GARDEN);
        db.execSQL(CREATE_TABLE_ACTION);
        db.execSQL(CREATE_TABLE_FAMILY);
        db.execSQL(CREATE_TABLE_SPECIE);
        db.execSQL(CREATE_TABLE_SEEDS);

        // db.execSQL(CREATE_TABLE_ACTION);
//        populateActions(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        if (oldVersion <= 10) {
            db.execSQL(CREATE_TABLE_ACTION);
//            populateActions(db);

        }
        if (oldVersion < 12) {
            db.execSQL("Insert into " + ACTION_TABLE_NAME + "(" + ACTION_NAME + ") VALUES ('photo')");
        }
        if (oldVersion < 13) {
            db.execSQL("ALTER TABLE " + GARDEN_TABLE_NAME + " ADD COLUMN " + GARDEN_UUID + " VARCHAR(255);");
        }
        if (oldVersion < 14) {
            db.execSQL("ALTER TABLE " + ACTION_TABLE_NAME + " ADD COLUMN " + ACTION_UUID + " VARCHAR(255);");
        }
        if (oldVersion < 15) {
            db.execSQL("ALTER TABLE " + GARDEN_TABLE_NAME + " ADD COLUMN " + GARDEN_NAME + " VARCHAR(255);");
        }
        if (oldVersion < 16) {
            db.execSQL("ALTER TABLE " + GARDEN_TABLE_NAME + " ADD COLUMN " + GARDEN_LOCALITY_FORECAST
                    + " VARCHAR(255);");
        }
        if (oldVersion<17){
            db.execSQL(CREATE_TABLE_FAMILY);
            db.execSQL(CREATE_TABLE_SPECIE);
            db.execSQL(CREATE_TABLE_SEEDS);
        }
    }

}
