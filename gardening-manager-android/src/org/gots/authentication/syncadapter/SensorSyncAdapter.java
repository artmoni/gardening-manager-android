package org.gots.authentication.syncadapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gots.R;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.sensor.local.LocalSensorSamplesProvider;
import org.gots.sensor.notification.SensorStatusNotification;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSamplesProvider;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SensorSyncAdapter extends GotsSyncAdapter {
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

        // Get Parrot Notification Alerts
        ParrotSensorProvider parrotSensorProvider = new ParrotSensorProvider(getContext());
        List<ParrotLocationsStatus> status = parrotSensorProvider.getStatus();

        SensorStatusNotification notification = new SensorStatusNotification(getContext());
        String soilMoistureInstruction;
        String lightInstruction;
        String fertilizerInstruction;
        String airTemperatureInstruction;
        for (ParrotLocationsStatus parrotLocationsStatus : status) {

            soilMoistureInstruction = parrotLocationsStatus.getSoil_moisture().getInstruction_key();
            lightInstruction = parrotLocationsStatus.getLight().getInstruction_key();
            fertilizerInstruction = parrotLocationsStatus.getFertilizer().getInstruction_key();
            airTemperatureInstruction = parrotLocationsStatus.getAir_temperature().getInstruction_key();

            if (soilMoistureInstruction != null && soilMoistureInstruction.contains("low")) {
                notification.createNotification(getContext().getResources().getString(R.string.sensor_moisture_too_low));
            }
            if (lightInstruction != null && lightInstruction.contains("low")) {
                notification.createNotification(getContext().getResources().getString(R.string.sensor_light_too_low));
            }
            if (fertilizerInstruction != null && fertilizerInstruction.contains("low")) {
                notification.createNotification(getContext().getResources().getString(
                        R.string.sensor_fertilizer_too_low));
            }
            if (airTemperatureInstruction != null && airTemperatureInstruction.contains("low")) {
                notification.createNotification(getContext().getResources().getString(
                        R.string.sensor_airtemperature_too_low));
            }
            notification.show();
        }

        //TODO stop synch if data are already inserted
        // Get last 100 days of samples
        List<ParrotLocation> locations = parrotSensorProvider.getLocations();
        Calendar dateTo = Calendar.getInstance();
        Calendar datefrom = Calendar.getInstance();
        for (int nbDays = 10; nbDays < 100; nbDays += 10) {
            dateTo = datefrom;
            datefrom.add(Calendar.DAY_OF_YEAR, -nbDays);

            for (ParrotLocation parrotLocation : locations) {
                LocalSensorSamplesProvider localSensorProvider = new LocalSensorSamplesProvider(getContext(),
                        parrotLocation.getLocation_identifier());
                ParrotSamplesProvider parrotSamplesProvider = new ParrotSamplesProvider(getContext(),
                        parrotLocation.getLocation_identifier());

                List<ParrotSampleFertilizer> fertilizers = parrotSamplesProvider.getSamplesFertilizer(
                        datefrom.getTime(), dateTo.getTime());
                for (ParrotSampleFertilizer parrotSampleFertilizer : fertilizers) {
                    localSensorProvider.insertSampleFertilizer(parrotSampleFertilizer);
                }

                // Get Samples from last sync until now or all if never sync
                ParrotSampleTemperature lastSync = localSensorProvider.getLastSampleTemperature();
                if (lastSync == null || datefrom.getTime().after(lastSync.getCapture_ts())) {
                    List<ParrotSampleTemperature> temperatures = parrotSamplesProvider.getSamplesTemperature(
                            datefrom.getTime(), Calendar.getInstance().getTime());
                    for (ParrotSampleTemperature parrotSampleTemperature : temperatures) {
                        localSensorProvider.insertSampleTemperature(parrotSampleTemperature);
                    }
                }
            }

            intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
            getContext().sendBroadcast(intent);
        }
    }
}
