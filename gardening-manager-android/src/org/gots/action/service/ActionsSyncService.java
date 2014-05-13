package org.gots.action.service;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.ActionsSyncAdapter;
import org.gots.authentication.syncadapter.AllotmentSyncAdapter;
import org.gots.authentication.syncadapter.GardenSyncAdapter;
import org.gots.authentication.syncadapter.SeedSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class ActionsSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static GotsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new ActionsSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
