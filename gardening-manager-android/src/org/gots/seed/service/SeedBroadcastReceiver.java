package org.gots.seed.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class SeedBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(this.getClass().getName(), "Recurring alarm; requesting UpdateSeed service.");
		// start the download
		Intent startServiceIntent = new Intent(context, SeedUpdateService.class);
		context.startService(startServiceIntent);
	}

}
