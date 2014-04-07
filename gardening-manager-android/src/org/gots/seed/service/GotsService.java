package org.gots.seed.service;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class GotsService extends Service {
    protected GotsPreferences gotsPrefs;

    protected GotsSeedManager seedManager;

    protected GardenManager gardenManager;

    protected GotsGrowingSeedManager growingSeedManager;

    protected GotsActionSeedProvider actionseedManager;

    @Override
    public void onCreate() {
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(this);

        seedManager = GotsSeedManager.getInstance();
        seedManager.initIfNew(this);

        gardenManager = GardenManager.getInstance();
        gardenManager.initIfNew(getApplicationContext());
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getApplicationContext());
        actionseedManager = GotsActionSeedManager.getInstance().initIfNew(getApplicationContext());

        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
