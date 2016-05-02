package org.gots.sensor.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.gots.R;
import org.gots.ui.ActionActivity;
import org.gots.ui.CoreActivity;

public class SensorStatusNotification {
    private static final int NOTIFICATION = 102;
    NotificationCompat.InboxStyle inboxStyle;
    private Context mContext;
    private int numMessages;
    private Notification summaryNotification;
    private NotificationCompat.Builder mBuilder;

    public SensorStatusNotification(Context context) {
        mContext = context;
        numMessages = 0;
        mBuilder = new NotificationCompat.Builder(mContext).setSmallIcon(R.drawable.ic_sensor).setContentTitle(
                mContext.getResources().getString(R.string.dashboard_sensor_name)).setContentText("Alert");
        Intent resultIntent = new Intent(mContext, CoreActivity.class);
        resultIntent.setAction(CoreActivity.LAUNCHER_ACTION);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(ActionActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        inboxStyle = new NotificationCompat.InboxStyle(mBuilder);
    }

    public final void createNotification(String notificationDescription) {
        mBuilder.setNumber(++numMessages);
        inboxStyle.addLine(notificationDescription);
        inboxStyle.setBigContentTitle(numMessages + " new alert");
        mBuilder.setContentText(numMessages + " new alert");
        // .setSummaryText("johndoe@gmail.com")

    }

    public void show() {
        NotificationManager mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(NOTIFICATION, inboxStyle.build());
    }
}
