package org.gots.action.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.bean.SowingAction;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.provider.local.sql.GrowingSeedDBHelper;
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.ActionActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ActionNotificationService extends Service {
	private static final int NOTIFICATION = 100;

	NotificationManager mNM;
	private ArrayList<BaseActionInterface> actions = new ArrayList<BaseActionInterface>();

	private static final String TAG = "ActionNotificationService";

	@Override
	public IBinder onBind(Intent arg0) {

		return null;
	}

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.d(TAG, "Starting service : checking actions to do");
		// Display a notification about us starting. We put an icon in the
		// status bar.
		actions.clear();
		GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);
		ArrayList<GrowingSeedInterface> allSeeds = helper.getGrowingSeeds();
		// if (allSeeds.size() > 0)

		for (Iterator<GrowingSeedInterface> iterator = allSeeds.iterator(); iterator.hasNext();) {
			GrowingSeedInterface seed = iterator.next();
			ActionSeedDBHelper actionseedHelper = new ActionSeedDBHelper(this);
			ArrayList<BaseActionInterface> seedActions;

			seedActions = actionseedHelper.getActionsToDoBySeed(seed);

			actions.addAll(seedActions);
		}
		// GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);

		if (!actions.isEmpty()) {
			BaseActionInterface action = actions.iterator().next();

			GrowingSeedInterface seed = helper.getSeedById(action.getGrowingSeedId());
			createNotification(action, seed);

		}

		// ##########

		VendorSeedDBHelper helperVendor = new VendorSeedDBHelper(this);
		ArrayList<BaseSeedInterface> allMySeeds = helperVendor.getMySeeds();
		ArrayList<BaseActionInterface> sowingActions = new ArrayList<BaseActionInterface>();
		BaseSeedInterface sowingseed = new GrowingSeed();
		for (Iterator<BaseSeedInterface> iterator = allMySeeds.iterator(); iterator.hasNext();) {
			BaseSeedInterface seed = iterator.next();
			// ActionSeedDBHelper actionseedHelper = new
			// ActionSeedDBHelper(this);
			// ArrayList<BaseActionInterface> seedActions;
			//
			// seedActions = actionseedHelper.getActionsToDoBySeed(seed);
			Calendar cal = Calendar.getInstance();
			if (cal.get(Calendar.MONTH) >= seed.getDateSowingMin()   &&  cal.get(Calendar.MONTH)<=seed.getDateSowingMax()) {
				BaseActionInterface action = new SowingAction(this);
				sowingActions.add(action);
				sowingseed = seed;
			}

			// actions.addAll(seedActions);
		}
		// GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);

		if (!sowingActions.isEmpty()) {
			BaseActionInterface action = sowingActions.iterator().next();

			// GrowingSeedInterface seed =
			// helper.getSeedById(action.getGrowingSeedId());
			createNotification(action, sowingseed);

		}

		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		mNM.cancel(NOTIFICATION);
		Log.d(TAG, "Stopping service : " + actions.size() + " actions found");
		super.onDestroy();

	}

	private final void createNotification(BaseActionInterface action, BaseSeedInterface seed) {
		// In this sample, we'll use the same text for the ticker and the
		// expanded notification
		String content = "";
		String title = getText(R.string.notification_action_title).toString();
		// CharSequence content = SeedUtil.translateAction(this, action) + ":" +
		// SeedUtil.translateSpecie(this, seed);

		CharSequence specieName = SeedUtil.translateSpecie(this, seed);
		title = title.replace("_SPECIE_", specieName);

		if (actions.size() > 1) {
			content = getText(R.string.notification_action_content).toString();
			content = content.replace("_NBACTIONS_", Integer.toString(actions.size() - 1));
		}

		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(SeedWidget.getSeedDrawable(this, seed), title,
				System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActionActivity.class), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, title, content, contentIntent);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		// Send the notification.
		mNM.notify(NOTIFICATION, notification);

	}
}
