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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class VendorSeedDBHelper extends GotsDBHelper {

    private static VendorSeedDBHelper helper = null;

    private List<BaseSeedInterface> allSeeds = new ArrayList<BaseSeedInterface>();

    private String TAG = "VendorSeedDBHelper";

    public VendorSeedDBHelper(Context mContext) {
        super(mContext);
    }

    public synchronized BaseSeedInterface insertSeed(BaseSeedInterface seed) {
        // Création d'un ContentValues (fonctionne comme une HashMap)
        long rowid;
        // open();
        ContentValues values = getContentValuesFromSeed(seed);

        try {
            rowid = bdd.insert(DatabaseHelper.SEEDS_TABLE_NAME, null, values);
            seed.setId(Long.valueOf(rowid).intValue());
        } finally {
            // close();
        }

        return seed;
    }

    public synchronized BaseSeedInterface updateSeed(BaseSeedInterface seed) {
        // Création d'un ContentValues (fonctionne comme une HashMap)
        // open();
        ContentValues values = getContentValuesFromSeed(seed);
        Cursor cursor;

        if (seed.getUUID() != null) {
            int nbRows = bdd.update(DatabaseHelper.SEEDS_TABLE_NAME, values,
                    DatabaseHelper.SEED_UUID + "='" + seed.getUUID() + "'", null);

            cursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_UUID + "='" + seed.getUUID()
                    + "'", null, null, null, null);

        } else {
            int rowid = bdd.update(DatabaseHelper.SEEDS_TABLE_NAME, values,
                    DatabaseHelper.SEED_ID + "='" + seed.getSeedId() + "'", null);
            cursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_ID + "='" + seed.getSeedId()
                    + "'", null, null, null, null);

        }
        if (cursor.moveToFirst()) {
            int rowid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_ID));
            seed.setId(rowid);
        }
        cursor.close();

        return seed;
    }

    private ContentValues getContentValuesFromSeed(BaseSeedInterface seed) {
        ContentValues values = new ContentValues();
        if (seed == null)
            return null;
        values.put(DatabaseHelper.SEED_NAME, seed.getName());
        values.put(DatabaseHelper.SEED_DESCRIPTION_GROWTH, seed.getDescriptionGrowth());
        values.put(DatabaseHelper.SEED_DESCRIPTION_CULTIVATION, seed.getDescriptionCultivation());
        values.put(DatabaseHelper.SEED_DESCRIPTION_DISEASES, seed.getDescriptionDiseases());
        values.put(DatabaseHelper.SEED_DESCRIPTION_HARVEST, seed.getDescriptionHarvest());

        values.put(DatabaseHelper.SEED_UUID, seed.getUUID());
        values.put(DatabaseHelper.SEED_DURATIONMIN, seed.getDurationMin());
        values.put(DatabaseHelper.SEED_DURATIONMAX, seed.getDurationMax());
        values.put(DatabaseHelper.SEED_DATESOWINGMIN, seed.getDateSowingMin());
        values.put(DatabaseHelper.SEED_DATESOWINGMAX, seed.getDateSowingMax());
        values.put(DatabaseHelper.SEED_BARECODE, seed.getBareCode());
        values.put(DatabaseHelper.SEED_FAMILY, seed.getFamily());
        values.put(DatabaseHelper.SEED_GENUS, seed.getGenus());
        values.put(DatabaseHelper.SEED_ORDER, seed.getOrder());
        values.put(DatabaseHelper.SEED_SPECIE, seed.getSpecie());
        values.put(DatabaseHelper.SEED_VARIETY, seed.getVariety());
        values.put(DatabaseHelper.SEED_URLDESCRIPTION, seed.getUrlDescription());
        values.put(DatabaseHelper.SEED_NBSACHET, seed.getNbSachet());

        if (seed.getActionToDo() != null && seed.getActionToDo().size() > 0 && seed.getActionToDo().get(0) != null)
            values.put(DatabaseHelper.SEED_ACTION1, seed.getActionToDo().get(0).getName());

        return values;
    }

    public synchronized String[] getArraySeeds() {
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, null, null, null, null, null);

        String[] arraySeeds = new String[managedCursor.getCount()];
        int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(DatabaseHelper.SEED_NAME);
            do {
                name = managedCursor.getString(nameColumn);
                arraySeeds[j++] = name;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return arraySeeds;
    }

    public synchronized ArrayList<String> getArrayFamily() {
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, null, null, DatabaseHelper.SEED_FAMILY,
                null, null);

        // String[] arrayFamily = new String[managedCursor.getCount()];
        ArrayList<String> arrayFamily = new ArrayList<String>();
        // int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(DatabaseHelper.SEED_FAMILY);
            do {
                name = managedCursor.getString(nameColumn);
                // arrayFamily[j++] = name;
                arrayFamily.add(name);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        Collections.sort(arrayFamily);
        return arrayFamily;
    }

    public synchronized String[] getArraySpecie() {
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SPECIE_TABLE_NAME, null, null, null, null, null, null);

        String[] arraySpecie = new String[managedCursor.getCount()];
        int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(DatabaseHelper.SPECIE_NAME);
            do {
                name = managedCursor.getString(nameColumn);
                arraySpecie[j++] = name;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return arraySpecie;
    }

    public synchronized String getFamilyBySpecie(String specie) {
        // open();
        // Cursor managedCursor = bdd.query(DatabaseHelper.FAMILY_ID, null,
        // DatabaseHelper.SEED_ID + "=" + seed.getId(), null, null,
        // null, null);
        String MY_QUERY = "SELECT " + DatabaseHelper.FAMILY_NAME + " FROM " + DatabaseHelper.FAMILY_TABLE_NAME
                + " a INNER JOIN " + DatabaseHelper.SPECIE_TABLE_NAME + " b ON a." + DatabaseHelper.FAMILY_ID + "=b."
                + DatabaseHelper.SPECIE_FAMILY_ID + " WHERE b." + DatabaseHelper.SPECIE_NAME + "='" + specie + "'";
        Cursor managedCursor = bdd.rawQuery(MY_QUERY, null);
        String family = "";
        if (managedCursor.moveToFirst()) {
            int nameColumn = managedCursor.getColumnIndex(DatabaseHelper.FAMILY_NAME);
            family = managedCursor.getString(nameColumn);
        }
        managedCursor.close();
        // close();
        return family;
    }

    public synchronized String[] getArrayVarietyBySpecie(String specie) {
        // open();

        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_SPECIE + "='"
                + specie + "'", null, null, null, null);

        String[] arrayVariety = new String[managedCursor.getCount()];
        int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(DatabaseHelper.SEED_VARIETY);
            do {
                name = managedCursor.getString(nameColumn);
                arrayVariety[j++] = name;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return arrayVariety;
    }

    public synchronized String[] getArrayVariety() {
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_VARIETY + "<>"
                + "''", null, DatabaseHelper.SEED_VARIETY, null, null);

        String[] arrayFamily = new String[managedCursor.getCount()];
        int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(DatabaseHelper.SEED_VARIETY);
            do {
                name = managedCursor.getString(nameColumn);
                if (name != null)
                    arrayFamily[j++] = name;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return arrayFamily;
    }

    public synchronized ArrayList<BaseSeedInterface> getMySeeds() {
        ArrayList<BaseSeedInterface> mySeeds = new ArrayList<BaseSeedInterface>();
        BaseSeedInterface searchedSeed = new GrowingSeed();
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_NBSACHET + ">0",
                null, null, null, null);

        if (managedCursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(managedCursor);
                mySeeds.add(searchedSeed);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return mySeeds;
    }

    public synchronized ArrayList<BaseSeedInterface> getVendorSeeds() {
        ArrayList<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();
        try {
            BaseSeedInterface searchedSeed = new GrowingSeed();
            // open();
            Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, null, null, null, null, null);

            if (managedCursor.moveToFirst()) {
                do {
                    searchedSeed = cursorToSeed(managedCursor);
                    vendorSeeds.add(searchedSeed);
                } while (managedCursor.moveToNext());
            }
            managedCursor.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // close();
        return vendorSeeds;
    }

    public synchronized BaseSeedInterface getSeedByName(String name) {
        BaseSeedInterface searchedSeed = new GrowingSeed();
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_NAME + " like \""
                + name + "\"", null, null, null, null);

        if (managedCursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(managedCursor);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return searchedSeed;
    }

    public synchronized BaseSeedInterface getSeedByBarCode(String barecode) {
        BaseSeedInterface searchedSeed = new GrowingSeed();
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_BARECODE + "=\""
                + barecode + "\"", null, null, null, null);

        if (managedCursor.moveToFirst()) {
            searchedSeed = cursorToSeed(managedCursor);
        }
        managedCursor.close();
        // close();
        return searchedSeed;
    }

    public synchronized BaseSeedInterface getSeedById(int id) {
        BaseSeedInterface searchedSeed = null;
        // open();

        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_ID + "='" + id
                + "'", null, null, null, null);
        // Log.d("getSeedById", "ID=>"+id+" / QUERY=>"+bdd.ge)
        if (managedCursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(managedCursor);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return searchedSeed;
    }

    public synchronized BaseSeedInterface getSeedByUUID(String reference) {
        BaseSeedInterface searchedSeed = null;
        // open();
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_UUID + "='"
                + reference + "'", null, null, null, null);
        // Log.d("getSeedById", "ID=>"+id+" / QUERY=>"+bdd.ge)
        if (managedCursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(managedCursor);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        // close();
        return searchedSeed;
    }

    public BaseSeedInterface cursorToSeed(Cursor cursor) {
        BaseSeedInterface bsi = new GrowingSeed();
        bsi.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_ID)));
        bsi.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_NAME)));
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_UUID)));
        bsi.setBareCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_BARECODE)));
        bsi.setDescriptionGrowth(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_DESCRIPTION_GROWTH)));
        bsi.setDescriptionCultivation(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_DESCRIPTION_CULTIVATION)));
        bsi.setDescriptionDiseases(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_DESCRIPTION_DISEASES)));
        bsi.setDescriptionHarvest(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_DESCRIPTION_HARVEST)));
        bsi.setFamily(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_FAMILY)));
        bsi.setOrder(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_ORDER)));
        bsi.setGenus(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_GENUS)));
        bsi.setSpecie(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_SPECIE)));
        bsi.setVariety(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_VARIETY)));
        bsi.setDurationMin(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_DURATIONMIN)));
        bsi.setDurationMax(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_DURATIONMAX)));
        bsi.setDateSowingMin(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_DATESOWINGMIN)));
        bsi.setDateSowingMax(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_DATESOWINGMAX)));
        bsi.setUrlDescription(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_URLDESCRIPTION)));
        bsi.setNbSachet(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_NBSACHET)));

        // bsi.setDateSowing(new
        // Date(cursor.getColumnIndex(SeedSQLite.DATESOWING)));

        // a.setName(cursor.getString(cursor
        // .getColumnIndex(SeedSQLite.ACTION1)));
        ActionFactory factory = new ActionFactory();
        BaseActionInterface baseAction = factory.buildAction(mContext,
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_ACTION1)));
        if (baseAction != null)
            bsi.getActionToDo().add(baseAction);

        // bsi.setDateLastWatering(cursor.getString(cursor.getColumnIndex(SeedSQLite.DATE_LAST_WATERING)));

        return bsi;
    }

    public synchronized String[] getArraySeedsByFamily(String string) {
        // String[] items = new String[allSeeds.size()];
        ArrayList<String> searchedSeeds = new ArrayList<String>();
        for (Iterator<BaseSeedInterface> iterator = allSeeds.iterator(); iterator.hasNext();) {
            BaseSeedInterface seed = iterator.next();
            if (string.toLowerCase().equals(seed.getFamily()))
                searchedSeeds.add(seed.getName());
        }
        return searchedSeeds.toArray(new String[searchedSeeds.size()]);
    }

    public synchronized String[] getArraySeedAction() {
        ArrayList<BaseActionInterface> actions = new ArrayList<BaseActionInterface>();
        for (Iterator<BaseSeedInterface> iterator = getMySeeds().iterator(); iterator.hasNext();) {
            BaseSeedInterface baseSeedInterface = iterator.next();
            actions.addAll(baseSeedInterface.getActionToDo());
        }
        String[] actionsName = new String[actions.size()];
        int i = 0;
        for (Iterator<BaseActionInterface> iterator = actions.iterator(); iterator.hasNext();) {
            BaseActionInterface seedActionInterface = iterator.next();
            actionsName[i++] = seedActionInterface.getName();
        }
        return actionsName;
    }

    public synchronized Object remove(BaseSeedInterface vendorSeed) {
        long rowid;
        // open();
        try {
            rowid = bdd.delete(DatabaseHelper.SEEDS_TABLE_NAME, DatabaseHelper.SEED_ID + "='" + vendorSeed.getSeedId()
                    + "'", null);
        } finally {
            // close();
        }

        return rowid;
    }

}
