package org.gots.action.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.seed.service.GotsService;
import org.gots.ui.ActionActivity;
import org.gots.ui.MainActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ActionNotificationService extends GotsService {
    private static final int NOTIFICATION = 100;

    // NotificationManager mNM;
    private static final String TAG = "ActionNotificationService";
    private ArrayList<ActionOnSeed> actions = new ArrayList<ActionOnSeed>();
    private GotsGrowingSeedManager growingSeedManager;

    private GotsActionSeedProvider actionseedManager;

    @Override
    public void onCreate() {
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(ActionNotificationService.this);
        actionseedManager = GotsActionSeedManager.getInstance().initIfNew(ActionNotificationService.this);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Starting service : checking actions to do");
        // Display a notification about us starting. We put an icon in the
        // status bar.
        actions.clear();

        new AsyncTask<Void, Void, Void>() {

            boolean shouldcontinue = true;
            private Thread thread;

            protected void onPreExecute() {
                thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (shouldcontinue) {
                                sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
                                sleep(1000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
            }

            ;

            @Override
            protected Void doInBackground(Void... params) {
                ArrayList<GrowingSeed> allSeeds = growingSeedManager.getGrowingSeeds();
                // if (allSeeds.size() > 0)

                for (Iterator<GrowingSeed> iterator = allSeeds.iterator(); iterator.hasNext(); ) {
                    GrowingSeed seed = iterator.next();
                    List<ActionOnSeed> seedActions;

                    seedActions = actionseedManager.getActionsToDoBySeed(seed, false);
                    actions.addAll(seedActions);
                }
                if (!actions.isEmpty()) {
                    ActionOnSeed action = actions.iterator().next();

                    GrowingSeed seed = growingSeedManager.getGrowingSeedById(action.getGrowingSeedId());
                    if (seed != null)
                        createNotification(action, seed.getPlant());

                }

                return null;
            }

            protected void onPostExecute(Void result) {
                shouldcontinue = false;
                sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

            }

            ;
        }.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // mNM.cancel(NOTIFICATION);
        Log.d(TAG, "Stopping service : " + actions.size() + " actions found");
        super.onDestroy();

    }

    private final void createNotification(BaseAction action, BaseSeed seed) {
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
                SeedUtil.getSeedDrawable(this, seed)).setContentTitle(title).setContentText(content);

        // The PendingIntent to launch our activity if the user selects this
        // notification
        // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActionActivity.class), 0);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.setAction(MainActivity.LAUNCHER_ACTION);

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
        mNotificationManager.notify(NOTIFICATION, mBuilder.build());

    }
}
