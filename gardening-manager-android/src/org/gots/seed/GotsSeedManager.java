package org.gots.seed;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.exception.GotsException;
import org.gots.exception.NotImplementedException;
import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GotsSeedManager extends BroadcastReceiver implements GotsSeedProvider {

    private static final String TAG = "GotsSeedManager";

    private static GotsSeedManager instance;

    private Context mContext;

    private GotsSeedProvider mSeedProvider;

    private boolean initDone = false;

    private List<BaseSeedInterface> newSeeds = new ArrayList<BaseSeedInterface>();

    // private List<BaseSeedInterface> allSeeds = new ArrayList<BaseSeedInterface>();
    Map<Integer, BaseSeedInterface> allSeeds;

    private static Exception firstCall;

    // private List<BaseSeedInterface> myStock;

    private GotsPreferences gotsPrefs;

    private String[] listSpecies;

    private NuxeoManager nuxeoManager;

    private GotsSeedManager() {
        // mLocalProvider = new LocalSeedProvider(mContext);
        // allSeeds = new ArrayList<BaseSeedInterface>();
        allSeeds = new HashMap<>();
    }

    private GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    protected void setSeedProvider() {
        // ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        // NetworkInfo ni = cm.getActiveNetworkInfo();
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
    public List<BaseSeedInterface> getVendorSeeds(boolean force, int page, int pageSize) {
        if (!force && allSeeds.size() > 10 || mContext == null)
            return new ArrayList<BaseSeedInterface>(allSeeds.values());
        GotsSeedProvider provider;
        if (force && !nuxeoManager.getNuxeoClient().isOffline()) {
            provider = new NuxeoSeedProvider(mContext);
            newSeeds = provider.getNewSeeds();
        } else {
            provider = new LocalSeedProvider(mContext);
        }
        for (BaseSeedInterface seedInterface : provider.getVendorSeeds(force, page, pageSize)) {
            allSeeds.put(seedInterface.getSeedId(), seedInterface);
        }

        return new ArrayList<BaseSeedInterface>(allSeeds.values());
    }

    @Override
    public void getAllFamilies() {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFamilyById(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public BaseSeedInterface getSeedById(int seedId) {
        if (allSeeds.get(seedId) != null) {
            return allSeeds.get(seedId);
        }
        return mSeedProvider.getSeedById(seedId);
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed, File file) {
        final BaseSeedInterface createSeed = mSeedProvider.createSeed(seed, file);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(createSeed.getSeedId(), createSeed);
        return createSeed;
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        final BaseSeedInterface updateSeed = mSeedProvider.updateSeed(newSeed);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(updateSeed.getSeedId(), updateSeed);
        return updateSeed;
    }

    @Override
    public void deleteSeed(BaseSeedInterface vendorSeed) {
        mSeedProvider.deleteSeed(vendorSeed);
        allSeeds.remove(vendorSeed.getSeedId());
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
    }

    @Override
    public BaseSeedInterface addToStock(BaseSeedInterface vendorSeed, final GardenInterface garden) {
        vendorSeed = mSeedProvider.addToStock(vendorSeed, garden);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(vendorSeed.getSeedId(), vendorSeed);
        return vendorSeed;
    }

    @Override
    public BaseSeedInterface removeToStock(BaseSeedInterface vendorSeed, final GardenInterface garden) {
        vendorSeed = mSeedProvider.removeToStock(vendorSeed, garden);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        allSeeds.put(vendorSeed.getSeedId(), vendorSeed);
        return vendorSeed;
    }

    @Override
    public List<BaseSeedInterface> getMyStock(GardenInterface garden, boolean force) {
        List<BaseSeedInterface> myStock = new ArrayList<>();
        if (!force) {
            for (BaseSeedInterface seedInterface : allSeeds.values()) {
                if (seedInterface.getNbSachet() > 0) {
                    myStock.add(seedInterface);
                }
            }
        } else {
            myStock = mSeedProvider.getMyStock(garden, force);
            for (BaseSeedInterface baseSeedInterface : myStock) {
                allSeeds.put(baseSeedInterface.getSeedId(), baseSeedInterface);
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
    public List<BaseSeedInterface> getNewSeeds() {
        return newSeeds;
    }

    @Override
    public synchronized BaseSeedInterface getSeedByBarCode(String barecode) {
        return mSeedProvider.getSeedByBarCode(barecode);
    }

    @Override
    public List<BaseSeedInterface> getVendorSeedsByName(String currentFilter, boolean force) {
        return mSeedProvider.getVendorSeedsByName(currentFilter, force);
    }

    public LikeStatus like(BaseSeedInterface mSeed, boolean cancelLike) throws GotsException {
        final LikeStatus like = mSeedProvider.like(mSeed, cancelLike);
        allSeeds.put(mSeed.getSeedId(), mSeed);
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        return like;
    }

    @Override
    public List<BaseSeedInterface> getMyFavorites() {
        return mSeedProvider.getMyFavorites();
    }

    @Override
    public List<BaseSeedInterface> getSeedBySowingMonth(int month) {
        List<BaseSeedInterface> seedBySowingMonth = new ArrayList<>();
        for (BaseSeedInterface baseSeedInterface : allSeeds.values()) {
            if (baseSeedInterface.getDateSowingMin() <= month && baseSeedInterface.getDateSowingMax() >= month)
                seedBySowingMonth.add(baseSeedInterface);
        }
        if (seedBySowingMonth.size() == 0) {
            for (BaseSeedInterface seed : mSeedProvider.getSeedBySowingMonth(month)) {
                allSeeds.put(seed.getSeedId(), seed);
                seedBySowingMonth.add(seed);
            }
        }

        return seedBySowingMonth;
    }

    public synchronized String[] getArraySpecies(boolean force) {
        if (listSpecies == null || force)
            listSpecies = mSeedProvider.getArraySpecies(force);

        return listSpecies;
    }

    public synchronized String getFamilyBySpecie(String specie) {
        return mSeedProvider.getFamilyBySpecie(specie);
    }

    @Override
    public SpeciesDocument getSpecies(boolean force) throws NotImplementedException {
        return mSeedProvider.getSpecies(force);
    }

    @Override
    public BaseSeedInterface getSeedByUUID(String uuid) {
        BaseSeedInterface searchedSeed = null;
        for (BaseSeedInterface seed : allSeeds.values()) {
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
    public List<BaseSeedInterface> getRecognitionSeeds(boolean force) {
        return mSeedProvider.getRecognitionSeeds(force);
    }

    @Override
    public void createRecognitionSeed(File file, NuxeoUtils.OnBlobUpload callback) {
        mSeedProvider.createRecognitionSeed(file,callback);
    }
}
