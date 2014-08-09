package org.gots.authentication.syncadapter;

import java.util.Calendar;
import java.util.List;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GotsSeedManager;
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

        WeatherProvider localProvider = new LocalWeatherProvider(getContext());

        // Get weather history from Gardening Manager Server
        if (gotsPrefs.isConnectedToServer()) {
            NuxeoWeatherProvider nuxeoProvider = new NuxeoWeatherProvider(getContext(),
                    gardenManager.getCurrentGarden());
            List<WeatherConditionInterface> allCondition = nuxeoProvider.getAllWeatherForecast();
            for (WeatherConditionInterface weatherCondition : allCondition) {
                WeatherConditionInterface localCondition = localProvider.getCondition(weatherCondition.getDate());
                if (localCondition.getSummary() == null && weatherCondition.getSummary() != null)
                    localProvider.insertCondition(weatherCondition);
            }
        }

        
        // Get forecast weather from previmeteo web service
        WeatherProvider weatherProvider = null;
        if (gotsPrefs.isConnectedToServer())
            weatherProvider=new NuxeoWeatherProvider(getContext(), gardenManager.getCurrentGarden());
        else
            weatherProvider = new LocalWeatherProvider(getContext());
        for (int forecastDay = 0; forecastDay < 4; forecastDay++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, forecastDay);

            WeatherProvider previmeteoWeatherProvider = new PrevimeteoWeatherProvider(getContext());
            WeatherConditionInterface previmeteoCondition = previmeteoWeatherProvider.getCondition(cal.getTime());

            WeatherConditionInterface localCondition = weatherProvider.getCondition(cal.getTime());
            if (previmeteoCondition != null && previmeteoCondition.getSummary() != null) {
                if (localCondition != null && localCondition.getId() > 0) {
                    previmeteoCondition.setId(localCondition.getId());
                    previmeteoCondition = weatherProvider.updateCondition(previmeteoCondition,cal.getTime());
                } else {
                    previmeteoCondition = weatherProvider.insertCondition(previmeteoCondition);
                }
            } 
        }


        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
        getContext().sendBroadcast(new Intent(BroadCastMessages.WEATHER_DISPLAY_EVENT));
    }
}
