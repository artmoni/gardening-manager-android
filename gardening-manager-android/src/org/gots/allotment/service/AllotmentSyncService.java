package org.gots.allotment.service;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.AllotmentSyncAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class AllotmentSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();

    private static GotsSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new AllotmentSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
