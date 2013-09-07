package org.gots.seed.service;

import java.util.List;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.HutActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class SeedNotification {
    private Context mContext;

    private NotificationManager notificationManager;

    private static final int NOTIFICATION = 101;

    public SeedNotification(Context context) {
        this.mContext = context;

        notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public final void createNotification(List<BaseSeedInterface> newSeeds) {
        String content = "";
        CharSequence specieName = SeedUtil.translateSpecie(mContext, newSeeds.get(0));
        String title = mContext.getText(R.string.notification_seed_title).toString();
        title = title.replace("_SPECIE_", specieName);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, new Intent(mContext, HutActivity.class), 0);

        if (newSeeds.size() > 1) {
            content = mContext.getText(R.string.notification_seed_content).toString();
            content = content.replace("_NBSEEDS_", Integer.toString(newSeeds.size() - 1));
        }

        // Set the icon, scrolling text and timestamp
        // BEFORE API 11
        Notification notification = new Notification(SeedWidget.getSeedDrawable(mContext, newSeeds.get(0)), title,
                System.currentTimeMillis());
        notification.setLatestEventInfo(mContext, title, content, contentIntent);

        // API 11 AND MORE
        // Notification notification = new
        // Notification.Builder(mContext).setContentTitle(title).setContentText(content).setSmallIcon(
        // SeedWidget.getSeedDrawable(mContext, newSeeds.get(0))).getNotification();
        // .setLargeIcon(aBitmap)

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(NOTIFICATION, notification);

    }
}
