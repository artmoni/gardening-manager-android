package org.gots.inapp;

import org.gots.R;
import org.gots.preferences.GotsPreferences;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AppRater {
    private final static String APP_TITLE = "Gardening Manager";

    private final static String APP_PNAME = "org.gots";

    private final static int DAYS_UNTIL_PROMPT = 3;

    private final static int LAUNCHES_UNTIL_PROMPT = 10;

    private static int launch_count;

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = mContext.getSharedPreferences("apprater", 0);
        if (prefs.getBoolean("dontshowagain", false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        launch_count = prefs.getInt("app_launch_count", 0) + 1;
        editor.putInt("app_launch_count", launch_count);

        // Get date of first launch
        Long date_firstLaunch = prefs.getLong("date_firstlaunch", 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong("date_firstlaunch", date_firstLaunch);
        }

        // Wait at least n days before opening
        if (launch_count > 1 && launch_count % LAUNCHES_UNTIL_PROMPT == 0) {
            if (System.currentTimeMillis() <= date_firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                showRateDialog(mContext, editor);
            }
        }

        editor.commit();
    }

    public static void showRateDialog(final Context mContext, final SharedPreferences.Editor editor) {
        final Dialog dialog = new Dialog(mContext);
//        final Dialog dialog = new Dialog(mContext, R.style.CustomDialog);
        dialog.setTitle(mContext.getResources().getString(R.string.inapp_rating_title) + " " + APP_TITLE);
        
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(mContext.getResources().getColor(R.color.white));

        TextView tv = new TextView(mContext);

        GotsPreferences gotsPrefs = GotsPreferences.getInstance().initIfNew(mContext);
        tv.setText(mContext.getResources().getString(R.string.inapp_rating_descrition).replace("_APP_TITLE_",
                gotsPrefs.getGardeningManagerAppname()));
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);

        Button b1 = new Button(mContext);
        b1.setText(mContext.getResources().getString(R.string.inapp_rating_title) + " " + APP_TITLE);
        b1.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
                dialog.dismiss();
                GoogleAnalyticsTracker.getInstance().trackEvent("Button", "AppRater", "rate",launch_count);;        

            }
        });
        ll.addView(b1);

        Button b2 = new Button(mContext);
        Button b3 = new Button(mContext);
        b3.setText(mContext.getResources().getString(R.string.inapp_rating_cancel));
        b3.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (editor != null) {
                    
                    editor.putBoolean("dontshowagain", true);
                    editor.commit();
                    GoogleAnalyticsTracker.getInstance().trackEvent("Button", "AppRater", "dontshowagain", launch_count);;        

                }
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        b2.setText(mContext.getResources().getString(R.string.inapp_rating_later));
        b2.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                GoogleAnalyticsTracker.getInstance().trackEvent("Button", "AppRater", "showlater", launch_count);;        

            }
        });
        ll.addView(b2);

        dialog.setContentView(ll);
        dialog.show();
    }
}
// see http://androidsnippets.com/prompt-engaged-users-to-rate-your-app-in-the-android-market-appirater
