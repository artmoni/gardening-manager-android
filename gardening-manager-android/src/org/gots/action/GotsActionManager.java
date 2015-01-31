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

    private ArrayList<BaseAction> cacheActions;

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
        cacheActions = new ArrayList<BaseAction>();
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
    public BaseAction getActionById(int id) {
        return provider.getActionById(id);
    }

    @Override
    public BaseAction getActionByName(String name) {
        return provider.getActionByName(name);
    }

    @Override
    public ArrayList<BaseAction> getActions(boolean force) {
        if (force == false && cacheActions.size() > 0)
            return cacheActions;
        return provider.getActions(force);
    }

    @Override
    public BaseAction createAction(BaseAction action) {
        final BaseAction createAction = provider.createAction(action);
        cacheActions.add(createAction);
        return createAction;
    }

    @Override
    public BaseAction updateAction(BaseAction action) {
        return provider.updateAction(action);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED.equals(intent.getAction())
                || BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction()))
            setProvider();

    }
}
