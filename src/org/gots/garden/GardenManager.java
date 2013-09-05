package org.gots.garden;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.garden.provider.nuxeo.NuxeoGardenProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.NotConfiguredException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GardenManager extends BroadcastReceiver {
    private static final String TAG = "GardenManager";

    private static GardenManager instance;

    private static Exception firstCall;

    private Context mContext;

    private GardenProvider gardenProvider = null;

    private boolean initDone = false;

    private GardenManager() {
    }

    /**
     * After first call, {@link #initIfNew(Context)} must be called else a {@link NotConfiguredException} will be thrown
     * on the second call attempt.
     */
    public static synchronized GardenManager getInstance() {
        if (instance == null) {
            instance = new GardenManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    /**
     * If it was already called once, the method returns without any change.
     */
    public synchronized void initIfNew(Context context) {
        if (initDone) {
            return;
        }
        this.mContext = context;
        setGardenProvider();
        initDone = true;
    }

    public void finalize() {
        initDone = false;
        mContext = null;
        instance=null;
    }

    private void setGardenProvider() {
        // new AsyncTask<Void, Integer, Void>() {
        // @Override
        // protected Void doInBackground(Void... params) {
        if (GotsPreferences.getInstance().isConnectedToServer()) {
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
        if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())||BroadCastMessages.GARDEN_SETTINGS_CHANGED.equals(intent.getAction())) {
            setGardenProvider();
        }
    }

    public long addGarden(GardenInterface garden) {

        final long id;

        AsyncTask<GardenInterface, Integer, GardenInterface> task = new AsyncTask<GardenInterface, Integer, GardenInterface>() {
            @Override
            protected GardenInterface doInBackground(GardenInterface... params) {
                GardenInterface newGarden = gardenProvider.createGarden(params[0]);
                return newGarden;
            }

            protected void onPostExecute(GardenInterface result) {
                setCurrentGarden(result);
                GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
                tracker.trackEvent("Garden", "location", result.getLocality(), 0);
            };
        }.execute(garden);

        try {
            GardenInterface newgarden = task.get();
            return newgarden.getId();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

//    private void changeDatabase(int position) {
//        DatabaseHelper helper = new DatabaseHelper(mContext);
//        helper.setDatabase(position);
//
//        // WeatherManager wm = new WeatherManager(mContext);
//        // wm.getWeatherFromWebService(getcurrentGarden());
//
//    }

    public GardenInterface getCurrentGarden() {
        GardenInterface garden = gardenProvider.getCurrentGarden();
        return garden;
    }

    public void setCurrentGarden(GardenInterface garden) {
        GotsPreferences.getInstance().set(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, (int)garden.getId());
        Log.d(TAG, "setCurrentGarden [" + garden.getId() + "] " + garden.getLocality()
                + " has been set as current workspace");
//        changeDatabase((int) garden.getId());
    }

    public void removeGarden(GardenInterface garden) {
        new AsyncTask<GardenInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(GardenInterface... params) {
                gardenProvider.removeGarden(params[0]);
                return null;
            }
        }.execute(garden);
    }

    public void updateCurrentGarden(GardenInterface garden) {
        new AsyncTask<GardenInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(GardenInterface... params) {
                gardenProvider.updateGarden(params[0]);
                return null;
            }
        }.execute(garden);
    }

    public void update() {
        // new RefreshTask().execute(new Object(), false);

    }

    // private class RefreshTask extends AsyncTask<Object, Boolean, Long> {
    // @Override
    // protected Long doInBackground(Object... params) {
    //
    // GotsConnector connector;
    // if (!isLocalStore)
    // // connector = new SimpleConnector();
    // connector = new NuxeoConnector(mContext);
    // else
    // connector = new LocalConnector(mContext);
    // List<BaseSeedInterface> seeds = connector.getAllSeeds();
    //
    // VendorSeedDBHelper theSeedBank = new VendorSeedDBHelper(mContext);
    // for (Iterator<BaseSeedInterface> iterator = seeds.iterator();
    // iterator.hasNext();) {
    // BaseSeedInterface baseSeedInterface = iterator.next();
    // if (theSeedBank.getSeedByReference(baseSeedInterface.getReference()) ==
    // null)
    // theSeedBank.insertSeed(baseSeedInterface);
    //
    // }
    // return null;
    // }
    //
    // @Override
    // protected void onPostExecute(Long result) {
    // // VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
    // // ArrayList<BaseSeedInterface> vendorSeeds;
    // // vendorSeeds = myBank.getVendorSeeds();
    //
    // // setListAdapter(new ListVendorSeedAdapter(mContext, vendorSeeds));
    // Toast.makeText(mContext, "Updated", 20).show();
    //
    // super.onPostExecute(result);
    // }
    // }

    public List<GardenInterface> getMyGardens(boolean force) {
        return gardenProvider.getMyGardens(force);
    }

}
