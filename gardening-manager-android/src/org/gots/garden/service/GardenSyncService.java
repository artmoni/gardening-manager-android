package org.gots.garden.service;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.GardenSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GardenSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static GotsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new GardenSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
