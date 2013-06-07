package org.gots.seed.service;

import java.util.ArrayList;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.nuxeo.NuxeoSeedProvider;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.HutActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class SeedUpdateService extends Service {
    public static final String BROADCAST_ACTION = "org.gots.broadcastseed.displaylist";

    public static final String ISNEWSEED = "org.gots.isnewseed";

    private static final int NOTIFICATION = 101;

    private static Intent intent = null;

    private static boolean isNewSeed = false;

    NotificationManager mNM;

    private ArrayList<BaseSeedInterface> newSeeds = new ArrayList<BaseSeedInterface>();

    private String TAG = "SeedNotificationService";

    private GotsSeedProvider mRemoteProvider;

    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent arg0) {

        return null;
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mRemoteProvider = new NuxeoSeedProvider(getApplicationContext());
        intent = new Intent(BROADCAST_ACTION);

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // List<BaseSeedInterface> newSeeds = new
        // ArrayList<BaseSeedInterface>();
        Log.d(TAG, "Starting service : checking seeds from web services");

        // VendorSeedDBHelper helper = new VendorSeedDBHelper(this);
        mRemoteProvider.getVendorSeeds();
        // for (BaseSeedInterface baseSeedInterface : mRemoteProvider.getVendorSeeds()) {
        //
        // if (helper.getSeedByUUID(baseSeedInterface.getUUID()) != null) {
        // helper.updateSeed(baseSeedInterface);
        // Log.d(TAG, "updateSeed :"+baseSeedInterface);
        // }
        // else {
        // newSeeds.add(baseSeedInterface);
        // helper.insertSeed(baseSeedInterface);
        // Log.d(TAG, "insertSeed :"+baseSeedInterface);
        //
        // }
        // }
        // if (newSeeds.size() > 0) {
        //
        // createNotification();
        // isNewSeed = true;
        // }
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second

        return super.onStartCommand(intent, flags, startId);
    }

    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            displaySeedsAvailable();
            // handler.postDelayed(this, 5000); // 5 seconds
            // stopSelf();
        }
    };

    private void displaySeedsAvailable() {
        Log.d(TAG, "entered displaySeedsAvailable");

        intent.putExtra(ISNEWSEED, isNewSeed);
        // intent.putExtra("counter", String.valueOf(++counter));
        sendBroadcast(intent);
        stopSelf();
    }

    @Override
    public void onDestroy() {
        // mNM.cancel(NOTIFICATION);
        Log.d(TAG, "Stopping service : " + newSeeds.size() + " seeds found");
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
