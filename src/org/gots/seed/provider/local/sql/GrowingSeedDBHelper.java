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
package org.gots.seed.provider.local.sql;

import java.util.ArrayList;
import java.util.Date;

import org.gots.DatabaseHelper;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class GrowingSeedDBHelper extends GotsDBHelper {

    public GrowingSeedDBHelper(Context mContext) {
        super(mContext);
    }

    // @Override
    // protected void finalize() throws Throwable {
    // if (bdd != null)
    // bdd.close();
    // super.finalize();
    // }

    public GrowingSeedInterface insertSeed(GrowingSeedInterface seed, String allotmentReference) {
        long rowid;
        // open();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.GROWINGSEED_SEED_ID, seed.getSeedId());
        values.put(DatabaseHelper.GROWINGSEED_ALLOTMENT_ID, allotmentReference);
        try {
            if (seed.getDateSowing() != null)
                values.put(DatabaseHelper.GROWINGSEED_DATESOWING, seed.getDateSowing().getTime());
            if (seed.getDateLastWatering() != null)
                values.put(DatabaseHelper.GROWINGSEED_DATELASTWATERING, seed.getDateLastWatering().getTime());
            rowid = bdd.insert(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, values);
            seed.setGrowingSeedId((int) rowid);
        } finally {
            // close();
        }

        // seed.setId((int) rowid);
        return seed;
    }

    public ArrayList<GrowingSeedInterface> getGrowingSeeds() {
        ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
        GrowingSeedInterface searchedSeed = new GrowingSeed();
        // open();

        try {
            Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    searchedSeed = cursorToSeed(cursor);
                    allSeeds.add(searchedSeed);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } finally {
            // close();
        }
        return allSeeds;
    }

    private GrowingSeedInterface cursorToSeed(Cursor cursor) {
        GrowingSeedInterface bsi = null;
        VendorSeedDBHelper sb = new VendorSeedDBHelper(mContext);
        bsi = (GrowingSeedInterface) sb.getSeedById(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_SEED_ID)));
        if (bsi == null) {
            bsi = new GrowingSeed();
            bsi.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_SEED_ID)));
        }
        bsi.setGrowingSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_ID)));
        bsi.setDateSowing(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATESOWING))));
        bsi.setDateLastWatering(new Date(
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATELASTWATERING))));

        return bsi;
    }

    public ArrayList<GrowingSeedInterface> getSeedsByAllotment(String allotmentReference) {
        ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
        GrowingSeedInterface searchedSeed = new GrowingSeed();
        // open();

        try {
            Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null,
                    DatabaseHelper.GROWINGSEED_ALLOTMENT_ID + "='" + allotmentReference + "'", null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    searchedSeed = cursorToSeed(cursor);
                    allSeeds.add(searchedSeed);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } finally {
            // close();
        }
        return allSeeds;
    }

    public GrowingSeedInterface getSeedById(int growingSeedId) {
        GrowingSeedInterface searchedSeed = null;
        // open();

        try {
            Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_ID
                    + "='" + growingSeedId + "'", null, null, null, null);

            if (cursor.moveToFirst()) {

                searchedSeed = cursorToSeed(cursor);

            }
            cursor.close();
        } finally {
            // close();
        }
        return searchedSeed;
    }

    public void deleteGrowingSeed(GrowingSeedInterface seed) {
        // open();

        bdd.delete(DatabaseHelper.GROWINGSEEDS_TABLE_NAME,
                DatabaseHelper.GROWINGSEED_ID + "='" + seed.getGrowingSeedId() + "'", null);
        // close();
    }

}
