package org.gots.seed.service;

import java.util.List;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.SeedUtil;
import org.gots.ui.ActionActivity;
import org.gots.ui.CoreActivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class SeedNotification {
    private Context mContext;

//    private NotificationManager notificationManager;

    private static final int NOTIFICATION = 101;

    public SeedNotification(Context context) {
        this.mContext = context;

//        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public final void createNotification(List<BaseSeed> newSeeds) {
        String content = "";
        CharSequence specieName = SeedUtil.translateSpecie(mContext, newSeeds.get(0));
        String title = mContext.getText(R.string.notification_seed_title).toString();
        title = title.replace("_SPECIE_", specieName);

//        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, HutActivity.class), 0);

        if (newSeeds.size() > 1) {
            content = mContext.getText(R.string.notification_seed_content).toString();
            content = content.replace("_NBSEEDS_", Integer.toString(newSeeds.size() - 1));
        }

        // Set the icon, scrolling text and timestamp
        // BEFORE API 11
//        Notification notification = new Notification(SeedWidget.getSeedDrawable(mContext, newSeeds.get(0)), title,
//                System.currentTimeMillis());
//        notification.setLatestEventInfo(mContext, title, content, contentIntent);

        // API 11 AND MORE
        // Notification notification = new
        // Notification.Builder(mContext).setContentTitle(title).setContentText(content).setSmallIcon(
        // SeedWidget.getSeedDrawable(mContext, newSeeds.get(0))).getNotification();
        // .setLargeIcon(aBitmap)

//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notificationManager.notify(NOTIFICATION, notification);
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(
                SeedUtil.getSeedDrawable(mContext, newSeeds.get(0))).setContentTitle(title).setContentText(content);

        // The PendingIntent to launch our activity if the user selects this
        // notification
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, ActionActivity.class), 0);

        Intent resultIntent = new Intent(mContext, CoreActivity.class);
        resultIntent.setAction(CoreActivity.LAUNCHER_CATALOGUE);
        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(ActionActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        // Set the info for the views that show in the notification panel.
        // notification.setLatestEventInfo(this, title, content, contentIntent);
        // notification.flags |= Notification.FLAG_AUTO_CANCEL;
        // Send the notification.
        mNotificationManager.notify(NOTIFICATION, mBuilder.build());
    }
}
