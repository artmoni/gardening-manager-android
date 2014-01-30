package org.gots.seed.service;

import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsSeedManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class GotsService extends Service {
    protected GotsPreferences gotsPrefs;

    protected GotsSeedManager seedManager;

    protected GardenManager gardenManager;

    @Override
    public void onCreate() {
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(this);

        seedManager = GotsSeedManager.getInstance();
        seedManager.initIfNew(this);

        gardenManager = GardenManager.getInstance();
        gardenManager.initIfNew(getApplicationContext());
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
