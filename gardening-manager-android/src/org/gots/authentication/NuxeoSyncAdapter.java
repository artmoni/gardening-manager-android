package org.gots.authentication;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;

public abstract class NuxeoSyncAdapter extends AbstractThreadedSyncAdapter {

    public NuxeoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public NuxeoSyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    protected NuxeoContext getNuxeoContext() {
        return NuxeoContext.get(getContext());
    }

    protected Session getNuxeoSession() {
        return getNuxeoContext().getSession();
    }

    protected AndroidAutomationClient getAutomationClient() {
        return getNuxeoContext().getNuxeoClient();
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        // TODO Auto-generated method stub

    }
}