package org.gots.ui;

import org.gots.R;
import org.gots.analytics.GotsAnalytics;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AboutActivity extends SherlockActivity {
    private AboutActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        mContext = this;
        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            finish();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_about, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
        super.onDestroy();
    }
}
