package org.gots.action;

import java.util.ArrayList;

import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.action.provider.nuxeo.NuxeoActionProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.NotConfiguredException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GotsActionManager implements GotsActionProvider {

    private static GotsActionManager instance;

    private static Exception firstCall;

    GotsActionProvider provider;

    private boolean initDone;

    private Context mContext;

    private GotsPreferences gotsPrefs;

    private ArrayList<BaseActionInterface> cacheActions;

    public static synchronized GotsActionManager getInstance() {
        if (instance == null) {
            instance = new GotsActionManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    public synchronized GotsActionManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        gotsPrefs = GotsPreferences.getInstance().initIfNew(context);
        cacheActions = new ArrayList<BaseActionInterface>();
        setProvider();
        initDone = true;
        return this;
    }

    public void setProvider() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (gotsPrefs.isConnectedToServer() && ni != null && ni.isConnected()) {
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
        if (force)
            cacheActions = provider.getActions(force);
        return cacheActions;
    }

    @Override
    public BaseActionInterface createAction(BaseActionInterface action) {
        return provider.createAction(action);
    }

    @Override
    public BaseActionInterface updateAction(BaseActionInterface action) {
        return provider.updateAction(action);
    }
}
