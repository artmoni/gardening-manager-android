package org.gots.seed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.local.GotsGrowingSeedProvider;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoGrowingSeedProvider;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GotsGrowingSeedManager extends BroadcastReceiver implements GotsGrowingSeedProvider {

    private static GotsGrowingSeedManager instance;

    private static Exception firstCall;

    GotsGrowingSeedProvider provider;

    Map<Integer, HashMap<Integer, GrowingSeedInterface>> seedsByAllotment; // Map<AllotmentID, HashMap<getGrowingSeedId,
                                                                           // GrowingSeedInterface>>

    private boolean initDone;

    private GotsPreferences gotsPrefs;

    private Context mContext;

    private boolean resetAllotments = false;

    private NuxeoManager nuxeoManager;

    private GotsGrowingSeedManager() {
        // mLocalProvider = new LocalSeedProvider(mContext);
        // allSeeds = new ArrayList<BaseSeedInterface>();
    }

    protected void setGrowingSeedProvider() {
        
        if (gotsPrefs.isConnectedToServer() && !nuxeoManager.getNuxeoClient().isOffline()) {
            provider = new NuxeoGrowingSeedProvider(mContext);
        } else
            provider = new LocalGrowingSeedProvider(mContext);
    }

    public static synchronized GotsGrowingSeedManager getInstance() {
        if (instance == null) {
            instance = new GotsGrowingSeedManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {
        if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
            resetAllotments = true;
        }
        if (NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())
                || BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction()))
            setGrowingSeedProvider();
    }

    /**
     * If it was already called once, the method returns without any change.
     */
    public synchronized GotsGrowingSeedManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        gotsPrefs = GotsPreferences.getInstance().initIfNew(context);
        nuxeoManager = NuxeoManager.getInstance().initIfNew(context);
        // mContext.registerReceiver(this, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        setGrowingSeedProvider();
        initDone = true;
        return this;
    }

    @Override
    public GrowingSeedInterface plantingSeed(GrowingSeedInterface seed, BaseAllotmentInterface allotment) {
        if (!seedsByAllotment.containsKey(allotment.getId())) {
            seedsByAllotment.put(allotment.getId(), new HashMap<Integer, GrowingSeedInterface>());
        }
        seedsByAllotment.get(allotment.getId()).put(seed.getGrowingSeedId(), seed);
        return provider.plantingSeed(seed, allotment);
    }

    @Override
    public ArrayList<GrowingSeedInterface> getGrowingSeeds() {
        return provider.getGrowingSeeds();
    }

    @Override
    public List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force) {
        if (seedsByAllotment == null || resetAllotments) {
            resetAllotments = false;
            seedsByAllotment = new HashMap<Integer, HashMap<Integer, GrowingSeedInterface>>();
        }

        if (force || seedsByAllotment.get(allotment.getId()) == null) {
            seedsByAllotment.put(allotment.getId(), new HashMap<Integer, GrowingSeedInterface>());
            for (GrowingSeedInterface seed : provider.getGrowingSeedsByAllotment(allotment, force)) {
                seedsByAllotment.get(allotment.getId()).put(seed.getGrowingSeedId(), seed);
            }
        }
        // return provider.getGrowingSeedsByAllotment(allotment, force);
        return new ArrayList<GrowingSeedInterface>(seedsByAllotment.get(allotment.getId()).values());
    }

    @Override
    public GrowingSeedInterface getGrowingSeedById(int growingSeedId) {
        return provider.getGrowingSeedById(growingSeedId);
    }

    @Override
    public void deleteGrowingSeed(GrowingSeedInterface seed) {
        for (HashMap<Integer, GrowingSeedInterface> seedMap : seedsByAllotment.values()) {
            if (seedMap.containsKey(seed.getGrowingSeedId())) {
                seedMap.remove(seed.getGrowingSeedId());
                break;
            }
        }
        provider.deleteGrowingSeed(seed);
    }

}
