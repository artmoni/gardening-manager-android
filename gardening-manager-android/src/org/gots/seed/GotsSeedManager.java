package org.gots.seed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.exception.GotsException;
import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GotsSeedManager extends BroadcastReceiver implements GotsSeedProvider {

    private static final String TAG = "GotsSeedManager";

    private static GotsSeedManager instance;

    private Context mContext;

    private GotsSeedProvider mSeedProvider;

    private boolean initDone = false;

    private List<BaseSeed> newSeeds = new ArrayList<BaseSeed>();

    Map<Integer, BaseSeed> allSeeds;

    private static Exception firstCall;

    private GotsPreferences gotsPrefs;

    private String[] listSpecies;

    private NuxeoManager nuxeoManager;
    private List<BotanicSpecie> botanicSpecies = new ArrayList<>();

    private GotsSeedManager() {
        // mLocalProvider = new LocalSeedProvider(mContext);
        // allSeeds = new ArrayList<BaseSeed>();
        allSeeds = new HashMap<>();
    }

    private GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    protected void setSeedProvider() {
        if (gotsPrefs.isConnectedToServer() && !nuxeoManager.getNuxeoClient().isOffline()) {
            mSeedProvider = new NuxeoSeedProvider(mContext);
        } else
            mSeedProvider = new LocalSeedProvider(mContext);
    }

    public static synchronized GotsSeedManager getInstance() {
        if (instance == null) {
            instance = new GotsSeedManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    /**
     * If it was already called once, the method returns without any change.
     */
    public synchronized GotsSeedManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        gotsPrefs = (GotsPreferences) getGotsContext().getServerConfig();
        nuxeoManager = NuxeoManager.getInstance().initIfNew(context);

        setSeedProvider();
        initDone = true;
        return this;
    }

    public void reset() {
        initDone = false;
        mContext = null;
        instance = null;
    }

    @Override
    public List<BaseSeed> getVendorSeeds(boolean force, int page, int pageSize) {
        if (!force && allSeeds.size() > 10 || mContext == null)
            return new ArrayList<BaseSeed>(allSeeds.values());
        GotsSeedProvider provider;
        if (force && !nuxeoManager.getNuxeoClient().isOffline()) {
            provider = new NuxeoSeedProvider(mContext);
            newSeeds = provider.getNewSeeds();
        } else {
            provider = new LocalSeedProvider(mContext);
        }
        for (BaseSeed seedInterface : provider.getVendorSeeds(force, page, pageSize)) {
            allSeeds.put(seedInterface.getSeedId(), seedInterface);
        }

        return new ArrayList<BaseSeed>(allSeeds.values());
    }

    @Override
    public List<BotanicFamily> getAllFamilies() {
        // TODO Auto-generated method stub
        return new ArrayList<>();
    }

    @Override
    public void getFamilyById(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public BaseSeed getSeedById(int seedId) {
        if (allSeeds.get(seedId) != null) {
            return allSeeds.get(seedId);
        }
        return mSeedProvider.getSeedById(seedId);
    }

    @Override
    public BaseSeed createSeed(BaseSeed seed, File file) {
        final BaseSeed createSeed = mSeedProvider.createSeed(seed, file);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(createSeed.getSeedId(), createSeed);
        return createSeed;
    }

    @Override
    public BaseSeed updateSeed(BaseSeed newSeed) {
        final BaseSeed updateSeed = mSeedProvider.updateSeed(newSeed);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(updateSeed.getSeedId(), updateSeed);
        return updateSeed;
    }

    @Override
    public void deleteSeed(BaseSeed vendorSeed) {
        mSeedProvider.deleteSeed(vendorSeed);
        allSeeds.remove(vendorSeed.getSeedId());
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
    }

    @Override
    public BaseSeed addToStock(BaseSeed vendorSeed, final GardenInterface garden) {
        vendorSeed = mSeedProvider.addToStock(vendorSeed, garden);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(vendorSeed.getSeedId(), vendorSeed);
        return vendorSeed;
    }

    @Override
    public BaseSeed removeToStock(BaseSeed vendorSeed, final GardenInterface garden) {
        vendorSeed = mSeedProvider.removeToStock(vendorSeed, garden);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(vendorSeed.getSeedId(), vendorSeed);
        return vendorSeed;
    }

    @Override
    public List<BaseSeed> getMyStock(GardenInterface garden, boolean force) {
        List<BaseSeed> myStock = new ArrayList<>();
        if (!force) {
            for (BaseSeed seedInterface : allSeeds.values()) {
                if (seedInterface.getNbSachet() > 0) {
                    myStock.add(seedInterface);
                }
            }
        } else {
            myStock = mSeedProvider.getMyStock(garden, force);
            for (BaseSeed baseSeed : myStock) {
                allSeeds.put(baseSeed.getSeedId(), baseSeed);
            }
        }
        return myStock;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())
                || NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())) {
            setSeedProvider();
        }
        if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
            setSeedProvider();
        }
    }

    @Override
    public List<BaseSeed> getNewSeeds() {
        return newSeeds;
    }

    @Override
    public synchronized ArrayList<BaseSeed> getSeedByBarCode(String barecode) {
        return mSeedProvider.getSeedByBarCode(barecode);
    }

    @Override
    public List<BaseSeed> getVendorSeedsByName(String currentFilter, boolean force) {
        List<BaseSeed> vendorSeedsByName = new ArrayList<>();
        if (force && !nuxeoManager.getNuxeoClient().isOffline()) {
            NuxeoSeedProvider nuxeoSeedProvider = new NuxeoSeedProvider(mContext);
            vendorSeedsByName.addAll(nuxeoSeedProvider.getVendorSeedsByName(currentFilter, force));
        } else {
            vendorSeedsByName.addAll(mSeedProvider.getVendorSeedsByName(currentFilter, force));
        }

        return vendorSeedsByName;
    }

    public LikeStatus like(BaseSeed mSeed, boolean cancelLike) throws GotsException {
        final LikeStatus like = mSeedProvider.like(mSeed, cancelLike);
        allSeeds.put(mSeed.getSeedId(), mSeed);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        return like;
    }

    @Override
    public List<BaseSeed> getMyFavorites() {
        List<BaseSeed> myFavorites = new ArrayList<>();

        for (BaseSeed baseSeed : allSeeds.values()) {
            if (baseSeed.getLikeStatus() != null && baseSeed.getLikeStatus().getUserLikeStatus() > 0)
                myFavorites.add(baseSeed);
        }
        return myFavorites;
    }

    @Override
    public List<BaseSeed> getSeedBySowingMonth(int month) {
        List<BaseSeed> seedBySowingMonth = new ArrayList<>();
        for (BaseSeed baseSeed : allSeeds.values()) {
            if (baseSeed.getDateSowingMin() <= month && baseSeed.getDateSowingMax() >= month)
                seedBySowingMonth.add(baseSeed);
        }
        if (seedBySowingMonth.size() == 0) {
            for (BaseSeed seed : mSeedProvider.getSeedBySowingMonth(month)) {
                allSeeds.put(seed.getSeedId(), seed);
                seedBySowingMonth.add(seed);
            }
        }

        return seedBySowingMonth;
    }

