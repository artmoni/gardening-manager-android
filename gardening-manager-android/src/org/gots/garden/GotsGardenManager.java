package org.gots.garden;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gots.bean.Garden;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.exception.GardenNotFoundException;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.garden.provider.nuxeo.NuxeoGardenProvider;
import org.gots.nuxeo.NuxeoManager;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class GotsGardenManager extends BroadcastReceiver {
    private static final String TAG = "GardenManager";

    private static GotsGardenManager instance;

    private static Exception firstCall;

    private Context mContext;

    private GardenProvider gardenProvider = null;

    private boolean initDone = false;

    private Map<Long, GardenInterface> myGardens;

    private GardenInterface currentGarden;

    private NuxeoManager nuxeoManager;

    private GotsGardenManager() {
    }

    /**
     * After first call, {@link #initIfNew(Context)} must be called else a {@link NotConfiguredException} will be thrown
     * on the second call attempt.
     */
    public static synchronized GotsGardenManager getInstance() {
        if (instance == null) {
            instance = new GotsGardenManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    public void reset() {
        initDone = false;
    }

    /**
     * If it was already called once, the method returns without any change.
     * 
     * @return TODO
     */
    public synchronized GotsGardenManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        nuxeoManager = NuxeoManager.getInstance().initIfNew(context);
        setGardenProvider();
        initDone = true;
        return instance;
    }

    public void finalize() {
        initDone = false;
        mContext = null;
        instance = null;
    }

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    private void setGardenProvider() {

        if (getGotsContext().getServerConfig().isConnectedToServer() && !nuxeoManager.getNuxeoClient().isOffline()) {
            gardenProvider = new NuxeoGardenProvider(mContext);
        } else {
            // return null;
            // }
            // }.execute();
            gardenProvider = new LocalGardenProvider(mContext);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())
                || NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())) {
            setGardenProvider();
        }
    }

    public GardenInterface addGarden(GardenInterface garden) {
        GardenInterface newGarden = gardenProvider.createGarden(garden);
        myGardens.put(newGarden.getId(), newGarden);
        // gardenProvider.setCurrentGarden(newGarden);
        // GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        // tracker.trackEvent("Garden", "location", result.getLocality(), 0);
        return newGarden;
    }

    public GardenInterface getCurrentGarden() {
        if (currentGarden == null)
            currentGarden = gardenProvider.getCurrentGarden();
        if (currentGarden == null)
            // throw new GardenNotFoundException();
            currentGarden = new Garden();
        return currentGarden;
    }

    public void setCurrentGarden(GardenInterface garden) {
        currentGarden = garden;
        gardenProvider.setCurrentGarden(garden);
        // mContext.sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));

        Log.d(TAG, "[" + garden.getId() + "] " + garden.getLocality() + " has been set as current garden");
    }

    public void removeGarden(GardenInterface garden) {
        gardenProvider.removeGarden(garden);
        myGardens.remove(garden.getId());
    }

    public void updateCurrentGarden(GardenInterface garden) {
        gardenProvider.updateGarden(garden);
        myGardens.put(garden.getId(), garden);

    }

    public List<GardenInterface> getMyGardens(boolean force) {
        if (myGardens == null || force) {
            myGardens = new HashMap<Long, GardenInterface>();
            for (GardenInterface garden : gardenProvider.getMyGardens(force)) {
                myGardens.put(garden.getId(), garden);
                gardenProvider.getUsersAndGroups(garden);
            }
        }

        // myGardens = gardenProvider.getMyGardens(force);
        return new ArrayList<GardenInterface>(myGardens.values());
    }

    public int share(GardenInterface garden, String user, String permission) {
        if (gardenProvider instanceof NuxeoGardenProvider) {
            return gardenProvider.share(garden, user, permission);
        }
        return -1;
    }

}
