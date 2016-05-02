/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.garden.provider.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.gots.utils.GotsDBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GardenDBHelper extends GotsDBHelper {

    private static final String TAG = "GardenDBHelper";

    public GardenDBHelper(Context mContext) {
        super(mContext, GotsDBHelper.DATABASE_GARDEN_TYPE);
    }

    public synchronized GardenInterface insertGarden(GardenInterface garden) {
        long rowid;
        ContentValues values = gardenToValues(garden);

        rowid = bdd.insert(GardenSQLite.GARDEN_TABLE_NAME, null, values);
        garden.setId(rowid);
        return garden;
    }

    private ContentValues gardenToValues(GardenInterface garden) {
        ContentValues values = new ContentValues();
        values.put(GardenSQLite.GARDEN_UUID, garden.getUUID());
        values.put(GardenSQLite.GARDEN_NAME, garden.getName());
        values.put(GardenSQLite.GARDEN_ADMINAREA, garden.getAdminArea());
        values.put(GardenSQLite.GARDEN_COUNTRYNAME, garden.getCountryName());
        values.put(GardenSQLite.GARDEN_LOCALITY, garden.getLocality());
        values.put(GardenSQLite.GARDEN_LOCALITY_FORECAST, garden.getLocalityForecast());
        values.put(GardenSQLite.GARDEN_ALTITUDE, garden.getGpsAltitude());
        values.put(GardenSQLite.GARDEN_LATITUDE, garden.getGpsLatitude());
        values.put(GardenSQLite.GARDEN_LONGITUDE, garden.getGpsLongitude());

        return values;
    }

    private GardenInterface cursorToGarden(Cursor cursor) {
        GardenInterface garden = new Garden();
        garden.setId(cursor.getInt(cursor.getColumnIndex(GardenSQLite.GARDEN_ID)));
        garden.setName(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_NAME)));
        garden.setUUID(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_UUID)));
        garden.setAdminArea(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_ADMINAREA)));
        garden.setCountryName(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_COUNTRYNAME)));
        garden.setLocality(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_LOCALITY)));
        garden.setLocalityForecast(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_LOCALITY_FORECAST)));
        garden.setDateLastSynchro(new Date(cursor.getInt(cursor.getColumnIndex(GardenSQLite.GARDEN_LAST_SYNCHRO))));
        garden.setGpsAltitude(cursor.getDouble(cursor.getColumnIndex(GardenSQLite.GARDEN_ALTITUDE)));
        garden.setGpsLongitude(cursor.getDouble(cursor.getColumnIndex(GardenSQLite.GARDEN_LONGITUDE)));
        garden.setGpsLatitude(cursor.getDouble(cursor.getColumnIndex(GardenSQLite.GARDEN_LATITUDE)));
        return garden;
    }

    /*
     * getGarden
     * @param gardenId give you the specific garden, else -1 give you the first
     * garden available
     */
    public synchronized GardenInterface getGarden(int gardenId) {
        GardenInterface garden = null;
        // SeedActionInterface searchedSeed = new GrowingSeed();
        Cursor cursor;
        if (gardenId == -1)
            cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, null, null, null, null, null);
        else
            cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, GardenSQLite.GARDEN_ID + "=" + gardenId, null,
                    null, null, null);

        if (cursor.moveToFirst()) {
            garden = cursorToGarden(cursor);
        }
        cursor.close();
        return garden;
    }

    public synchronized GardenInterface updateGarden(GardenInterface garden) {
        ContentValues values = gardenToValues(garden);
        int nbRows;
        Cursor cursor;
        if (garden.getId() > 0) {
            nbRows = bdd.update(GardenSQLite.GARDEN_TABLE_NAME, values, GardenSQLite.GARDEN_ID + "='" + garden.getId()
                    + "'", null);
            cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, GardenSQLite.GARDEN_ID + "='" + garden.getId()
                    + "'", null, null, null, null);
        } else {

            nbRows = bdd.update(GardenSQLite.GARDEN_TABLE_NAME, values,
                    GardenSQLite.GARDEN_UUID + "='" + garden.getUUID() + "'", null);

            cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, GardenSQLite.GARDEN_UUID + "='" + garden.getUUID()
                    + "'", null, null, null, null);

            if (cursor.moveToFirst()) {
                int rowid = cursor.getInt(cursor.getColumnIndex(GardenSQLite.GARDEN_ID));
                garden.setId(rowid);
            }
            cursor.close();
        }
        Log.d(TAG, "Updating " + nbRows + " rows > " + garden);
        return garden;
    }

    public synchronized List<GardenInterface> getGardens() {
        List<GardenInterface> gardens = new ArrayList<GardenInterface>();
        Cursor cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                GardenInterface garden = cursorToGarden(cursor);
                gardens.add(garden);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return gardens;
    }

    public synchronized int deleteGarden(GardenInterface garden) {
        int rowid = bdd.delete(GardenSQLite.GARDEN_TABLE_NAME, GardenSQLite.GARDEN_ID + "=" + garden.getId(), null);
        if (rowid != 1) {
            Log.w("deleteGarden", "Garden=" + garden + " has not been found");
        }
        return rowid;
    }

    public synchronized int getCountGarden() {
        Cursor cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, null, null, null, null, null);
        int nbGarden = cursor.getCount();
        cursor.close();
        return nbGarden;
    }

}
