package org.gots.seed.service;

import org.gots.authentication.SeedSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SeedSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static SeedSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new SeedSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
