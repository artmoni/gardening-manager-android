package org.gots.ui.fragment;

import org.gots.analytics.GotsAnalytics;
import org.gots.preferences.GotsPreferences;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AbstractFragmentActivity extends SherlockFragmentActivity {
    protected GotsPreferences gotsPref;

    @Override
    protected void onCreate(Bundle arg0) {
        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());
        gotsPref=GotsPreferences.getInstance();
        gotsPref.initIfNew(this);
        super.onCreate(arg0);
    }

    @Override
    protected void onDestroy() {
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
        super.onDestroy();
    }
}
