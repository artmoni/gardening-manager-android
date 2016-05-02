package org.gots.authentication;

import android.accounts.AccountManager;
import android.content.Context;

import org.gots.allotment.GotsAllotmentManager;
import org.gots.allotment.provider.AllotmentProvider;
import org.gots.context.GotsContext;
import org.gots.garden.GotsGardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;

public class GotsSyncAdapter extends NuxeoSyncAdapter {

    protected static final String TAG = GotsSyncAdapter.class.getSimpleName();
    protected AccountManager mAccountManager;
    protected GotsPreferences gotsPrefs;
    protected AllotmentProvider allotmentManager;
    protected GotsGrowingSeedManager growingSeedManager;
    protected GotsGardenManager gardenManager;
    protected GotsSeedManager seedManager;

    public GotsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);

        gotsPrefs = getGotsContext().getServerConfig();

        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getContext());
        allotmentManager = GotsAllotmentManager.getInstance().initIfNew(getContext());
        gardenManager = GotsGardenManager.getInstance().initIfNew(getContext());
        seedManager = GotsSeedManager.getInstance().initIfNew(getContext());

    }

    public GotsContext getGotsContext() {
        return GotsContext.get(getContext());
    }

}
