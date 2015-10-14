package org.gots.seed.provider.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.gots.exception.GotsException;
import org.gots.exception.GotsUserNotConnectedException;
import org.gots.exception.NotImplementedException;
import org.gots.garden.GardenInterface;
import org.gots.garden.provider.local.GardenSQLite;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.seed.BaseSeed;
import org.gots.seed.BaseSeedImpl;
import org.gots.seed.BotanicFamily;
import org.gots.seed.BotanicSpecie;
import org.gots.seed.LikeStatus;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.utils.GotsDBHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalSeedProvider extends GotsDBHelper implements GotsSeedProvider {
    private static final String TAG = "LocalSeedProvider";

    // VendorSeedDBHelper myBank;

    protected List<BaseSeed> newSeeds = new ArrayList<BaseSeed>();

    public LocalSeedProvider(Context context) {
        super(context, DATABASE_GARDEN_TYPE);
        // myBank = new VendorSeedDBHelper(context);
    }

    public String[] getArraySpecies(boolean force) {
        Cursor managedCursor = bdd.query(GardenSQLite.SPECIE_TABLE_NAME, null, null, null, null, null, null);

        String[] arraySpecie = new String[managedCursor.getCount()];
        int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(GardenSQLite.SPECIE_NAME);
            do {
                name = managedCursor.getString(nameColumn);
                arraySpecie[j++] = name;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        return arraySpecie;
    }

    public synchronized String[] getArrayVariety() {
        Cursor cursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_VARIETY + "<>" + "''",
                null, GardenSQLite.SEED_VARIETY, null, null);

        String[] arrayFamily = new String[cursor.getCount()];
        int j = 0;
        if (cursor.moveToFirst()) {
            String name;
            int nameColumn = cursor.getColumnIndex(GardenSQLite.SEED_VARIETY);
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
        // Cursor managedCursor = bdd.query(GardenSQLite.FAMILY_ID, null,
        // GardenSQLite.SEED_ID + "=" + seed.getId(), null, null,
        // null, null);
        String MY_QUERY = "SELECT " + GardenSQLite.FAMILY_NAME + " FROM " + GardenSQLite.FAMILY_TABLE_NAME
                + " a INNER JOIN " + GardenSQLite.SPECIE_TABLE_NAME + " b ON a." + GardenSQLite.FAMILY_ID + "=b."
                + GardenSQLite.SPECIE_FAMILY_ID + " WHERE b." + GardenSQLite.SPECIE_NAME + "='" + specie + "'";
        Cursor managedCursor = bdd.rawQuery(MY_QUERY, null);
        String family = "";
        if (managedCursor.moveToFirst()) {
            int nameColumn = managedCursor.getColumnIndex(GardenSQLite.FAMILY_NAME);
            family = managedCursor.getString(nameColumn);
        }
        managedCursor.close();
        // close();
        return family;
    }

    public synchronized String[] getArrayVarietyBySpecie(String specie) {
        Cursor managedCursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_SPECIE + "='"
                + specie + "'", null, null, null, null);

        String[] arrayVariety = new String[managedCursor.getCount()];
        int j = 0;
        if (managedCursor.moveToFirst()) {
            String name;
            int nameColumn = managedCursor.getColumnIndex(GardenSQLite.SEED_VARIETY);
            do {
                name = managedCursor.getString(nameColumn);
                arrayVariety[j++] = name;
            } while (managedCursor.moveToNext());
        }
        managedCursor.close();
        return arrayVariety;
    }

    @Override
    public List<BotanicFamily> getAllFamilies() {
        // TODO
        return new ArrayList<>();
    }

    @Override
    public void getFamilyById(int id) {
        // TODO
    }

    @Override
    public BaseSeed getSeedById(int seedId) {
        BaseSeed searchedSeed = null;
        Cursor cursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_ID + "=" + seedId + "",
                null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                searchedSeed = cursorToSeed(cursor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchedSeed;
    }

    @Override
    public ArrayList<BaseSeed> getSeedByBarCode(String barecode) {
        ArrayList<BaseSeed> vendorSeeds = new ArrayList<BaseSeed>();

        Cursor query = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_BARECODE
                + "=\"" + barecode + "\"", null, null, null, null);

        if (query.moveToFirst())
            do {
                vendorSeeds.add(cursorToSeed(query));
            } while (query.moveToNext());

        query.close();
        return vendorSeeds;
    }

    @Override
    public List<BaseSeed> getVendorSeeds(boolean force, int page, int pageSize) {
        ArrayList<BaseSeed> vendorSeeds = new ArrayList<BaseSeed>();
        try {
            BaseSeed searchedSeed = new BaseSeedImpl();
            Cursor query = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, null, null, null, null, null);

            if (query.moveToFirst()) {
                do {
                    searchedSeed = cursorToSeed(query);
                    vendorSeeds.add(searchedSeed);
                } while (query.moveToNext());
            }
            query.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return vendorSeeds;
    }

    @Override
    public BaseSeed createSeed(BaseSeed seed, File imageFile) {
        long rowid;
        ContentValues values = getContentValuesFromSeed(seed);

        rowid = bdd.insert(GardenSQLite.SEEDS_TABLE_NAME, null, values);

        seed.setSeedId(Long.valueOf(rowid).intValue());

        return seed;
    }

    @Override
    public BaseSeed updateSeed(BaseSeed seed) {

        // Création d'un ContentValues (fonctionne comme une HashMap)
        ContentValues values = getContentValuesFromSeed(seed);
        // Cursor cursor;
        bdd.update(GardenSQLite.SEEDS_TABLE_NAME, values, GardenSQLite.SEED_ID + "='" + seed.getSeedId() + "'",
                null);
        // cursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_ID + "='" + seed.getSeedId()
        // + "'", null, null, null, null);
        //
        // if (cursor.moveToFirst()) {
        // int id = cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_ID));
        // seed.setSeedId(id);
        // }
        // cursor.close();

        return seed;
    }

    @Override
    public BaseSeed addToStock(BaseSeed vendorSeed, GardenInterface garden) {
        vendorSeed.setNbSachet(vendorSeed.getNbSachet() + 1);
        vendorSeed = updateSeed(vendorSeed);
        return vendorSeed;
    }

    @Override
    public BaseSeed removeToStock(BaseSeed vendorSeed, GardenInterface garden) {
        vendorSeed.setNbSachet(vendorSeed.getNbSachet() - 1);
        vendorSeed = updateSeed(vendorSeed);
        return vendorSeed;
    }

    @Override
    public List<BaseSeed> getMyStock(GardenInterface garden, boolean force) {
        ArrayList<BaseSeed> mySeeds = new ArrayList<BaseSeed>();
        BaseSeed searchedSeed = new BaseSeedImpl();
        Cursor managedCursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_NBSACHET + ">0",
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
    public void deleteSeed(BaseSeed vendorSeed) {
        bdd.delete(GardenSQLite.SEEDS_TABLE_NAME, GardenSQLite.SEED_ID + "='" + vendorSeed.getSeedId() + "'", null);

    }

    @Override
    public List<BaseSeed> getNewSeeds() {
        return newSeeds;
    }

    private ContentValues
    getContentValuesFromSeed(BaseSeed seed) {
        ContentValues values = new ContentValues();
        if (seed == null)
            return null;
        values.put(GardenSQLite.SEED_NAME, seed.getName());
        values.put(GardenSQLite.SEED_DESCRIPTION_GROWTH, seed.getDescriptionEnvironment());
        values.put(GardenSQLite.SEED_DESCRIPTION_CULTIVATION, seed.getDescriptionCultivation());
        values.put(GardenSQLite.SEED_DESCRIPTION_DISEASES, seed.getDescriptionDiseases());
        values.put(GardenSQLite.SEED_DESCRIPTION_HARVEST, seed.getDescriptionHarvest());
        values.put(GardenSQLite.SEED_UUID, seed.getUUID());
        values.put(GardenSQLite.SEED_DURATIONMIN, seed.getDurationMin());
        values.put(GardenSQLite.SEED_DURATIONMAX, seed.getDurationMax());
        values.put(GardenSQLite.SEED_DATESOWINGMIN, seed.getDateSowingMin());
        values.put(GardenSQLite.SEED_DATESOWINGMAX, seed.getDateSowingMax());
        values.put(GardenSQLite.SEED_BARECODE, seed.getBareCode());
        values.put(GardenSQLite.SEED_FAMILY, seed.getFamily());
        values.put(GardenSQLite.SEED_GENUS, seed.getGenus());
        values.put(GardenSQLite.SEED_ORDER, seed.getOrder());
        values.put(GardenSQLite.SEED_SPECIE, seed.getSpecie());
        values.put(GardenSQLite.SEED_VARIETY, seed.getVariety());
        values.put(GardenSQLite.SEED_URLDESCRIPTION, seed.getUrlDescription());
        values.put(GardenSQLite.SEED_NBSACHET, seed.getNbSachet());
        values.put(GardenSQLite.SEED_LANGUAGE, seed.getLanguage());
        values.put(GardenSQLite.SEED_STATE, seed.getState());

        if (seed.getLikeStatus() != null) {
            values.put(GardenSQLite.SEED_LIKE_COUNT, seed.getLikeStatus().getLikesCount());
            values.put(GardenSQLite.SEED_LIKE_STATUS, seed.getLikeStatus().getUserLikeStatus());
        }
        if (seed.getActionToDo() != null && seed.getActionToDo().size() > 0 && seed.getActionToDo().get(0) != null)
            values.put(GardenSQLite.SEED_ACTION1, seed.getActionToDo().get(0).getName());

        return values;
    }

    private BaseSeed cursorToSeed(Cursor cursor) {
        BaseSeed bsi = new BaseSeedImpl();
        bsi.setSeedId(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_ID)));
        bsi.setName(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_NAME)));
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_UUID)));
        bsi.setBareCode(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_BARECODE)));
        bsi.setDescriptionEnvironment(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_DESCRIPTION_GROWTH)));
        bsi.setDescriptionCultivation(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_DESCRIPTION_CULTIVATION)));
        bsi.setDescriptionDiseases(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_DESCRIPTION_DISEASES)));
        bsi.setDescriptionHarvest(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_DESCRIPTION_HARVEST)));
        bsi.setFamily(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_FAMILY)));
        bsi.setOrder(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_ORDER)));
        bsi.setGenus(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_GENUS)));
        bsi.setSpecie(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_SPECIE)));
        bsi.setVariety(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_VARIETY)));
        bsi.setDurationMin(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_DURATIONMIN)));
        bsi.setDurationMax(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_DURATIONMAX)));
        bsi.setDateSowingMin(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_DATESOWINGMIN)));
        bsi.setDateSowingMax(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_DATESOWINGMAX)));
        bsi.setUrlDescription(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_URLDESCRIPTION)));
        bsi.setNbSachet(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_NBSACHET)));
        bsi.setLanguage(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_LANGUAGE)));
        bsi.setState(cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_STATE)));

        LikeStatus like = new LikeStatus();
        like.setLikesCount(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_LIKE_COUNT)));
        like.setUserLikeStatus(cursor.getInt(cursor.getColumnIndex(GardenSQLite.SEED_LIKE_STATUS)));
        bsi.setLikeStatus(like);

