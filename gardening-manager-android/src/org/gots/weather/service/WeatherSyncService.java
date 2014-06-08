package org.gots.weather.service;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.WeatherSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WeatherSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static GotsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new WeatherSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
