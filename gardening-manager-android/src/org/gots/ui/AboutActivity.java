package org.gots.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import org.gots.R;
import org.gots.analytics.GotsAnalytics;
import org.gots.preferences.GotsPreferences;

import java.util.Locale;

public class AboutActivity extends BaseGotsActivity {
    protected int refreshCounter;
    private String TAG = "AboutActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        new AsyncTask<Void, Integer, String>() {
            private TextView name;

            @Override
            protected void onPreExecute() {
                name = (TextView) findViewById(R.id.textVersion);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                PackageInfo pInfo;
                String version = "";
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;

                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                return version;
            }

            protected void onPostExecute(String version) {
                name.setText("Version " + version);
            }

            ;
        }.execute();

        setButtonClickable(R.id.idSocialGoogle, GotsPreferences.URL_GOOGLEPLUS_GARDENING_MANAGER);
        setButtonClickable(R.id.idSocialFacebook, GotsPreferences.URL_FACEBOOK_GARDENING_MANAGER);
        setButtonClickable(R.id.idSocialTwitter, GotsPreferences.URL_TWITTER_GARDENING_MANAGER);
        setButtonClickable(R.id.idTranslateButton, GotsPreferences.URL_TRANSLATE_GARDENING_MANAGER);

        ImageView flag = (ImageView) findViewById(R.id.imageTranslateFlag);
        int flagRessource = getResources().getIdentifier(
                "org.gots:drawable/" + Locale.getDefault().getCountry().toLowerCase(), null, null);
        flag.setImageResource(flagRessource);

    }


    protected void setButtonClickable(int viewId, final String url) {
        View button = (View) findViewById(viewId);
        if (button != null)
            button.setOnClickListener(new LinearLayout.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
                    GoogleAnalyticsTracker.getInstance().trackPageView(url);
                }
            });
    }

    protected void addProgress() {
        refreshCounter++;
    }

    ;

    protected void removeProgress() {
        refreshCounter--;
    }


    @Override
    protected boolean requireFloatingButton() {
        return false;
    }
}
