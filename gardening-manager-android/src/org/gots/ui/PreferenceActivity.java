package org.gots.ui;

import org.gots.R;
import org.gots.preferences.GotsPreferences;
import org.gots.provider.DummySeedProvider;
import org.gots.provider.GardenContentProvider;
import org.nuxeo.android.config.NuxeoServerConfig;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;

public class PreferenceActivity extends android.preference.PreferenceActivity implements
        OnSharedPreferenceChangeListener {

    GotsPreferences gotsPreferences;

    public static final long HOUR_PER_DAY = 24L;

    public static final long MINUTE_PER_HOUR = 60L;

    public static final long SECONDS_PER_MINUTE = 60L;

    public static final long SYNC_INTERVAL = SECONDS_PER_MINUTE * MINUTE_PER_HOUR * HOUR_PER_DAY;

    private Account mAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        gotsPreferences = GotsPreferences.getInstance().initIfNew(getApplicationContext());

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

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        displayPreference(key, gotsPreferences.get(key, null));
        if (GotsPreferences.SYNC_SCHEDULE.equals(key)) {
            ContentResolver.setSyncAutomatically(mAccount, DummySeedProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, DummySeedProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

            ContentResolver.setSyncAutomatically(mAccount, GardenContentProvider.AUTHORITY, true);
            ContentResolver.addPeriodicSync(mAccount, GardenContentProvider.AUTHORITY, new Bundle(),
                    gotsPreferences.getScheduleTimeForNotification() * SYNC_INTERVAL);

        }
    }

}
