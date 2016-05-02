package org.gots.action.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.authentication.syncadapter.ActionsSyncAdapter;

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
