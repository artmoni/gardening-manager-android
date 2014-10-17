package org.gots.seed.service;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.context.GotsContext;
import org.gots.garden.GotsGardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class GotsService extends Service {
    protected GotsPreferences gotsPrefs;

    protected GotsSeedManager seedManager;

    protected GotsGardenManager gardenManager;

    protected GotsGrowingSeedManager growingSeedManager;

    protected GotsActionSeedProvider actionseedManager;

    protected GotsAllotmentManager allotmentManager;

    protected GotsContext getGotsContext() {
        return GotsContext.get(getApplicationContext());
    }    
    @Override
    public void onCreate() {
        gotsPrefs = getGotsContext().getServerConfig();

        seedManager = GotsSeedManager.getInstance().initIfNew(getApplicationContext());
        gardenManager = GotsGardenManager.getInstance().initIfNew(getApplicationContext());
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getApplicationContext());
        actionseedManager = GotsActionSeedManager.getInstance().initIfNew(getApplicationContext());
        allotmentManager = GotsAllotmentManager.getInstance().initIfNew(getApplicationContext());

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
