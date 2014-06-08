package org.gots.service;

import java.util.Calendar;

import org.gots.action.service.ActionTODOBroadcastReceiver;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.service.SeedBroadcastReceiver;
import org.gots.weather.service.WeatherBroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //        setRecurringAlarm(context);
    }

//    private void setRecurringAlarm(Context context) {
//        Calendar updateTime = Calendar.getInstance();
//
//        /*
//         * ActionTODOBroadcastReceiver
//         */
//        Intent actionsBroadcaster = new Intent(context, ActionTODOBroadcastReceiver.class);
//        PendingIntent actionTODOIntent = PendingIntent.getBroadcast(context, 0, actionsBroadcaster,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//
//        if (GotsPreferences.isDevelopment())
//            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
//                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, actionTODOIntent);
//        else {
//            updateTime.set(Calendar.HOUR_OF_DAY, 20);
//            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY * 7, actionTODOIntent);
//        }
//
//        /*
//         * WeatherBroadcastReceiver
//         */
//        Intent weatherBroadcastIntent = new Intent(context, WeatherBroadcastReceiver.class);
//        PendingIntent weatherUpdateService = PendingIntent.getBroadcast(context, 1, weatherBroadcastIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        if (GotsPreferences.isDevelopment())
//            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
//                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, weatherUpdateService);
//        else {
//            updateTime.set(Calendar.HOUR_OF_DAY, 20);
//            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, weatherUpdateService);
//        }
//        
//        /*
//         * SeedBroadcastReceiver
//         */
//        Intent seedBroadcastIntent = new Intent(context, SeedBroadcastReceiver.class);
//        PendingIntent seedUpdateService = PendingIntent.getBroadcast(context, 1, seedBroadcastIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//
//        if (GotsPreferences.isDevelopment())
//            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 60000, seedUpdateService);
//        else {
//            updateTime.set(Calendar.HOUR_OF_DAY, 20);
//            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY, seedUpdateService);
//        }
//    }
}
