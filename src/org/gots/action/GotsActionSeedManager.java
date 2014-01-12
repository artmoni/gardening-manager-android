package org.gots.action;

import java.util.ArrayList;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.provider.nuxeo.NuxeoActionProvider;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GrowingSeedInterface;
import org.gots.utils.NotConfiguredException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GotsActionSeedManager implements GotsActionSeedProvider {

    private static GotsActionSeedManager instance;

    private static Exception firstCall;

    GotsActionSeedProvider provider;

    private boolean initDone;

    private Context mContext;

    private GotsPreferences gotsPrefs;        


    public static synchronized GotsActionSeedManager getInstance() {
        if (instance == null) {
            instance = new GotsActionSeedManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    public synchronized GotsActionSeedManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        // mContext.registerReceiver(this, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        gotsPrefs = GotsPreferences.getInstance().initIfNew(context);
        setProvider();
        initDone = true;
        return this;
    }

    public void setProvider() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (gotsPrefs.isConnectedToServer() && ni != null && ni.isConnected()) {
            provider = new NuxeoActionSeedProvider(mContext);
        } else
            provider = new LocalActionSeedProvider(mContext);
    }

    @Override
    public long doAction(BaseActionInterface action, GrowingSeedInterface seed) {
        return provider.doAction(action, seed);
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsToDo() {
        return provider.getActionsToDo();
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
        return provider.getActionsToDoBySeed(seed);
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
        return provider.getActionsDoneBySeed(seed);
    }

    @Override
    public BaseActionInterface insertAction(BaseActionInterface action, GrowingSeedInterface seed) {
        return provider.insertAction(action, seed);
    }

}
