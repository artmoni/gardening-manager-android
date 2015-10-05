package org.gots.seed.provider.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.gots.DatabaseHelper;
import org.gots.exception.GotsException;
import org.gots.exception.GotsUserNotConnectedException;
import org.gots.exception.NotImplementedException;
import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.seed.BaseSeed;
import org.gots.seed.BaseSeedImpl;
import org.gots.seed.LikeStatus;
import org.gots.seed.SpeciesDocument;
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
    public BaseSeed getSeedById(int seedId) {
        BaseSeed searchedSeed = null;
        Cursor cursor = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_ID + "=" + seedId + "",
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

        Cursor query = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, DatabaseHelper.SEED_BARECODE
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
            Cursor query = bdd.query(DatabaseHelper.SEEDS_TABLE_NAME, null, null, null, null, null, null);

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

        rowid = bdd.insert(DatabaseHelper.SEEDS_TABLE_NAME, null, values);

        seed.setSeedId(Long.valueOf(rowid).intValue());

        return seed;
    }

    @Override
    public BaseSeed updateSeed(BaseSeed seed) {

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
    public void deleteSeed(BaseSeed vendorSeed) {
        bdd.delete(DatabaseHelper.SEEDS_TABLE_NAME, DatabaseHelper.SEED_ID + "='" + vendorSeed.getSeedId() + "'", null);

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
        values.put(DatabaseHelper.SEED_NAME, seed.getName());
        values.put(DatabaseHelper.SEED_DESCRIPTION_GROWTH, seed.getDescriptionEnvironment());
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
        values.put(DatabaseHelper.SEED_STATE, seed.getState());

        if (seed.getLikeStatus() != null) {
            values.put(DatabaseHelper.SEED_LIKE_COUNT, seed.getLikeStatus().getLikesCount());
            values.put(DatabaseHelper.SEED_LIKE_STATUS, seed.getLikeStatus().getUserLikeStatus());
        }
        if (seed.getActionToDo() != null && seed.getActionToDo().size() > 0 && seed.getActionToDo().get(0) != null)
            values.put(DatabaseHelper.SEED_ACTION1, seed.getActionToDo().get(0).getName());

        return values;
    }

    private BaseSeed cursorToSeed(Cursor cursor) {
        BaseSeed bsi = new BaseSeedImpl();
        bsi.setSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_ID)));
        bsi.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_NAME)));
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_UUID)));
        bsi.setBareCode(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_BARECODE)));
        bsi.setDescriptionEnvironment(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_DESCRIPTION_GROWTH)));
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
        bsi.setState(cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_STATE)));

        LikeStatus like = new LikeStatus();
        like.setLikesCount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_LIKE_COUNT)));
        like.setUserLikeStatus(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.SEED_LIKE_STATUS)));
        bsi.setLikeStatus(like);

//        BaseAction baseAction = ActionFactory.buildAction(mContext,
//                cursor.getString(cursor.getColumnIndex(DatabaseHelper.SEED_ACTION1)));
//        if (baseAction != null)
//            bsi.getActionToDo().add(baseAction);

        return bsi;
    }

    @Override
    public BaseSeed getSeedByUUID(String uuid) {
        Cursor cursor;
        BaseSeed searchedSeed = null;
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
    public List<BaseSeed> getRecognitionSeeds(boolean force) {
        Log.d(TAG, "createRecognitionSeed not implemented here");
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

    @Override
    public SpeciesDocument getSpecies(boolean force) throws NotImplementedException {
        throw new NotImplementedException();
    }
}
