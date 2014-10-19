package org.gots.seed.provider.local;

import java.util.ArrayList;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.exception.GotsException;
import org.gots.exception.GotsUserNotConnectedException;
import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.LikeStatus;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;


public class LocalSeedProvider extends GotsDBHelper implements GotsSeedProvider {
    private static final String TAG = "LocalSeedProvider";

    // VendorSeedDBHelper myBank;

    protected List<BaseSeedInterface> newSeeds = new ArrayList<BaseSeedInterface>();

    public LocalSeedProvider(Context context) {
        super(context);
        // myBank = new VendorSeedDBHelper(context);
    }

    public String[] getArraySpecies(boolean force) {
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
        return arraySpecie;
    }

    public synchronized String[] getArrayVariety() {
        Cursor cursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_VARIETY + "<>" + "''",
                null, DatabaseHelper.SEED_VARIETY, null, null);

        String[] arrayFamily = new String[cursor.getCount()];
        int j = 0;
        if (cursor.moveToFirst()) {
            String name;
            int nameColumn = cursor.getColumnIndex(DatabaseHelper.SEED_VARIETY);
            do {
                name = cursor.getString(nameColumn);
                if (name != null)
                    arrayFamily[j++] = name;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return arrayFamily;
    }

    @Override
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
        return arrayVariety;
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
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_ID + "='" + seedId
                + "'", null, null, null, null);
        if (managedCursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(managedCursor);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        return searchedSeed;
    }

    @Override
    public BaseSeedInterface getSeedByBarCode(String barecode) {
        BaseSeedInterface searchedSeed = new GrowingSeed();
        if (bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_BARECODE + "=\"" + barecode + "\"",
                null, null, null, null).moveToFirst()) {
            searchedSeed = cursorToSeed(bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_BARECODE
                    + "=\"" + barecode + "\"", null, null, null, null));
        }
        bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_BARECODE + "=\"" + barecode + "\"", null,
                null, null, null).close();
        return searchedSeed;
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds(boolean force, int page, int pageSize) {
        ArrayList<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();
        try {
            BaseSeedInterface searchedSeed = new GrowingSeed();
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
        return vendorSeeds;
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
        long rowid;
        ContentValues values = getContentValuesFromSeed(seed);

        rowid = bdd.insert(DatabaseHelper.SEEDS_TABLE_NAME, null, values);

        seed.setId(Long.valueOf(rowid).intValue());

        return seed;
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface seed) {

        // Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = getContentValuesFromSeed(seed);
        // Cursor cursor;
        bdd.update(DatabaseHelper.SEEDS_TABLE_NAME, values, DatabaseHelper.SEED_ID + "='" + seed.getSeedId() + "'",
                null);
        // cursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_ID + "='" + seed.getSeedId()
        // + "'", null, null, null, null);
        //
        // if (cursor.moveToFirst()) {
        // int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_ID));
        // seed.setId(id);
        // }
        // cursor.close();

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
        Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_NBSACHET + ">0",
                null, null, null, null);

        if (managedCursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(managedCursor);
                mySeeds.add(searchedSeed);
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        return mySeeds;
    }

    @Override
    public void deleteSeed(BaseSeedInterface vendorSeed) {
        bdd.delete(DatabaseHelper.SEEDS_TABLE_NAME, DatabaseHelper.SEED_ID + "='" + vendorSeed.getSeedId() + "'", null);

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
        values.put(DatabaseHelper.SEED_LANGUAGE, seed.getLanguage());

        if (seed.getLikeStatus() != null) {
            values.put(DatabaseHelper.SEED_LIKE_COUNT, seed.getLikeStatus().getLikesCount());
            values.put(DatabaseHelper.SEED_LIKE_STATUS, seed.getLikeStatus().getUserLikeStatus());
        }
        if (seed.getActionToDo() != null && seed.getActionToDo().size() > 0 && seed.getActionToDo().get(0) != null)
            values.put(DatabaseHelper.SEED_ACTION1, seed.getActionToDo().get(0).getName());

        return values;
    }

    private BaseSeedInterface cursorToSeed(Cursor cursor) {
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
        bsi.setLanguage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_LANGUAGE)));

        LikeStatus like = new LikeStatus();
        like.setLikesCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_LIKE_COUNT)));
        like.setUserLikeStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_LIKE_STATUS)));
        bsi.setLikeStatus(like);

        BaseActionInterface baseAction = ActionFactory.buildAction(mContext,
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_ACTION1)));
        if (baseAction != null)
            bsi.getActionToDo().add(baseAction);

        return bsi;
    }

    public BaseSeedInterface getSeedByUUID(String uuid) {
        Cursor cursor;
        BaseSeedInterface searchedSeed = null;
        if (uuid != null) {

            cursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_UUID + "='" + uuid + "'",
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                searchedSeed = cursorToSeed(cursor);
            }
            cursor.close();
        }
        return searchedSeed;
    }

    @Override
    public void force_refresh(boolean refresh) {
    }

    @Override
    public List<BaseSeedInterface> getVendorSeedsByName(String currentFilter) {
        ArrayList<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();
        try {
            BaseSeedInterface searchedSeed = new GrowingSeed();
            Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_VARIETY
                    + " LIKE \"%" + currentFilter + "%\"", null, null, null, null);

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
        return vendorSeeds;
    }

    public LikeStatus like(BaseSeedInterface mSeed, boolean b) throws GotsException {
        throw new GotsUserNotConnectedException(mContext);
    }

    @Override
    public List<BaseSeedInterface> getMyFavorites() {
        List<BaseSeedInterface> favorites = new ArrayList<BaseSeedInterface>();
        for (BaseSeedInterface baseSeedInterface : getVendorSeeds(false, 0, 25)) {
            if (baseSeedInterface.getLikeStatus().getUserLikeStatus() > 0)
                favorites.add(baseSeedInterface);
        }
        return favorites;
    }

    @Override
    public List<BaseSeedInterface> getSeedBySowingMonth(int month) {
        ArrayList<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();
        try {
            BaseSeedInterface searchedSeed = new GrowingSeed();
            Cursor managedCursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_DATESOWINGMIN
                    + "=" + month, null, null, null, null);

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
        return vendorSeeds;
    }

}
