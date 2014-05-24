package org.gots.sensor.service;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.SensorSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SensorSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static GotsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new SensorSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
