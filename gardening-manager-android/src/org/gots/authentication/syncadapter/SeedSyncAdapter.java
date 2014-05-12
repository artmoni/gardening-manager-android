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

public class SeedSyncAdapter extends GotsSyncAdapter {
    public SeedSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }

    private Thread thread;

    boolean shouldcontinue = true;

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("SeedSyncAdapter", "onPerformSync for account[" + account.name + "]");

        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (shouldcontinue) {
                        getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
                        sleep(1000);
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        seedManager.force_refresh(true);
        seedManager.getMyStock(gardenManager.getCurrentGarden());

        List<BaseSeedInterface> newSeeds = seedManager.getNewSeeds();
        if (newSeeds != null && newSeeds.size() > 0) {
            SeedNotification notification = new SeedNotification(getContext());
            notification.createNotification(newSeeds);
        }
        // handler.removeCallbacks(sendUpdatesToUI);
        // handler.postDelayed(sendUpdatesToUI, 0); // 1 second
        shouldcontinue = false;
        getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

    }
}