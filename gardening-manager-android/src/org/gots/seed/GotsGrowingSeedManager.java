package org.gots.seed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.local.GotsGrowingSeedProvider;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoGrowingSeedProvider;
import org.gots.utils.NotConfiguredException;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

public class GotsGrowingSeedManager implements GotsGrowingSeedProvider {

    private static GotsGrowingSeedManager instance;

    private static Exception firstCall;

    GotsGrowingSeedProvider provider;

    Map<Integer, GrowingSeedInterface> cacheGrowingSeed = new HashMap<Integer, GrowingSeedInterface>();

    Map<Integer, HashMap<Integer, GrowingSeedInterface>> growingSeeds;

    private boolean initDone;

    private GotsPreferences gotsPrefs;

    private Context mContext;

    private GotsGrowingSeedManager() {
        // mLocalProvider = new LocalSeedProvider(mContext);
        // allSeeds = new ArrayList<BaseSeedInterface>();
    }

    protected void setGrowingSeedProvider() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (gotsPrefs.isConnectedToServer() && ni != null && ni.isConnected()) {
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

    /**
     * If it was already called once, the method returns without any change.
     */
    public synchronized GotsGrowingSeedManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        gotsPrefs = GotsPreferences.getInstance().initIfNew(context);
        // mContext.registerReceiver(this, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        setGrowingSeedProvider();
        initDone = true;
        return this;
    }

    @Override
    public GrowingSeedInterface plantingSeed(GrowingSeedInterface seed, BaseAllotmentInterface allotment) {
        return provider.plantingSeed(seed, allotment);
    }

    @Override
    public ArrayList<GrowingSeedInterface> getGrowingSeeds() {
        return provider.getGrowingSeeds();
    }

    @Override
    public List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force) {
//        if (force || growingSeeds == null || growingSeeds.get(allotment.getId()) == null) {
//            growingSeeds = new HashMap<Integer, HashMap<Integer, GrowingSeedInterface>>();
//            
//        }
        return provider.getGrowingSeedsByAllotment(allotment, false);
    }

    @Override
    public GrowingSeedInterface getGrowingSeedById(int growingSeedId) {
        return provider.getGrowingSeedById(growingSeedId);
    }

    @Override
    public void deleteGrowingSeed(GrowingSeedInterface seed) {
        new AsyncTask<GrowingSeedInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(GrowingSeedInterface... params) {
                provider.deleteGrowingSeed(params[0]);
                return null;
            }

            protected void onPostExecute(Void result) {
                mContext.sendBroadcast(new Intent(BroadCastMessages.GROWINGSEED_DISPLAYLIST));
            };
        }.execute(seed);

    }

}
