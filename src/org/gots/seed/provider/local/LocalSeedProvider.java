package org.gots.seed.provider.local;

import java.util.ArrayList;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.garden.GardenInterface;
import org.gots.provider.AbstractProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class LocalSeedProvider extends GotsDBHelper implements GotsSeedProvider {

    private static final String TAG = "LocalSeedProvider";

    VendorSeedDBHelper myBank;

    protected List<BaseSeedInterface> newSeeds = new ArrayList<BaseSeedInterface>();

    public LocalSeedProvider(Context context) {
        super(context);
        myBank = new VendorSeedDBHelper(context);
    }

    @Override
    public void getAllFamilies() {
        // TODO
    }

    @Override
    public void getFamilyById(int id) {
        // TODO
    }

    @Override
    public BaseSeedInterface getSeedById(int seedId) {
        BaseSeedInterface searchedSeed = null;
        // open();

        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_ID + "='" + seedId
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

    @Override
    public List<BaseSeedInterface> getVendorSeeds(boolean force) {
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

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
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

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface seed) {

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

    @Override
    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        vendorSeed.setNbSachet(vendorSeed.getNbSachet() + 1);
        updateSeed(vendorSeed);
    }

    @Override
    public void removeToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        vendorSeed.setNbSachet(vendorSeed.getNbSachet() - 1);
        updateSeed(vendorSeed);

    }

    @Override
    public List<BaseSeedInterface> getMyStock(GardenInterface garden) {
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

    @Override
    public void remove(BaseSeedInterface vendorSeed) {
        myBank.remove(vendorSeed);

    }

    @Override
    public List<BaseSeedInterface> getNewSeeds() {
        return newSeeds;
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
}
