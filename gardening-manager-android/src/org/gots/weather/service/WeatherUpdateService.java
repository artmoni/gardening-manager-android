package org.gots.weather.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.gots.broadcast.BroadCastMessages;
import org.gots.weather.WeatherConditionInterface;

public class WeatherUpdateService extends Service {
    private static final String TAG = "WeatherUpdateService";
    private final Handler handler = new Handler();
    private Intent intent;
    private boolean isWeatherError = false;
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            displayWeatherAvailable();
            // handler.postDelayed(this, 5000); // 5 seconds
        }
    };

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

        // TODO change to async task and update UI in postExecute
        getWeatherFromWebService();

        return super.onStartCommand(intent, flags, startId);
    }

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

    public void getWeatherFromWebService() {

        try {

            new AsyncTask<Void, Integer, WeatherConditionInterface>() {
                protected void onPreExecute() {
                    sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
                }

                ;

                @Override
                protected WeatherConditionInterface doInBackground(Void... params) {
//                    for (int forecastDay = 0; forecastDay < 4; forecastDay++) {
//                        WeatherProvider previmeteoWeatherProvider = new PrevimeteoWeatherProvider(
//                                getApplicationContext());
////                        previmeteoWeatherProvider.fetchWeatherForecast(null);
//                    }
                    return null;

                }

                @Override
                protected void onPostExecute(WeatherConditionInterface weatherCondition) {
                    handler.removeCallbacks(sendUpdatesToUI);
                    handler.postDelayed(sendUpdatesToUI, 0); // 1 second=1000
                    sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
                    super.onPostExecute(weatherCondition);

                }
            }.execute();

        } catch (Exception e) {
            if (e.getMessage() != null)
                Log.e(TAG, e.getMessage());
            Toast.makeText(this, "Try another nearest city", Toast.LENGTH_SHORT).show();
        }

    }
}
