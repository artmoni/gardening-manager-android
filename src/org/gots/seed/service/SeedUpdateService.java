package org.gots.seed.service;

import java.util.ArrayList;
import java.util.Iterator;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.sql.VendorSeedDBHelper;
import org.gots.seed.providers.nuxeo.NuxeoSeedProvider;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.HutActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SeedUpdateService extends Service {
	private static final int NOTIFICATION = 100;

	NotificationManager mNM;
	private ArrayList<BaseSeedInterface> newSeeds = new ArrayList<BaseSeedInterface>();

	private String TAG = "ActionNotificationService";

	private GotsSeedProvider mRemoteProvider;

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mRemoteProvider = new NuxeoSeedProvider(getApplicationContext());
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// List<BaseSeedInterface> newSeeds = new
		// ArrayList<BaseSeedInterface>();
		Log.d(TAG, "Starting service : checking seeds from web services");

		VendorSeedDBHelper helper = new VendorSeedDBHelper(this);

		for (Iterator<BaseSeedInterface> iterator = mRemoteProvider.getAllSeeds().iterator(); iterator.hasNext();) {
			BaseSeedInterface baseSeedInterface = iterator.next();
			if (helper.getSeedByReference(baseSeedInterface.getReference()) != null) {
				helper.updateSeed(baseSeedInterface);
			} else {
				newSeeds.add(baseSeedInterface);
				helper.insertSeed(baseSeedInterface);
			}
		}
		if (newSeeds.size() > 0) {

			createNotification();

		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		mNM.cancel(NOTIFICATION);
		Log.d(TAG, "Stopping service : " + newSeeds.size() + " actions found");
		super.onDestroy();

	}

	private final void createNotification() {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		String content = "";
		String title = getText(R.string.notification_seed_title).toString();
		// CharSequence content = SeedUtil.translateAction(this, action) + ":" +
		// SeedUtil.translateSpecie(this, seed);

		CharSequence specieName = SeedUtil.translateSpecie(this, newSeeds.get(0));
		title = title.replace("_SPECIE_", specieName);

		if (newSeeds.size() > 1) {
			content = getText(R.string.notification_seed_content).toString();
			content = content.replace("_NBSEEDS_", Integer.toString(newSeeds.size() - 1));
		}

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(SeedWidget.getSeedDrawable(this, newSeeds.get(0)), title,
				System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, HutActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, title, content, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// Send the notification.
		mNM.notify(NOTIFICATION, notification);

	}
}
