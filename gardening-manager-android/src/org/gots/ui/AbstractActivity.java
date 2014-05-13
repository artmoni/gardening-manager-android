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

import org.gots.R;
import org.gots.allotment.AllotmentManager;
import org.gots.analytics.GotsAnalytics;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsSeedManager;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author jcarsique
 * 
 */
public abstract class AbstractActivity extends ActionBarActivity {
    // private static final String TAG = AbstractActivity.class.getSimpleName();

    protected GotsPreferences gotsPrefs;

    protected GotsPurchaseItem gotsPurchase;

    protected GardenManager gardenManager;

    protected NuxeoManager nuxeoManager;

    protected GotsSeedManager seedManager;

    protected AllotmentManager allotmentManager;

    private View progressView;

    private Menu menu;

    private static ArrayList<AbstractActivity> activities = new ArrayList<AbstractActivity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO All this should be part of the application/service/...
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(this);
        gotsPurchase = new GotsPurchaseItem(this);
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
        registerReceiver(progressReceiver, new IntentFilter(BroadCastMessages.PROGRESS_UPDATE));
        registerReceiver(progressReceiver, new IntentFilter(BroadCastMessages.PROGRESS_FINISHED));

        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

        try {
            GoogleAnalyticsTracker.getInstance().setCustomVar(1, "App Version",
                    getPackageManager().getPackageInfo(getPackageName(), 0).versionName, 1);
        } catch (NameNotFoundException e) {
        }

        if (gotsPurchase.isPremium()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Premium", 1);
        } else
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Guest", 1);

        if (gotsPrefs.isConnectedToServer()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Connected", "Connected", 1);
        } else
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Connected", "Guest", 1);

        super.onPostCreate(savedInstanceState);
    }

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.PROGRESS_UPDATE.equals(intent.getAction()))
                setProgressRefresh(true);
            else if (BroadCastMessages.PROGRESS_FINISHED.equals(intent.getAction()))
                setProgressRefresh(false);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);

        unregisterReceiver(gardenManager);
        unregisterReceiver(allotmentManager);
        unregisterReceiver(seedManager);
        unregisterReceiver(progressReceiver);
        if (activities.size() == 0) {
            nuxeoManager.shutdown();
            gardenManager.finalize();
            seedManager.finalize();
            allotmentManager.finalize();

        }
        GoogleAnalyticsTracker.getInstance().dispatch();
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        this.menu = menu;
        menu.findItem(R.id.refresh_seed).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onRefresh();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

     abstract protected void onRefresh();

    protected void setProgressRefresh(boolean refresh) {
        if (menu == null)
            return;
        MenuItem itemRefresh = menu.findItem(R.id.refresh_seed);
        if (itemRefresh == null)
            return;

        if (refresh) {
            if (progressView == null)
                progressView = (View) getLayoutInflater().inflate(R.layout.actionbar_indeterminate_progress, null);
            if (progressView.getAnimation() == null) {
                Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
                rotation.setRepeatCount(Animation.INFINITE);
                progressView.startAnimation(rotation);
//                progressView.setBackgroundColor(getResources().getColor(R.color.action_warning_color));
            }
//             itemRefresh.setActionView(progressView);
            itemRefresh = MenuItemCompat.setActionView(itemRefresh, progressView);
        } else {
            if (progressView != null) {
                progressView.clearAnimation();
            }
//             itemRefresh.setActionView(null);
            itemRefresh = MenuItemCompat.setActionView(itemRefresh, null);

        }

    }

}
