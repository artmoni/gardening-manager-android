package org.gots.authentication.syncadapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
import org.gots.seed.GotsSeedManager;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class GardenSyncAdapter extends GotsSyncAdapter {

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

        final Intent intent = new Intent();
        intent.setAction(BroadCastMessages.PROGRESS_UPDATE);
        intent.putExtra("AUTHORITY", authority);
        getContext().sendBroadcast(intent);

        GardenManager gardenManager = GardenManager.getInstance().initIfNew(getContext());
        gardens = gardenManager.getMyGardens(true);

        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);

    }
}
