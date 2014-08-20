package org.gots.authentication;

import org.gots.allotment.GotsAllotmentManager;
import org.gots.garden.GotsGardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class GotsSyncAdapter extends NuxeoSyncAdapater {

    protected AccountManager mAccountManager;

    protected GotsPreferences gotsPrefs;

    protected GotsAllotmentManager allotmentManager;

    protected GotsGrowingSeedManager growingSeedManager;

    protected GotsGardenManager gardenManager;

    protected GotsSeedManager seedManager;

    public GotsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);

        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(getContext());

        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getContext());
        allotmentManager = GotsAllotmentManager.getInstance().initIfNew(getContext());
        gardenManager = GotsGardenManager.getInstance().initIfNew(getContext());
        seedManager = GotsSeedManager.getInstance().initIfNew(getContext());

    }

}
