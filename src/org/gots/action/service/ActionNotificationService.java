package org.gots.action.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.bean.SowingAction;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.ActionActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ActionNotificationService extends Service {
    private static final int NOTIFICATION = 100;

//    NotificationManager mNM;

    private ArrayList<BaseActionInterface> actions = new ArrayList<BaseActionInterface>();

    private static final String TAG = "ActionNotificationService";

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
//        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Starting service : checking actions to do");
        // Display a notification about us starting. We put an icon in the
        // status bar.
        actions.clear();
        GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(this);
        ArrayList<GrowingSeedInterface> allSeeds =  growingSeedManager.getGrowingSeeds();
        // if (allSeeds.size() > 0)

        for (Iterator<GrowingSeedInterface> iterator = allSeeds.iterator(); iterator.hasNext();) {
            GrowingSeedInterface seed = iterator.next();
            GotsActionSeedProvider actionseedManager = GotsActionSeedManager.getInstance().initIfNew(this);
            ArrayList<BaseActionInterface> seedActions;

            seedActions = actionseedManager.getActionsToDoBySeed(seed);

            actions.addAll(seedActions);
        }
        // GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);

        if (!actions.isEmpty()) {
            BaseActionInterface action = actions.iterator().next();

            GrowingSeedInterface seed = growingSeedManager.getGrowingSeedById(action.getGrowingSeedId());
            createNotification(action, seed);

        }

        // ##########

        LocalSeedProvider helperVendor = new LocalSeedProvider(getApplicationContext());
        List<BaseSeedInterface> allMySeeds = helperVendor.getMyStock(GardenManager.getInstance().initIfNew(this).getCurrentGarden());
        List<BaseActionInterface> sowingActions = new ArrayList<BaseActionInterface>();
        BaseSeedInterface sowingseed = new GrowingSeed();
        for (Iterator<BaseSeedInterface> iterator = allMySeeds.iterator(); iterator.hasNext();) {
            BaseSeedInterface seed = iterator.next();

            Calendar cal = Calendar.getInstance();
            if (cal.get(Calendar.MONTH) >= seed.getDateSowingMin()
                    && cal.get(Calendar.MONTH) <= seed.getDateSowingMax()) {
                BaseActionInterface action = new SowingAction(this);
                sowingActions.add(action);
                sowingseed = seed;
            }

        }

        if (!sowingActions.isEmpty()) {
            BaseActionInterface action = sowingActions.iterator().next();
            createNotification(action, sowingseed);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
//        mNM.cancel(NOTIFICATION);
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
        // Notification notification = new Notification(SeedWidget.getSeedDrawable(this, seed), title,
        // System.currentTimeMillis());
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(
                SeedWidget.getSeedDrawable(this, seed)).setContentTitle(title).setContentText(content);

        // The PendingIntent to launch our activity if the user selects this
        // notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActionActivity.class), 0);

        Intent resultIntent = new Intent(this, ActionActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ActionActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Set the info for the views that show in the notification panel.
        // notification.setLatestEventInfo(this, title, content, contentIntent);
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Send the notification.
        mNotificationManager.notify(NOTIFICATION, mBuilder.getNotification());

    }
}
