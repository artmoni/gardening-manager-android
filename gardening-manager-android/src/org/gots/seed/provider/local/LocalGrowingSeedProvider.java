/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed.provider.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.gots.DatabaseHelper;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedImpl;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.utils.GotsDBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    public GrowingSeed plantingSeed(GrowingSeed growingSeed, BaseAllotmentInterface allotment) {
        long rowid;

        rowid = bdd.insert(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, seedToValues(growingSeed, allotment));
        growingSeed.setId((int) rowid);

        return growingSeed;
    }

    protected ContentValues seedToValues(GrowingSeed seed, BaseAllotmentInterface allotment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.GROWINGSEED_SEED_ID, seed.getPlant().getSeedId());
        values.put(DatabaseHelper.GROWINGSEED_UUID, seed.getPlant().getUUID());

        values.put(DatabaseHelper.GROWINGSEED_ALLOTMENT_ID, allotment.getId());
        if (seed.getDateSowing() != null)
            values.put(DatabaseHelper.GROWINGSEED_DATESOWING, seed.getDateSowing().getTime());
        if (seed.getDateLastWatering() != null)
            values.put(DatabaseHelper.GROWINGSEED_DATELASTWATERING, seed.getDateLastWatering().getTime());
        if (seed.getDateHarvest() != null)
            values.put(DatabaseHelper.GROWINGSEED_DATEHARVEST, seed.getDateHarvest().getTime());
        return values;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#getGrowingSeeds()
     */
    @Override
    public ArrayList<GrowingSeed> getGrowingSeeds() {
        ArrayList<GrowingSeed> allSeeds = new ArrayList<GrowingSeed>();
        GrowingSeed searchedSeed = new GrowingSeedImpl();
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

    private GrowingSeed cursorToSeed(Cursor cursor) {
        GrowingSeed bsi = new GrowingSeedImpl();
        bsi.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_ID)));
        GotsSeedProvider localSeedProvider = new LocalSeedProvider(mContext);
        bsi.setPlant(localSeedProvider.getSeedById(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_SEED_ID))));
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_UUID)));
        bsi.setDateSowing(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATESOWING))));
        bsi.setDateHarvest(new Date(cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATEHARVEST))));
        bsi.setDateLastWatering(new Date(
                cursor.getLong(cursor.getColumnIndex(DatabaseHelper.GROWINGSEED_DATELASTWATERING))));

        return bsi;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.seed.provider.local.GotsGrowingSeedProvider#getSeedsByAllotment(java.lang.String)
     */
    @Override
    public List<GrowingSeed> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force) {
        ArrayList<GrowingSeed> allSeeds = new ArrayList<GrowingSeed>();
        GrowingSeed searchedSeed = new GrowingSeedImpl();

        Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_ALLOTMENT_ID
                + "='" + allotment.getName() + "'", null, null, null, null);

        // TODO change this and remove code above when version 1.0.2 will be removed from users
        if (cursor.getCount() == 0) {
            cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_ALLOTMENT_ID
                    + "='" + allotment.getId() + "'", null, null, null, null);

        }

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
    public GrowingSeed getGrowingSeedById(int growingSeedId) {
        GrowingSeed searchedSeed = null;

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
    public void deleteGrowingSeed(GrowingSeed seed) {

        bdd.delete(DatabaseHelper.GROWINGSEEDS_TABLE_NAME,
                DatabaseHelper.GROWINGSEED_ID + "='" + seed.getId() + "'", null);
    }

    public GrowingSeed updateGrowingSeed(GrowingSeed growingSeed, BaseAllotmentInterface allotment) {

        // Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = seedToValues(growingSeed, allotment);

        bdd.update(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, values,
                DatabaseHelper.GROWINGSEED_ID + "='" + growingSeed.getId() + "'", null);

        return growingSeed;
    }

    public GrowingSeed getGrowingSeedsByUUID(String uuid) {
        GrowingSeed searchedSeed = null;

        Cursor cursor = bdd.query(DatabaseHelper.GROWINGSEEDS_TABLE_NAME, null, DatabaseHelper.GROWINGSEED_UUID + "='"
                + uuid + "'", null, null, null, null);

        if (cursor.moveToFirst()) {

            searchedSeed = cursorToSeed(cursor);

        }
        cursor.close();
        return searchedSeed;
    }

}
