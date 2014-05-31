package org.gots.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.exception.GotsServerRestrictedException;
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

    public synchronized GotsActionSeedProvider initIfNew(Context context) {
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

    public void reset() {
        initDone = false;
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
    public long doAction(SeedActionInterface action, GrowingSeedInterface seed) {
        return provider.doAction(action, seed);
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsToDo() {
        return provider.getActionsToDo();
    }

    @Override
    public List<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
        return provider.getActionsToDoBySeed(seed);
    }

    @Override
    public List<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
        return provider.getActionsDoneBySeed(seed);
    }

    @Override
    public SeedActionInterface insertAction(GrowingSeedInterface seed, BaseActionInterface action) {
        return provider.insertAction(seed, action);
    }

    @Override
    public void uploadPicture(GrowingSeedInterface seed, File f) {
        provider.uploadPicture(seed, f);
    }

    @Override
    public File downloadHistory(GrowingSeedInterface mSeed) throws GotsServerRestrictedException {
        if (provider instanceof NuxeoActionSeedProvider)
            return provider.downloadHistory(mSeed);
        else
            throw new GotsServerRestrictedException(mContext);
    }

    @Override
    public List<File> getPicture(GrowingSeedInterface mSeed) throws GotsServerRestrictedException {
        if (provider instanceof NuxeoActionSeedProvider)
            return provider.getPicture(mSeed);
        else
            throw new GotsServerRestrictedException(mContext);
    }

}
