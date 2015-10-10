package org.gots.authentication.syncadapter;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;

import java.util.List;

public class AllotmentSyncAdapter extends GotsSyncAdapter {
    public AllotmentSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        Log.d("AllotmentSyncAdapter", "onPerformSync for account[" + account.name + "]");

        getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));

        // seedManager.force_refresh(true);
        // seedManager.getVendorSeeds(true);
        //
        // seedManager.getMyStock(gardenManager.getCurrentGarden());
        //
        // List<BaseSeed> newSeeds = seedManager.getNewSeeds();
        // if (newSeeds != null && newSeeds.size() > 0) {
        // SeedNotification notification = new SeedNotification(getContext());
        // notification.createNotification(newSeeds);
        // }


        List<BaseAllotmentInterface> myAllotments = allotmentManager.getMyAllotments(true);
//        for (BaseAllotmentInterface baseAllotmentInterface : myAllotments) {
//            growingSeedManager.getGrowingSeedsByAllotment(baseAllotmentInterface, true);
//        }

        getContext().sendBroadcast(new Intent(BroadCastMessages.ALLOTMENT_EVENT));
        getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

    }
}
