package org.gots.context;

import android.content.IntentFilter;
import android.util.Log;

import org.nuxeo.android.broadcast.NuxeoBroadcastMessages;
import org.nuxeo.android.context.SimpleNuxeoApplication;

public class SimpleGotsApplication extends SimpleNuxeoApplication implements GotsContextProvider {

    private static final String TAG = SimpleGotsApplication.class.getSimpleName();

    GotsContext gotsContext;

    @Override
    public GotsContext getGotsContext() {
        if (gotsContext == null) {
            gotsContext = new GotsContext(this);
            // register as listener for global config changes
            IntentFilter filter = new IntentFilter();
            filter.addAction(NuxeoBroadcastMessages.NUXEO_SETTINGS_CHANGED);
            filter.addAction(NuxeoBroadcastMessages.NUXEO_SERVER_CONNECTIVITY_CHANGED);
            gotsContext.setDebugUnregister(true);
            this.registerReceiver(gotsContext, filter);
        }
        return gotsContext;
    }

    @Override
    public void onTerminate() {
        try {
            this.unregisterReceiver(gotsContext);
        } catch (IllegalArgumentException e) {
            Log.w(TAG, "register receiver failure on context shutdown", e);
            // Ignore
        }
        super.onTerminate();
    }

}
