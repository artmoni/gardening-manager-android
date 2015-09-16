package org.gots.analytics;

import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;

import android.app.Application;
import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GotsAnalytics {

    public static final String TRACK_EVENT_RECOGNITION = "Recognition";
    protected static GotsAnalytics INSTANCE;

    protected int activityCount = 0;

    protected Integer dispatchIntervalSecs = 10;

    // protected static String apiKey = GotsPreferences.ANALYTICS_API_KEY;
    protected Context context;

    private GotsPreferences gotsPreferences;

    /**
     * NOTE: you should use your Application context, not your Activity context,
     * in order to avoid memory leaks.
     */
    protected GotsAnalytics(Application context) {
        this.context = context;
    }
    protected GotsContext getGotsContext() {
        return GotsContext.get(context);
    }
    /**
     * NOTE: you should use your Application context, not your Activity context,
     * in order to avoid memory leaks.
     */
    protected GotsAnalytics(int dispatchIntervalSecs, Application context) {
        this.dispatchIntervalSecs = dispatchIntervalSecs;
        this.context = context;

    }

    /**
     * This should be called once in onCreate() for each of your activities that
     * use GoogleAnalytics. These methods are not synchronized and don't
     * generally need to be, so if you want to do anything unusual you should
     * synchronize them yourself.
     */
    public void incrementActivityCount() {
        gotsPreferences = getGotsContext().getServerConfig();

        if (activityCount == 0) {
            if (dispatchIntervalSecs == null) {
                GoogleAnalyticsTracker.getInstance().startNewSession(gotsPreferences.getAnalyticsApiKey(), context);
            } else {
                GoogleAnalyticsTracker.getInstance().startNewSession(gotsPreferences.getAnalyticsApiKey(),
                        dispatchIntervalSecs, context);
            }

            if (GotsPreferences.isDevelopment())
                GoogleAnalyticsTracker.getInstance().setDryRun(true);
        }
        ++activityCount;
    }

    /**
     * This should be called once in onDestrkg() for each of your activities
     * that use GoogleAnalytics. These methods are not synchronized and don't
     * generally need to be, so if you want to do anything unusual you should
     * synchronize them yourself.
     */
    public void decrementActivityCount() {
        activityCount = Math.max(activityCount - 1, 0);

        if (activityCount == 0)
            GoogleAnalyticsTracker.getInstance().stopSession();
    }

    /**
     * Get or create an instance of GoogleAnalyticsSessionManager
     */
    public static GotsAnalytics getInstance(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new GotsAnalytics(application);

        }
        return INSTANCE;
    }

}
