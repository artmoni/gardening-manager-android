package org.gots.seed.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.SeedSyncAdapter;
import org.gots.context.SimpleGotsApplication;

public class SeedSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static GotsSyncAdapter sSyncAdapter = null;

    private static SimpleGotsApplication application;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (application == null) {
                application = (SimpleGotsApplication) getApplicationContext();
            }
            if (sSyncAdapter == null)
                sSyncAdapter = new SeedSyncAdapter(application, true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
