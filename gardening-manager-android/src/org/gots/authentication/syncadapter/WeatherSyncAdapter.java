package org.gots.authentication.syncadapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
import org.gots.seed.GotsSeedManager;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.gots.weather.provider.nuxeo.NuxeoWeatherProvider;
import org.gots.weather.provider.previmeteo.PrevimeteoWeatherProvider;
import org.gots.weather.provider.previmeteo.WeatherProvider;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class WeatherSyncAdapter extends GotsSyncAdapter {
    protected GotsSeedManager seedManager;

    public WeatherSyncAdapter(Context context, boolean autoInitialize) {
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

        LocalWeatherProvider localProvider = new LocalWeatherProvider(getContext());
        NuxeoWeatherProvider nuxeoProvider = new NuxeoWeatherProvider(getContext(), gardenManager.getCurrentGarden());

        // Get weather history from Gardening Manager Server
        if (gotsPrefs.isConnectedToServer()) {
            List<WeatherConditionInterface> allCondition = nuxeoProvider.getAllWeatherForecast();
            for (WeatherConditionInterface weatherCondition : allCondition) {
                WeatherConditionInterface localCondition = localProvider.getCondition(weatherCondition.getDate());
                if (localCondition.getSummary() == null && weatherCondition.getSummary() != null)
                    localProvider.insertWeather(weatherCondition);
            }
        }

        // Get forecast weather from previmeteo web service
        for (int forecastDay = 0; forecastDay < 4; forecastDay++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, forecastDay);

            WeatherProvider previmeteoWeatherProvider = new PrevimeteoWeatherProvider(getContext());
            WeatherConditionInterface previmeteoCondition = previmeteoWeatherProvider.getCondition(cal.getTime());

            WeatherConditionInterface localCondition = localProvider.getCondition(cal.getTime());
            if (previmeteoCondition.getSummary() != null) {

                if (localCondition != null && localCondition.getId() > 0) {
                    previmeteoCondition.setId(localCondition.getId());
                    previmeteoCondition = localProvider.updateWeather(previmeteoCondition);
                } else {
                    previmeteoCondition = localProvider.insertWeather(previmeteoCondition);
                }
            } else if (localCondition != null && localCondition.getSummary() != null)
                previmeteoCondition = localCondition;
        }

        getContext().sendBroadcast(new Intent(BroadCastMessages.WEATHER_DISPLAY_EVENT));

        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
    }
}
