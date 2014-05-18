package org.gots.authentication.syncadapter;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GotsSeedManager;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class ActionsSyncAdapter extends GotsSyncAdapter {
    protected GotsSeedManager seedManager;

    public ActionsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("ActionsSyncAdapter", "onPerformSync for account[" + account.name + "]");
        final Intent intent = new Intent();
        intent.setAction(BroadCastMessages.PROGRESS_UPDATE);
        intent.putExtra("AUTHORITY", authority);
        getContext().sendBroadcast(intent);
        //
        //
        // seedManager.force_refresh(true);
        // seedManager.getVendorSeeds(true);
        //
        // seedManager.getMyStock(gardenManager.getCurrentGarden());
        //
        // List<BaseSeedInterface> newSeeds = seedManager.getNewSeeds();
        // if (newSeeds != null && newSeeds.size() > 0) {
        // SeedNotification notification = new SeedNotification(getContext());
        // notification.createNotification(newSeeds);
        // }
         GotsActionSeedProvider actionseedManager= GotsActionSeedManager.getInstance().initIfNew(getContext());

        actionseedManager.getActionsToDo();
        
        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
    }
}
