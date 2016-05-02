package org.gots.authentication.syncadapter;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;

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

import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
        Calendar dateLastUploadedSamples = Calendar.getInstance();
        Calendar dateTo = Calendar.getInstance();

        for (ParrotLocation parrotLocation : locations) {
            for (ParrotLocationsStatus parrotLocationsStatus : status) {
                if (parrotLocationsStatus.getLocation_identifier().equals(parrotLocation.getLocation_identifier())) {
                    final Date global_validity_timedate_utc = parrotLocationsStatus.getGlobal_validity_timedate_utc();
                    if (global_validity_timedate_utc != null)
                        dateLastUploadedSamples.setTime(global_validity_timedate_utc);
                    else
                        dateLastUploadedSamples.setTime(parrotLocationsStatus.getLast_processed_upload_timedate_utc());
                    break;
                }
            }

            LocalSensorSamplesProvider localSensorProvider = new LocalSensorSamplesProvider(getContext(),
                    parrotLocation.getLocation_identifier());
            ParrotSamplesProvider parrotSamplesProvider = new ParrotSamplesProvider(getContext(),
                    parrotLocation.getLocation_identifier());
            final ParrotSampleTemperature lastSampleTemperature = localSensorProvider.getLastSampleTemperature();

            Calendar dateFrom = Calendar.getInstance();
            if (lastSampleTemperature != null) {
                dateFrom.setTime(lastSampleTemperature.getCapture_ts());

            } else {
                dateFrom.add(Calendar.DAY_OF_YEAR, -100);
                Log.d(TAG, "---" + "No temperature found locally, retrieve from last 100 days");
            }
            dateTo.setTime(dateFrom.getTime());
            Log.d(TAG,
                    "---" + parrotLocation + " last uploaded samples on "
                            + DateFormat.format("yyyy-MM-dd HH:mm:ss", dateLastUploadedSamples)
                            + " / last locally stored on " + DateFormat.format("yyyy-MM-dd HH:mm:ss", dateFrom));
            for (int nbDays = 10; dateLastUploadedSamples.compareTo(dateFrom) > 0 && nbDays < 100; dateFrom.setTime(dateTo.getTime()), nbDays += 10) {
                dateTo.add(Calendar.DAY_OF_YEAR, nbDays);
                Log.d(TAG,
                        "------" + parrotLocation + " sync data from  "
                                + DateFormat.format("yyyy-MM-dd HH:mm:ss", dateFrom) + " to "
                                + DateFormat.format("yyyy-MM-dd HH:mm:ss", dateTo));

                List<ParrotSampleFertilizer> fertilizers = parrotSamplesProvider.getSamplesFertilizer(
                        dateFrom.getTime(), dateTo.getTime());
                for (ParrotSampleFertilizer parrotSampleFertilizer : fertilizers) {
                    localSensorProvider.insertSampleFertilizer(parrotSampleFertilizer);
                }

                // Get Samples from last sync until now or all if never sync
                List<ParrotSampleTemperature> temperatures = parrotSamplesProvider.getSamplesTemperature(
                        dateFrom.getTime(), dateTo.getTime());
                for (ParrotSampleTemperature parrotSampleTemperature : temperatures) {
                    localSensorProvider.insertSampleTemperature(parrotSampleTemperature);
                }
            }

            intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
            getContext().sendBroadcast(intent);
        }
    }
}
