package org.gots.authentication.syncadapter;

import java.util.Iterator;
import java.util.List;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.service.SeedNotification;
import org.gots.sensor.notification.SensorStatusNotification;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SensorSyncAdapter extends GotsSyncAdapter {
    protected GotsSeedManager seedManager;

    public SensorSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("SensorSyncAdapter", "onPerformSync for account[" + account.name + "]");

        final Intent intent = new Intent();
        intent.setAction(BroadCastMessages.PROGRESS_UPDATE);
        intent.putExtra("AUTHORITY", authority);
        getContext().sendBroadcast(intent);

        ParrotSensorProvider parrotSensorProvider = new ParrotSensorProvider(getContext());
        List<ParrotLocationsStatus> status = parrotSensorProvider.getStatus();

        SensorStatusNotification notification = new SensorStatusNotification(getContext());
        for (ParrotLocationsStatus parrotLocationsStatus : status) {
            if (parrotLocationsStatus.getSoil_moisture().getInstruction_key() != null) {
                notification.createNotification(parrotLocationsStatus.getSoil_moisture().getInstruction_key());
            }
            if (parrotLocationsStatus.getLight().getInstruction_key() != null) {
                notification.createNotification(parrotLocationsStatus.getLight().getInstruction_key());
            }
            if (parrotLocationsStatus.getFertilizer().getInstruction_key() != null) {
                notification.createNotification(parrotLocationsStatus.getFertilizer().getInstruction_key());
            }
            if (parrotLocationsStatus.getAir_temperature().getInstruction_key() != null) {
                notification.createNotification(parrotLocationsStatus.getAir_temperature().getInstruction_key());
            }
            notification.show();
        }
        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);

    }

}
