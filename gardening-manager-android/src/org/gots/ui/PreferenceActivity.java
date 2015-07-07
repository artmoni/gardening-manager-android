package org.gots.ui;

import org.gots.R;
import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;
import org.gots.provider.ActionsContentProvider;
import org.gots.provider.AllotmentContentProvider;
import org.gots.provider.GardenContentProvider;
import org.gots.provider.SeedsContentProvider;
import org.gots.provider.SensorContentProvider;
import org.nuxeo.android.config.NuxeoServerConfig;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.util.Log;

public class PreferenceActivity extends android.preference.PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    GotsPreferences gotsPreferences;

    public static final long HOUR_PER_DAY = 24L;

    public static final long MINUTE_PER_HOUR = 60L;

    public static final long SECONDS_PER_MINUTE = 60L;

    public static final long SYNC_INTERVAL = SECONDS_PER_MINUTE * MINUTE_PER_HOUR * HOUR_PER_DAY;

    private static final String TAG = "PreferenceActivity";

    private Account mAccount;

    protected GotsContext getGotsContext() {
        return GotsContext.get(getApplicationContext());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        gotsPreferences = getGotsContext().getServerConfig();

        displayPreference(NuxeoServerConfig.PREF_SERVER_LOGIN, gotsPreferences.getNuxeoLogin());
        displayPreference(NuxeoServerConfig.PREF_SERVER_URL, gotsPreferences.getGardeningManagerServerURI());
        displayPreference(NuxeoServerConfig.PREF_SERVER_TOKEN, gotsPreferences.getToken());
        displayPreference(GotsPreferences.SYNC_SCHEDULE,
                String.valueOf(gotsPreferences.getScheduleTimeForNotification()));
        // displayPreference(NuxeoServerConfig.PREF_SERVER_LOGIN, gotsPreferences.getNuxeoLogin());

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        AccountManager accountManager = AccountManager.get(getApplicationContext());
        Account[] account = accountManager.getAccountsByType("gardening-manager");
        if (account.length > 0)
            mAccount = account[0];

    }

    private void displayPreference(String key, String value) {
        Preference pref = findPreference(key);
        if (pref != null && value != null)
            pref.setSummary(value);
        if (NuxeoServerConfig.PREF_SERVER_LOGIN.equals(key)) {
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
//                    LoginDialogFragment login = new LoginDialogFragment();
//                    login.show(getFragmentManager(), TAG);
                    return true;
                }
            });
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        try {
            displayPreference(key, gotsPreferences.get(key, null));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        if (GotsPreferences.SYNC_SCHEDULE.equals(key)) {
            ContentResolver.setSyncAutomatically(mAccount, SeedsContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, SeedsContentProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(mAccount, GardenContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, GardenContentProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(mAccount, ActionsContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, ActionsContentProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(mAccount, AllotmentContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, AllotmentContentProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(mAccount, SensorContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, SensorContentProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

        }
    }

}
