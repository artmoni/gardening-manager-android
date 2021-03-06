package org.gots.authentication.syncadapter;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.exception.GardenNotFoundException;
import org.gots.garden.GardenInterface;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.exception.UnknownWeatherException;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.gots.weather.provider.nuxeo.NuxeoWeatherProvider;
import org.gots.weather.provider.previmeteo.WeatherProvider;

import java.util.Calendar;
import java.util.List;

public class WeatherSyncAdapter extends GotsSyncAdapter {
    private String TAG = WeatherSyncAdapter.class.getSimpleName();

    private GardenInterface currentGarden;

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
                              SyncResult syncResult) {

        Log.d(TAG, "onPerformSync for account[" + account.name + "]");
        final Intent intent = new Intent();
        intent.setAction(BroadCastMessages.PROGRESS_UPDATE);
        intent.putExtra("AUTHORITY", authority);
        getContext().sendBroadcast(intent);

        WeatherProvider localProvider = new LocalWeatherProvider(getContext());

        // Get weather history from Gardening Manager Server
        try {
            currentGarden = gardenManager.getCurrentGarden();
        } catch (GardenNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        if (gotsPrefs.isConnectedToServer() && currentGarden != null) {
            NuxeoWeatherProvider nuxeoProvider;
            nuxeoProvider = new NuxeoWeatherProvider(getContext(), currentGarden);
            List<WeatherConditionInterface> allCondition = nuxeoProvider.getAllWeatherForecast();
            for (WeatherConditionInterface weatherCondition : allCondition) {
                WeatherConditionInterface localCondition;
                try {
                    localCondition = localProvider.getCondition(weatherCondition.getDate());
                } catch (UnknownWeatherException e) {
                    localCondition = localProvider.insertCondition(weatherCondition);
                }
            }
        }

        // Get forecast weather from previmeteo web service
        WeatherProvider nuxeoWeatherProvider = null;
        if (gotsPrefs.isConnectedToServer() && currentGarden != null)
            nuxeoWeatherProvider = new NuxeoWeatherProvider(getContext(), currentGarden);
//        else
//            weatherProvider = new LocalWeatherProvider(getContext());

        WeatherProvider weatherManager = new WeatherManager(getContext());
        short weatherRequestState = weatherManager.fetchWeatherForecast(currentGarden);
        if (weatherRequestState == WeatherManager.WEATHER_OK && nuxeoWeatherProvider != null) {
            for (int forecastDay = 0; forecastDay < 4; forecastDay++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, forecastDay);

                WeatherConditionInterface localWeatherCondition;
                try {
                    localWeatherCondition = weatherManager.getCondition(cal.getTime());
                    WeatherConditionInterface storedCondition = nuxeoWeatherProvider.getCondition(cal.getTime());

                    if (storedCondition != null && storedCondition.getId() > 0) {
                        localWeatherCondition.setId(storedCondition.getId());
                        localWeatherCondition = nuxeoWeatherProvider.updateCondition(localWeatherCondition);
                    } else {
                        localWeatherCondition = nuxeoWeatherProvider.insertCondition(localWeatherCondition);
                    }
                } catch (UnknownWeatherException e) {
                    Log.w(TAG, e.getMessage(), e);
                }

                // WeatherConditionInterface localCondition = weatherProvider.getCondition(cal.getTime());
                // if (previmeteoCondition != null && previmeteoCondition.getSummary() != null) {
                // if (localCondition != null && localCondition.getId() > 0) {
                // previmeteoCondition.setSeedId(localCondition.getId());
                // previmeteoCondition = weatherProvider.updateCondition(previmeteoCondition, cal.getTime());
                // } else {
                // previmeteoCondition = weatherProvider.insertCondition(previmeteoCondition);
                // }
                // }
            }
        } else {
            Log.e(TAG, "Weather cannot be found for city " + currentGarden.getLocality());
        }

        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
        getContext().sendBroadcast(new Intent(BroadCastMessages.WEATHER_DISPLAY_EVENT));
    }
}
