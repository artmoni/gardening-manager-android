package org.gots.authentication.syncadapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.action.GotsActionSeedManager;
import org.gots.allotment.AllotmentManager;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
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

public class GardenSyncAdapter extends GotsSyncAdapter {
    private Thread thread;

    boolean shouldcontinue = true;

    private List<GardenInterface> gardens = new ArrayList<GardenInterface>();

    private Context mContext;

    public GardenSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("GardenSyncAdapter", "onPerformSync for account[" + account.name + "]");
        gardens.clear();
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (shouldcontinue) {
                        mContext.sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
                        sleep(1000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        gardens = gardenManager.getMyGardens(true);
        shouldcontinue=false;
        mContext.sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

    }
}
