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
package org.gots.garden.sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class GardenDBHelper {

    private static final String TAG = "GardenDBHelper";

    // private List<BaseAllotmentInterface> allAllotment = new
    // ArrayList<BaseAllotmentInterface>();

    private GardenSQLite gardenSQLite;

    private SQLiteDatabase bdd;

    Context mContext;

    public GardenDBHelper(Context mContext) {
        gardenSQLite = new GardenSQLite(mContext);
        this.mContext = mContext;
    }

    public void open() {
        // on ouvre la BDD en écriture
        bdd = gardenSQLite.getWritableDatabase();
    }

    public void close() {
        // on ferme l'accès à la BDD
        bdd.close();
    }

    public GardenInterface insertGarden(GardenInterface garden) {
        long rowid;
        open();
        ContentValues values = new ContentValues();
        values.put(GardenSQLite.GARDEN_UUID, garden.getUUID());

        values.put(GardenSQLite.GARDEN_ADMINAREA, garden.getAdminArea());
        values.put(GardenSQLite.GARDEN_COUNTRYNAME, garden.getCountryName());
        values.put(GardenSQLite.GARDEN_LOCALITY, garden.getLocality());

        rowid = bdd.insert(GardenSQLite.GARDEN_TABLE_NAME, null, values);
        garden.setId(rowid);
        close();
        return garden;
    }

    public GardenInterface getGarden(int gardenId) {
        GardenInterface garden = null;
        // SeedActionInterface searchedSeed = new GrowingSeed();
        open();
        Cursor cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null,
                GardenSQLite.GARDEN_ID + "=" + gardenId, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                garden = cursorToGarden(cursor);

            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return garden;
    }

    private GardenInterface cursorToGarden(Cursor cursor) {
        GardenInterface garden = new Garden();
        // ActionFactory factory = new ActionFactory();
        // lot =
        // factory.buildAction(mContext,cursor.getString(cursor.getColumnIndex(GardenDatabase.ACTION_NAME)));
        // lot.setId(cursor.getInt(cursor.getColumnIndex(GardenDatabase.ACTION_ID)));
        garden.setId(cursor.getInt(cursor.getColumnIndex(GardenSQLite.GARDEN_ID)));

        garden.setAdminArea(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_ADMINAREA)));
        garden.setCountryName(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_COUNTRYNAME)));
        garden.setLocality(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_LOCALITY)));
        garden.setDateLastSynchro(new Date(
                cursor.getInt(cursor.getColumnIndex(GardenSQLite.GARDEN_LAST_SYNCHRO))));
        garden.setUUID(cursor.getString(cursor.getColumnIndex(GardenSQLite.GARDEN_UUID)));
        return garden;
    }

    public GardenInterface updateGarden(GardenInterface garden) {
        open();
        ContentValues values = new ContentValues();
        values.put(GardenSQLite.GARDEN_ADMINAREA, garden.getAdminArea());
        values.put(GardenSQLite.GARDEN_COUNTRYNAME, garden.getCountryName());
        values.put(GardenSQLite.GARDEN_LOCALITY, garden.getLocality());
        values.put(GardenSQLite.GARDEN_LATITUDE, garden.getGpsLatitude());
        values.put(GardenSQLite.GARDEN_LONGITUDE, garden.getGpsLongitude());
        values.put(GardenSQLite.GARDEN_ALTITUDE, garden.getGpsAltitude());
        values.put(GardenSQLite.GARDEN_UUID, garden.getUUID());

        int nbRows = bdd.update(GardenSQLite.GARDEN_TABLE_NAME, values,
                GardenSQLite.GARDEN_ID + "='" + garden.getId() + "'", null);
        Log.d(TAG,
                "Updating " + nbRows + " garden named " + garden.getLocality());
        close();
        return garden;
    }

    public List<GardenInterface> getGardens() {
        List<GardenInterface> gardens = new ArrayList<GardenInterface>();
        open();
        Cursor cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                GardenInterface garden = cursorToGarden(cursor);
                gardens.add(garden);
            } while (cursor.moveToNext());
        }
        cursor.close();
        close();
        return gardens;
    }

    public void deleteGarden(GardenInterface garden) {
        open();
        int rowid = bdd.delete(GardenSQLite.GARDEN_TABLE_NAME,
                GardenSQLite.GARDEN_ID + "=" + garden.getId(), null);
        if (rowid != 1) {
            Log.w("deleteGarden", "Garden id=" + garden.getId()
                    + " has not been found");
        }
        close();
    }

    public int getCountGarden() {
        int nbGarden;
        // SeedActionInterface searchedSeed = new GrowingSeed();
        open();
        Cursor cursor = bdd.query(GardenSQLite.GARDEN_TABLE_NAME, null, null,
                null, null, null, null);
        nbGarden = cursor.getCount();

        cursor.close();
        close();
        return nbGarden;
    }

}
