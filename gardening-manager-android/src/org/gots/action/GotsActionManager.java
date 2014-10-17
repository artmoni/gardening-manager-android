package org.gots.action;

import java.util.ArrayList;

import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.action.provider.nuxeo.NuxeoActionProvider;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.NotConfiguredException;
import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GotsActionManager extends BroadcastReceiver implements GotsActionProvider {

    private static GotsActionManager instance;

    private static Exception firstCall;

    GotsActionProvider provider;

    private boolean initDone;

    private Context mContext;

    private GotsPreferences gotsPrefs;

    private ArrayList<BaseActionInterface> cacheActions;

    private NuxeoManager nuxeoManager;

    public static synchronized GotsActionManager getInstance() {
        if (instance == null) {
            instance = new GotsActionManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }
    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }
    public synchronized GotsActionManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        gotsPrefs = getGotsContext().getServerConfig();
        nuxeoManager = NuxeoManager.getInstance().initIfNew(context);
        cacheActions = new ArrayList<BaseActionInterface>();
        setProvider();
        initDone = true;
        return this;
    }

    public void setProvider() {

        if (gotsPrefs.isConnectedToServer() && !nuxeoManager.getNuxeoClient().isOffline()) {
            provider = new NuxeoActionProvider(mContext);
        } else
            provider = new LocalActionProvider(mContext);
    }

    @Override
    public BaseActionInterface getActionById(int id) {
        return provider.getActionById(id);
    }

    @Override
    public BaseActionInterface getActionByName(String name) {
        return provider.getActionByName(name);
    }

    @Override
    public ArrayList<BaseActionInterface> getActions(boolean force) {
        return provider.getActions(force);
    }

    @Override
    public BaseActionInterface createAction(BaseActionInterface action) {
        return provider.createAction(action);
    }

    @Override
    public BaseActionInterface updateAction(BaseActionInterface action) {
        return provider.updateAction(action);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())
                || BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction()))
            setProvider();

    }
}
