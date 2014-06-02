package org.gots.allotment;

import java.util.List;

import org.gots.allotment.provider.AllotmentProvider;
import org.gots.allotment.provider.local.LocalAllotmentProvider;
import org.gots.allotment.provider.nuxeo.NuxeoAllotmentProvider;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.NotConfiguredException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AllotmentManager extends BroadcastReceiver implements AllotmentProvider {

    private static AllotmentManager instance;

    private static Exception firstCall;

    private Context mContext;

    private AllotmentProvider allotmentProvider = null;

    private boolean initDone = false;

    private AllotmentManager() {
    }

    /**
     * After first call, {@link #initIfNew(Context)} must be called else a {@link NotConfiguredException} will be thrown
     * on the second call attempt.
     */
    public static synchronized AllotmentManager getInstance() {
        if (instance == null) {
            instance = new AllotmentManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    /**
     * If it was already called once, the method returns without any change.
     */
    public synchronized AllotmentManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        // mContext.registerReceiver(this, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        setAllotmentProvider();
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

    private void setAllotmentProvider() {

        if (GotsPreferences.getInstance().isConnectedToServer()) {
            allotmentProvider = new NuxeoAllotmentProvider(mContext);
        } else {
            allotmentProvider = new LocalAllotmentProvider(mContext);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())
                || BroadCastMessages.GARDEN_SETTINGS_CHANGED.equals(intent.getAction())) {
            setAllotmentProvider();
        }
    }

    // public void setCurrentGarden(GardenInterface garden) {
    // GotsPreferences.getInstance().set(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, (int) garden.getId());
    // Log.d("setCurrentGarden", "[" + garden.getId() + "] " + garden.getLocality()
    // + " has been set as current workspace");
    // changeDatabase((int) garden.getId());
    // }

    @Override
    public BaseAllotmentInterface getCurrentAllotment() {
        return allotmentProvider.getCurrentAllotment();
    }

    @Override
    public List<BaseAllotmentInterface> getMyAllotments() {
        return allotmentProvider.getMyAllotments();
    }

    @Override
    public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment) {
        return allotmentProvider.createAllotment(allotment);
    }

    @Override
    public int removeAllotment(BaseAllotmentInterface allotment) {
        return allotmentProvider.removeAllotment(allotment);
    }

    @Override
    public BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment) {
        return allotmentProvider.updateAllotment(allotment);
    }

    @Override
    public void setCurrentAllotment(BaseAllotmentInterface allotmentInterface) {

        allotmentProvider.setCurrentAllotment(allotmentInterface);
    }
}
