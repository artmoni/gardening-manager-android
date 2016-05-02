package org.gots.action.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ActionTODOBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(this.getClass().getName(), "Recurring alarm; requesting download service.");
        // start the download
        Intent startServiceIntent = new Intent(context, ActionNotificationService.class);
        context.startService(startServiceIntent);
    }

}
