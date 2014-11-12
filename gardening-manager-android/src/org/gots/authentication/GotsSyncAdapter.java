package org.gots.authentication;

import org.gots.allotment.GotsAllotmentManager;
import org.gots.context.GotsContext;
import org.gots.garden.GotsGardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;

import android.accounts.AccountManager;
import android.content.Context;

public class GotsSyncAdapter extends NuxeoSyncAdapter {

    protected AccountManager mAccountManager;

    protected GotsPreferences gotsPrefs;

    protected GotsAllotmentManager allotmentManager;

    protected GotsGrowingSeedManager growingSeedManager;

    protected GotsGardenManager gardenManager;

    protected GotsSeedManager seedManager;

    protected GotsContext getGotsContext() {
        return GotsContext.get(getContext());
    }

    public GotsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);

        gotsPrefs = getGotsContext().getServerConfig();

        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getContext());
        allotmentManager = GotsAllotmentManager.getInstance().initIfNew(getContext());
        gardenManager = GotsGardenManager.getInstance().initIfNew(getContext());
        seedManager = GotsSeedManager.getInstance().initIfNew(getContext());

    }

}
