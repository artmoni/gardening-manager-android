package org.gots.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GrowingSeedInterface;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GotsActionSeedManager extends BroadcastReceiver implements GotsActionSeedProvider {

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
    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }
    public synchronized GotsActionSeedProvider initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        // mContext.registerReceiver(this, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        gotsPrefs = getGotsContext().getServerConfig();
        setProvider();
        initDone = true;
        return this;
    }

    public void reset() {
        initDone = false;
    }

    public void setProvider() {
        if (gotsPrefs.isConnectedToServer() && !NuxeoManager.getInstance().getNuxeoClient().isOffline()) {
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
    public List<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed, boolean force) {
        return provider.getActionsToDoBySeed(seed, force);
    }

    @Override
    public List<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed, boolean force) {
        return provider.getActionsDoneBySeed(seed, force);
    }

    @Override
    public SeedActionInterface insertAction(GrowingSeedInterface seed, BaseActionInterface action) {
        return provider.insertAction(seed, action);
    }

    @Override
    public File uploadPicture(GrowingSeedInterface seed, File localPictureFile) {
       return provider.uploadPicture(seed, localPictureFile);
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
        return provider.getPicture(mSeed);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())
                || BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction()))
            setProvider();
    }

}