//    public synchronized String[] getArraySpecies(boolean force) {
//        if (botanicSpecies == null || force) {
//            NuxeoSeedProvider seedProvider = new NuxeoSeedProvider(mContext);
//            botanicSpecies = seedProvider.getSpecies(force);
//
//        } else botanicSpecies = mSeedProvider.getSpecies(force);
//
//        return botanicSpecies;
//    }

    public synchronized String getFamilyBySpecie(String specie) {
        return mSeedProvider.getFamilyBySpecie(specie);
    }

    @Override
    public List<BotanicSpecie> getSpecies(boolean force) {
        if (botanicSpecies.size() == 0 || force) {
            NuxeoSeedProvider seedProvider = new NuxeoSeedProvider(mContext);
            botanicSpecies.addAll(seedProvider.getSpecies(force));

        } else botanicSpecies.addAll(mSeedProvider.getSpecies(force));

        return botanicSpecies;
    }

    @Override
    public BaseSeed getSeedByUUID(String uuid) {
        BaseSeed searchedSeed = null;
        for (BaseSeed seed : allSeeds.values()) {
            if (uuid != null && uuid.equals(seed.getUUID())) {
                searchedSeed = seed;
                return seed;
            }

        }

        searchedSeed = mSeedProvider.getSeedByUUID(uuid);
        if (searchedSeed != null)
            allSeeds.put(searchedSeed.getSeedId(), searchedSeed);
        return searchedSeed;
    }

    @Override
    public List<BaseSeed> getRecognitionSeeds(boolean force) {
        return mSeedProvider.getRecognitionSeeds(force);
    }

    @Override
    public void createRecognitionSeed(File file, NuxeoUtils.OnBlobUpload callback) {
        mSeedProvider.createRecognitionSeed(file, callback);
    }
}
