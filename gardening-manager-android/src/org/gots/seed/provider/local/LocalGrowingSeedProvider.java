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
package org.gots.seed.provider.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class LocalGrowingSeedProvider extends GotsDBHelper implements GotsGrowingSeedProvider {

    public LocalGrowingSeedProvider(Context mContext) {
        super(mContext);
    }

    // @Override
    // protected void finalize() throws Throwable {
    // if (bdd != null)
    // bdd.close();
    // super.finalize();
    // }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#insertSeed(org.gots.seed.GrowingSeedInterface,
     * java.lang.String)
     */
    @Override
    public GrowingSeedInterface plantingSeed(GrowingSeedInterface growingSeed, BaseAllotmentInterface allotment) {
        long rowid;

        rowid = bdd.insert(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, seedToValues(growingSeed, allotment));
        growingSeed.setGrowingSeedId((int) rowid);

        return growingSeed;
    }

    protected ContentValues seedToValues(GrowingSeedInterface seed, BaseAllotmentInterface allotment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.GROWINGSEED_SEED_ID, seed.getSeedId());
        values.put(DatabaseHelper.GROWINGSEED_UUID, seed.getUUID());

        values.put(DatabaseHelper.GROWINGSEED_ALLOTMENT_ID, allotment.getName());
        if (seed.getDateSowing() != null)
            values.put(DatabaseHelper.GROWINGSEED_DATESOWING, seed.getDateSowing().getTime());
        if (seed.getDateLastWatering() != null)
            values.put(DatabaseHelper.GROWINGSEED_DATELASTWATERING, seed.getDateLastWatering().getTime());
        return values;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#getGrowingSeeds()
     */
    @Override
    public ArrayList<GrowingSeedInterface> getGrowingSeeds() {
        ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
        GrowingSeedInterface searchedSeed = new GrowingSeed();
        // open();

        try {
            // TODO replace allotment reference to Id instead of name
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
        GotsSeedProvider localSeedProvider = new LocalSeedProvider(mContext);
        bsi = (GrowingSeedInterface) localSeedProvider.getSeedById(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_SEED_ID)));
        if (bsi == null) {
            bsi = new GrowingSeed();
            bsi.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_SEED_ID)));
        }
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_UUID)));
        bsi.setGrowingSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_ID)));
        bsi.setDateSowing(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATESOWING))));
        bsi.setDateLastWatering(new Date(
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATELASTWATERING))));

        return bsi;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#getSeedsByAllotment(java.lang.String)
     */
    @Override
    public List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force) {
        ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
        GrowingSeedInterface searchedSeed = new GrowingSeed();

        Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_ALLOTMENT_ID
                + "='" + allotment.getName() + "'", null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(cursor);
                allSeeds.add(searchedSeed);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return allSeeds;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#getSeedById(int)
     */
    @Override
    public GrowingSeedInterface getGrowingSeedById(int growingSeedId) {
        GrowingSeedInterface searchedSeed = null;

        Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_ID + "='"
                + growingSeedId + "'", null, null, null, null);

        if (cursor.moveToFirst()) {

            searchedSeed = cursorToSeed(cursor);

        }
        cursor.close();
        return searchedSeed;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#deleteGrowingSeed(org.gots.seed.GrowingSeedInterface)
     */
    @Override
    public void deleteGrowingSeed(GrowingSeedInterface seed) {

        bdd.delete(DatabaseHelper.GROWINGSEEDS_TABLE_NAME,
                DatabaseHelper.GROWINGSEED_ID + "='" + seed.getGrowingSeedId() + "'", null);
    }

    public GrowingSeedInterface updateGrowingSeed(GrowingSeedInterface seed, BaseAllotmentInterface allotment) {

        // Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = seedToValues(seed, allotment);

        bdd.update(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, values,
                DatabaseHelper.GROWINGSEED_ID + "='" + seed.getGrowingSeedId() + "'", null);

        return seed;
    }

    public GrowingSeedInterface getGrowingSeedsByUUID(String uuid) {
        GrowingSeedInterface searchedSeed = null;

        Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_UUID + "='"
                + uuid + "'", null, null, null, null);

        if (cursor.moveToFirst()) {

            searchedSeed = cursorToSeed(cursor);

        }
        cursor.close();
        return searchedSeed;
    }

}
