package org.gots.authentication.syncadapter;

import java.util.List;

import org.gots.action.GotsActionSeedManager;
import org.gots.allotment.AllotmentManager;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.service.SeedNotification;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class ActionsSyncAdapter extends GotsSyncAdapter {
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
        actionseedManager.getActionsToDo();
        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
    }
}
