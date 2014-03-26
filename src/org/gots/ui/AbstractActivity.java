/* *********************************************************************** *
 * project: org.gots.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : contact at gardening-manager dot com                  *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   http://www.gnu.org/licenses/gpl-2.0.html                              *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *   Contributors:                                                         *
 *                - jcarsique                                                *
 *                                                                         *
 * *********************************************************************** */
package org.gots.ui;

import java.util.ArrayList;

import org.gots.allotment.AllotmentManager;
import org.gots.analytics.GotsAnalytics;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsSeedManager;

import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author jcarsique
 * 
 */
public class AbstractActivity extends SherlockFragmentActivity {
    // private static final String TAG = AbstractActivity.class.getSimpleName();

    protected GotsPreferences gotsPrefs;

    protected GardenManager gardenManager;

    protected NuxeoManager nuxeoManager;

    protected GotsSeedManager seedManager;

    protected AllotmentManager allotmentManager;

    private static ArrayList<AbstractActivity> activities = new ArrayList<AbstractActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO All this should be part of the application/service/...
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(this);
        nuxeoManager = NuxeoManager.getInstance();
        nuxeoManager.initIfNew(this);
        gardenManager = GardenManager.getInstance();
        gardenManager.initIfNew(this);
        seedManager = GotsSeedManager.getInstance();
        seedManager.initIfNew(this);
        allotmentManager = AllotmentManager.getInstance();
        allotmentManager.initIfNew(this);
        activities.add(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(seedManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(seedManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

        try {
            GoogleAnalyticsTracker.getInstance().setCustomVar(1, "App Version",
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 1);
        } catch (NameNotFoundException e) {
        }

        if (gotsPrefs.isPremium()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Premium", 1);
        } else
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Guest", 1);

        if (gotsPrefs.isConnectedToServer()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Connected", "Connected", 1);
        } else
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Connected", "Guest", 1);

        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
        unregisterReceiver(gardenManager);
        unregisterReceiver(allotmentManager);
        unregisterReceiver(seedManager);
        if (activities.size() == 0) {
            nuxeoManager.shutdown();
            gardenManager.finalize();
            seedManager.finalize();
            allotmentManager.finalize();

        }
        GoogleAnalyticsTracker.getInstance().dispatch();
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
    }
}
