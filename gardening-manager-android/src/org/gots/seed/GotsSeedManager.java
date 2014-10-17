package org.gots.seed;

import java.util.ArrayList;
import java.util.List;

import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.exception.GotsException;
import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class GotsSeedManager extends BroadcastReceiver implements GotsSeedProvider {

    private static final String TAG = "GotsSeedManager";

    private static GotsSeedManager instance;

    private Context mContext;

    private GotsSeedProvider mSeedProvider;

    private boolean initDone = false;

    private List<BaseSeedInterface> newSeeds = new ArrayList<BaseSeedInterface>();

    private List<BaseSeedInterface> allSeeds = new ArrayList<BaseSeedInterface>();

    private static Exception firstCall;

    private List<BaseSeedInterface> myStock;

    private GotsPreferences gotsPrefs;

    private String[] listSpecies;

    private NuxeoManager nuxeoManager;

    private GotsSeedManager() {
        // mLocalProvider = new LocalSeedProvider(mContext);
        allSeeds = new ArrayList<BaseSeedInterface>();
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
        gotsPrefs = (GotsPreferences)getGotsContext().getServerConfig();
        nuxeoManager = NuxeoManager.getInstance().initIfNew(context);

        setSeedProvider();
        initDone = true;
        return this;
    }

    public void reset() {
        initDone = false;
    }

    public void finalize() {
        // mContext.unregisterReceiver(this);
        initDone = false;
        mContext = null;
        instance = null;
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds(boolean force) {
        if (!force && allSeeds.size() > 0)
            return allSeeds;

        if (force && !nuxeoManager.getNuxeoClient().isOffline()) {
            GotsSeedProvider provider = new NuxeoSeedProvider(mContext);
            allSeeds = provider.getVendorSeeds(force);
            newSeeds = provider.getNewSeeds();
        } else {
            GotsSeedProvider provider = new LocalSeedProvider(mContext);
            allSeeds = provider.getVendorSeeds(force);
        }

        return allSeeds;
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
        return mSeedProvider.getSeedById(seedId);
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {

        return mSeedProvider.createSeed(seed);
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        return mSeedProvider.updateSeed(newSeed);
    }

    @Override
    public void addToStock(final BaseSeedInterface vendorSeed, final GardenInterface garden) {
        new AsyncTask<GardenInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(GardenInterface... params) {
                mSeedProvider.addToStock(vendorSeed, garden);
                return null;
            }

            protected void onPostExecute(Void result) {
                force_refresh(true);
                mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));

            };
        }.execute(garden);

    }

    @Override
    public void removeToStock(final BaseSeedInterface vendorSeed, final GardenInterface garden) {
        new AsyncTask<GardenInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(GardenInterface... params) {
                mSeedProvider.removeToStock(vendorSeed, garden);
                return null;
            }

            protected void onPostExecute(Void result) {
                force_refresh(true);
                mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
            };
        }.execute(garden);

    }

    @Override
    public List<BaseSeedInterface> getMyStock(GardenInterface garden) {
        // if (stockChanged || myStock == null)
        myStock = mSeedProvider.getMyStock(garden);
        return myStock;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())
                || NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())) {
            setSeedProvider();
        }
    }

    @Override
    public void deleteSeed(BaseSeedInterface vendorSeed) {
        new AsyncTask<BaseSeedInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(BaseSeedInterface... params) {
                mSeedProvider.deleteSeed(params[0]);
                return null;
            }

            protected void onPostExecute(Void result) {
                mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
            };
        }.execute(vendorSeed);
    }

    @Override
    public List<BaseSeedInterface> getNewSeeds() {
        return newSeeds;
    }

    public void force_refresh(boolean refresh) {
        mSeedProvider.force_refresh(refresh);
    }

    @Override
    public synchronized BaseSeedInterface getSeedByBarCode(String barecode) {
        return mSeedProvider.getSeedByBarCode(barecode);
    }

    @Override
    public List<BaseSeedInterface> getVendorSeedsByName(String currentFilter) {
        return mSeedProvider.getVendorSeedsByName(currentFilter);
    }

    public LikeStatus like(BaseSeedInterface mSeed, boolean cancelLike) throws GotsException {
        return mSeedProvider.like(mSeed, cancelLike);
    }

    @Override
    public List<BaseSeedInterface> getMyFavorites() {
        return mSeedProvider.getMyFavorites();
    }

    @Override
    public List<BaseSeedInterface> getSeedBySowingMonth(int month) {
        List<BaseSeedInterface> monthlySeed = new ArrayList<BaseSeedInterface>();
        for (BaseSeedInterface baseSeedInterface : allSeeds) {
            if (month >= baseSeedInterface.getDateSowingMin() && month <= baseSeedInterface.getDateSowingMax())
                monthlySeed.add(baseSeedInterface);
        }
        return monthlySeed;
    }

    public synchronized String[] getArraySpecies(boolean force) {
        if (listSpecies == null || force)
            listSpecies = mSeedProvider.getArraySpecies(force);

        return listSpecies;
    }

    public synchronized String getFamilyBySpecie(String specie) {
        return mSeedProvider.getFamilyBySpecie(specie);
    }
}
