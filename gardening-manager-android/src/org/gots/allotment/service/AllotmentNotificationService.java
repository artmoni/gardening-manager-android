package org.gots.allotment.service;

import java.util.ArrayList;
import java.util.List;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.service.GotsService;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

public class AllotmentNotificationService extends GotsService {
    private static final int NOTIFICATION = 100;

    // NotificationManager mNM;

    private List<BaseAllotmentInterface> allotments = new ArrayList<BaseAllotmentInterface>();

    private static final String TAG = "AllotmentNotificationService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.d(TAG, "Starting service : checking allotments");
        // Display a notification about us starting. We put an icon in the
        // status bar.
        allotments.clear();

        new AsyncTask<Void, Void, Void>() {

            private Thread thread;

            boolean shouldcontinue = true;

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
            };

            @Override
            protected Void doInBackground(Void... params) {
                allotments = allotmentManager.getMyAllotments(false);
                // if (allSeeds.size() > 0)

                // for (Iterator<BaseAllotmentInterface> iterator = allSeeds.iterator(); iterator.hasNext();) {
                // BaseAllotmentInterface seed = iterator.next();
                // List<SeedActionInterface> seedActions;
                //
                // seedActions = actionseedManager.getActionsToDoBySeed(seed);
                // allotments.addAll(seedActions);
                // }
                // if (!allotments.isEmpty()) {
                // SeedActionInterface action = allotments.iterator().next();
                //
                // GrowingSeedInterface seed = growingSeedManager.getGrowingSeedById(action.getGrowingSeedId());
                // if (seed != null)
                // createNotification(action, seed);
                //
                // }

                return null;
            }

            protected void onPostExecute(Void result) {
                shouldcontinue = false;
                sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

            };
        }.execute();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // mNM.cancel(NOTIFICATION);
        Log.d(TAG, "Stopping service : " + allotments.size() + " allotments found");
        super.onDestroy();

    }

    // private final void createNotification(BaseActionInterface action, BaseSeed seed) {
    // // In this sample, we'll use the same text for the ticker and the
    // // expanded notification
    // String content = "";
    // String title = getText(R.string.notification_action_title).toString();
    // // CharSequence content = SeedUtil.translateAction(this, action) + ":" +
    // // SeedUtil.translateSpecie(this, seed);
    //
    // CharSequence specieName = SeedUtil.translateSpecie(this, seed);
    // title = title.replace("_SPECIE_", specieName);
    //
    // if (allotments.size() > 1) {
    // content = getText(R.string.notification_action_content).toString();
    // content = content.replace("_NBACTIONS_", Integer.toString(allotments.size() - 1));
    // }
    //
    // // Set the icon, scrolling text and timestamp
    // // Notification notification = new Notification(SeedWidget.getSeedDrawable(this, seed), title,
    // // System.currentTimeMillis());
    // NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this).setSmallIcon(
    // SeedWidget.getSeedDrawable(this, seed)).setContentTitle(title).setContentText(content);
    //
    // // The PendingIntent to launch our activity if the user selects this
    // // notification
    // // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActionActivity.class), 0);
    //
    // Intent resultIntent = new Intent(this, ActionActivity.class);
    //
    // // The stack builder object will contain an artificial back stack for the
    // // started Activity.
    // // This ensures that navigating backward from the Activity leads out of
    // // your application to the Home screen.
    // TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    // // Adds the back stack for the Intent (but not the Intent itself)
    // stackBuilder.addParentStack(ActionActivity.class);
    // // Adds the Intent that starts the Activity to the top of the stack
    // stackBuilder.addNextIntent(resultIntent);
    // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
    // mBuilder.setContentIntent(resultPendingIntent);
    // mBuilder.setAutoCancel(true);
    //
    // NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    // // Set the info for the views that show in the notification panel.
    // // notification.setLatestEventInfo(this, title, content, contentIntent);
    // // notification.flags |= Notification.FLAG_AUTO_CANCEL;
    // // Send the notification.
    // mNotificationManager.notify(NOTIFICATION, mBuilder.build());
    //
    // }
}
