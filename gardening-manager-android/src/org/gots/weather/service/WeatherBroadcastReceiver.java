package org.gots.weather.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class WeatherBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getName(), "Recurring alarm; onReceive.");
        // start the download
        Intent startServiceIntent = new Intent(context, WeatherUpdateService.class);
        context.startService(startServiceIntent);
    }

}
