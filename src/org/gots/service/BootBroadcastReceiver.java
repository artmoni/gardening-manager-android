package org.gots.service;

import org.gots.action.service.ActionNotificationService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, ActionNotificationService.class);
        context.startService(startServiceIntent);
	}

}
