/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.lang.ref.WeakReference;
import java.util.Calendar;

import org.gots.R;
import org.gots.action.service.ActionNotificationService;
import org.gots.action.service.ActionTODOBroadcastReceiver;
import org.gots.analytics.GotsAnalytics;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.service.SeedUpdateService;
import org.gots.weather.service.WeatherUpdateService;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class SplashScreenActivity extends AbstractActivity {
    private static final class SplashHandler extends Handler {

        private WeakReference<Activity> that;

        public SplashHandler(WeakReference<Activity> that) {
            this.that = that;
        }

        @Override
        public void handleMessage(Message msg) {
            Intent startServiceIntent = new Intent(that.get(), WeatherUpdateService.class);
            that.get().startService(startServiceIntent);

            Intent startServiceIntent2 = new Intent(that.get(), SeedUpdateService.class);
            that.get().startService(startServiceIntent2);

            Intent startServiceIntent3 = new Intent(that.get(), ActionNotificationService.class);
            that.get().startService(startServiceIntent3);

            switch (msg.what) {
            case STOPSPLASH:
                // remove SplashScreen from view
                Intent intent = new Intent(that.get(), DashboardActivity.class);
                that.get().startActivity(intent);
                that.get().finish();

                break;
            }
            super.handleMessage(msg);
        }
    }

    private static final int STOPSPLASH = 0;

    // private static final long SPLASHTIME = 3000;
    private static final long SPLASHTIME = 2000;

    private static Handler splashHandler;

    private Handler getSplashHandler() {
        if (splashHandler == null) {
            WeakReference<Activity> that = new WeakReference<Activity>(this);
            splashHandler = new SplashHandler(that);
        }
        return splashHandler;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

        Message msg = new Message();
        msg.what = STOPSPLASH;

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView name = (TextView) findViewById(R.id.textVersion);
            name.setText("version " + version);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        LinearLayout artmoni = (LinearLayout) findViewById(R.id.webArtmoni);
        artmoni.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.artmoni.eu"));
                startActivity(browserIntent);

            }
        });

        LinearLayout sauterdanslesflaques = (LinearLayout) findViewById(R.id.webSauterDansLesFlaques);
        sauterdanslesflaques.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.sauterdanslesflaques.com"));
                startActivity(browserIntent);

            }
        });

        ImageView socialGoogle = (ImageView) findViewById(R.id.idSocialGoogle);
        socialGoogle.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://plus.google.com/u/0/b/108868805153744305734/communities/105269291264998461912"));
                startActivity(browserIntent);

            }
        });

        ImageView socialFacebook = (ImageView) findViewById(R.id.idSocialFacebook);
        socialFacebook.setOnClickListener(new LinearLayout.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.facebook.com/pages/Gardening-Manager/120589404779871"));
                startActivity(browserIntent);

            }
        });

        // ImageView previmeteo = (ImageView) findViewById(R.id.idPrevimeteo);
        // previmeteo.setOnClickListener(new LinearLayout.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
        // Uri.parse("http://www.previmeteo.com/"));
        // startActivity(browserIntent);
        //
        // }
        // });
        // Intent startServiceIntent = new Intent(this,
        // NotificationService.class);
        // this.startService(startServiceIntent);
        setRecurringAlarm(this);

        int currentGardenId = gotsPrefs.getCurrentGardenId();
        if (currentGardenId == -1) {
            Intent intent = new Intent(this, FirstLaunchActivity.class);
            startActivityForResult(intent, 0);

        } else {
            if (GotsPreferences.isDevelopment())
                getSplashHandler().sendMessageDelayed(msg, 0);
            else
                getSplashHandler().sendMessageDelayed(msg, SPLASHTIME);
        }

    }

    @Override
    protected void onResume() {
        // Message msg = new Message();
        // msg.what = STOPSPLASH;
        // splashHandler.sendMessageDelayed(msg, SPLASHTIME);

        // Intent intent = new Intent(SplashScreenActivity.this,
        // DashboardActivity.class);
        // startActivity(intent);

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        int currentGardenId = gotsPrefs.getCurrentGardenId();
        if (currentGardenId > -1 || gotsPrefs.isConnectedToServer()) {
            Message msg = new Message();
            msg.what = STOPSPLASH;
            getSplashHandler().sendMessageDelayed(msg, SPLASHTIME);
        } else
            finish();

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
        super.onDestroy();
    }

    private void setRecurringAlarm(Context context) {
        // we know mobiletuts updates at right around 1130 GMT.
        // let's grab new stuff at around 11:45 GMT, inexactly
        Calendar updateTime = Calendar.getInstance();
        // updateTime.setTimeInMillis(System.currentTimeMillis());
        // updateTime.add(Calendar.SECOND, 10);
        // updateTime.setTimeZone(TimeZone.getTimeZone("GMT"));
        // updateTime.set(Calendar.HOUR_OF_DAY, 12);
        // updateTime.set(Calendar.MINUTE, 15);
        Intent downloader = new Intent(context, ActionTODOBroadcastReceiver.class);
        PendingIntent actionTODOIntent = PendingIntent.getBroadcast(context, 0, downloader,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (GotsPreferences.isDevelopment())
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, actionTODOIntent);
        else {
            updateTime.set(Calendar.HOUR_OF_DAY, 20);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, actionTODOIntent);
        }
    }

}
