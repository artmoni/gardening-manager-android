package org.gots.seed.service;

import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsSeedManager;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public abstract class GotsService extends Service {
    GotsPreferences gotsPrefs;
    GotsSeedManager seedManager;

    @Override
    public void onCreate() {
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(this);

        seedManager = GotsSeedManager.getInstance();
        seedManager.initIfNew(this);
    
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