//        BaseAction baseAction = ActionFactory.buildAction(mContext,
//                cursor.getString(cursor.getColumnIndex(GardenSQLite.SEED_ACTION1)));
//        if (baseAction != null)
//            bsi.getActionToDo().add(baseAction);

        return bsi;
    }

    @Override
    public BaseSeed getSeedByUUID(String uuid) {
        Cursor cursor;
        BaseSeed searchedSeed = null;
        if (uuid != null) {

            cursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_UUID + "='" + uuid + "'",
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                searchedSeed = cursorToSeed(cursor);
            }
            cursor.close();
        }
        return searchedSeed;
    }

    @Override
    public List<BaseSeed> getRecognitionSeeds(boolean force) {
        Log.d(TAG, "getRecognitionSeeds not implemented here");
        return new ArrayList<>();
    }

    @Override
    public void createRecognitionSeed(File file, NuxeoUtils.OnBlobUpload callback) {
        Log.d(TAG, "createRecognitionSeed not implemented here");
    }


    @Override
    public List<BaseSeed> getVendorSeedsByName(String currentFilter, boolean force) {
        ArrayList<BaseSeed> vendorSeeds = new ArrayList<BaseSeed>();
        try {
            BaseSeed searchedSeed = new BaseSeedImpl();
            Cursor managedCursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_VARIETY
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

    public LikeStatus like(BaseSeed mSeed, boolean b) throws GotsException {
        throw new GotsUserNotConnectedException(mContext);
    }

    @Override
    public List<BaseSeed> getMyFavorites() {
        List<BaseSeed> favorites = new ArrayList<BaseSeed>();
        for (BaseSeed baseSeed : getVendorSeeds(false, 0, 25)) {
            if (baseSeed.getLikeStatus().getUserLikeStatus() > 0)
                favorites.add(baseSeed);
        }
        return favorites;
    }

    @Override
    public List<BaseSeed> getSeedBySowingMonth(int month) {
        ArrayList<BaseSeed> vendorSeeds = new ArrayList<BaseSeed>();
        try {
            BaseSeed searchedSeed = new BaseSeedImpl();
            Cursor managedCursor = bdd.query(GardenSQLite.SEEDS_TABLE_NAME, null, GardenSQLite.SEED_DATESOWINGMIN
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

    @Override
    public List<BotanicSpecie> getSpecies(boolean force) {
       Log.d(TAG," -- not implemented --");
        return new ArrayList<>();
    }
}
