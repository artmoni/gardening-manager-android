package org.gots.action;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GrowingSeed;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GotsActionSeedManager extends BroadcastReceiver implements GotsActionSeedProvider {

    private static GotsActionSeedManager instance;

    private static Exception firstCall;

    GotsActionSeedProvider provider;
    Map<Integer, ActionOnSeed> actionsToDO;
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
        actionsToDO = new HashMap<>();
        return this;
    }

    public void reset() {
        initDone = false;
        mContext = null;
        instance = null;
    }

    public void setProvider() {
        if (gotsPrefs.isConnectedToServer() && !NuxeoManager.getInstance().initIfNew(mContext).getNuxeoClient().isOffline()) {
            provider = new NuxeoActionSeedProvider(mContext);
        } else
            provider = new LocalActionSeedProvider(mContext);
    }

    @Override
    public ActionOnSeed doAction(ActionOnSeed action, GrowingSeed seed) {
        action.setDateActionDone(Calendar.getInstance().getTime());
        seed.getPlant().getActionToDo().remove(action);
        seed.getPlant().getActionDone().add(action);
        actionsToDO.remove(action.getActionSeedId());
        return provider.doAction(action, seed);
    }

    @Override
    public ArrayList<ActionOnSeed> getActionsToDo(boolean force) {
        if (force || actionsToDO.size() == 0) {
            for (ActionOnSeed actionOnSeed : provider.getActionsToDo(force)) {
                actionsToDO.put(actionOnSeed.getActionSeedId(), actionOnSeed);
            }
        }
        return new ArrayList<>(actionsToDO.values());
    }

    @Override
    public List<ActionOnSeed> getActionsToDoBySeed(GrowingSeed seed, boolean force) {
        if (force) {
            for (ActionOnSeed actionOnSeed : provider.getActionsToDoBySeed(seed, force)) {
                actionsToDO.put(actionOnSeed.getActionSeedId(), actionOnSeed);
            }
        }

        ArrayList<ActionOnSeed> actionsBySeed = new ArrayList<>();
        for (ActionOnSeed actionOnSeed : actionsToDO.values()) {
            if (actionOnSeed.getGrowingSeedId() == seed.getId())
                actionsBySeed.add(actionOnSeed);
        }
        return actionsBySeed;
    }

    @Override
    public List<ActionOnSeed> getActionsDoneBySeed(GrowingSeed seed, boolean force) {
        return provider.getActionsDoneBySeed(seed, force);
    }

    @Override
    public ActionOnSeed insertAction(GrowingSeed seed, ActionOnSeed action) {
        if (action.getDuration() > 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, action.getDuration());
            action.setDateActionTodo(calendar.getTime());
        }
        actionsToDO.put(action.getActionSeedId(), action);
        return provider.insertAction(seed, action);
    }

    @Override
    public File uploadPicture(GrowingSeed seed, File localPictureFile) {
        return provider.uploadPicture(seed, localPictureFile);
    }

    @Override
    public File downloadHistory(GrowingSeed mSeed) throws GotsServerRestrictedException {
        if (provider instanceof NuxeoActionSeedProvider)
            return provider.downloadHistory(mSeed);
        else
            throw new GotsServerRestrictedException(mContext);
    }

    @Override
    public List<File> getPicture(GrowingSeed mSeed) throws GotsServerRestrictedException {
        return provider.getPicture(mSeed);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())
                || BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction()))
            setProvider();
        if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
            setProvider();
        }
    }

}
