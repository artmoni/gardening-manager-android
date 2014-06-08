package org.gots.authentication;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.AllotmentManager;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

public class GotsSyncAdapter extends AbstractThreadedSyncAdapter {

    protected AccountManager mAccountManager;

    protected GotsPreferences gotsPrefs;

   
  
    protected AllotmentManager allotmentManager;

    protected GotsGrowingSeedManager growingSeedManager;

    protected GardenManager gardenManager;

    public GotsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);

        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(getContext());

        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getContext());
        allotmentManager = AllotmentManager.getInstance().initIfNew(getContext());
        gardenManager =GardenManager.getInstance().initIfNew(getContext());
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        // TODO Auto-generated method stub

    }

}
