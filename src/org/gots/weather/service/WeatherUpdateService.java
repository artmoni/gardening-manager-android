package org.gots.weather.service;

import java.util.Calendar;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.provider.WeatherTask;
import org.gots.weather.provider.previmeteo.PrevimeteoWeatherTask;
import org.gots.weather.sql.WeatherDBHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class WeatherUpdateService extends Service {
    private static final String TAG = "WeatherUpdateService";

    private Intent intent;

    private final Handler handler = new Handler();

    private boolean isWeatherError = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(BroadCastMessages.WEATHER_DISPLAY_EVENT);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "getWeatherFromWebService");
        GardenManager gardenManager = new GardenManager(this);

        getWeatherFromWebService(gardenManager.getcurrentGarden());

        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            displayWeatherAvailable();
            // handler.postDelayed(this, 5000); // 5 seconds
            stopSelf();
        }
    };

    private void displayWeatherAvailable() {
        Log.d(TAG, "entered displayWeatherAvailable");

        intent.putExtra("error", isWeatherError);
        // intent.putExtra("counter", String.valueOf(++counter));
        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void getWeatherFromWebService(GardenInterface garden) {

        try {
            for (int forecastDay = 0; forecastDay < 4; forecastDay++) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, forecastDay);

                // GoogleWeatherTask(garden.getAddress(), cal.getTime());
                WeatherTask wt = new PrevimeteoWeatherTask(this, garden.getAddress(), cal.getTime());
                WeatherConditionInterface conditionInterface = wt.execute().get();

                if (conditionInterface != null) {
                    updateCondition(conditionInterface, forecastDay);

                }

                else {
                    // Toast.makeText(mContext,
                    // mContext.getResources().getString(R.string.weather_citynotfound),
                    // 50)
                    // .show();
                    Log.d(TAG, garden.getLocality() + " : " + getResources().getString(R.string.weather_citynotfound));
                    isWeatherError = true;
                    break;
                }

            }

        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e(TAG, e.getMessage());
            Toast.makeText(this, "Try another nearest city", 50).show();
        }

    }

    private void updateCondition(WeatherConditionInterface condition, int day) {
        WeatherDBHelper helper = new WeatherDBHelper(this);

        Calendar conditionDate = Calendar.getInstance();
        conditionDate.add(Calendar.DAY_OF_YEAR, day);

        condition.setDate(conditionDate.getTime());
        condition.setDayofYear(conditionDate.get(Calendar.DAY_OF_YEAR));

        WeatherConditionInterface wc = helper.getWeatherByDayofyear(conditionDate.get(Calendar.DAY_OF_YEAR));

        if (wc == null)
            helper.insertWeather(condition);
        else
            helper.updateWeather(condition);
        return;

    }
}
