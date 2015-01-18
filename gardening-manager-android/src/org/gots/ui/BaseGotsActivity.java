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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.action.GotsActionSeedManager;
import org.gots.ads.GotsAdvertisement;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.analytics.GotsAnalytics;
import org.gots.bean.DefaultGarden;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.context.GotsContextProvider;
import org.gots.exception.GardenNotFoundException;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.context.NuxeoContext;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

/**
 * @author jcarsique
 * 
 */
public abstract class BaseGotsActivity extends BaseNuxeoActivity implements GotsContextProvider {
    protected static final String TAG = BaseGotsActivity.class.getSimpleName();

    protected GotsPreferences gotsPrefs;

    protected GotsPurchaseItem gotsPurchase;

    protected GotsGardenManager gardenManager;

    protected NuxeoManager nuxeoManager;

    protected GotsSeedManager seedManager;

    protected GotsAllotmentManager allotmentManager;

    protected GotsActionSeedManager actionseedProvider;

    private View progressView;

    private Menu menu;

    private GardenInterface currentGarden;

    private static ArrayList<BaseGotsActivity> activities = new ArrayList<BaseGotsActivity>();

    private GotsGrowingSeedManager gotsGrowingSeedManager;

    public GotsContext getGotsContext() {
        return GotsContext.get(getApplicationContext());
    }

    protected GardenInterface getCurrentGarden() {
        return currentGarden;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO All this should be part of the application/service/...
        gotsPrefs = getGotsContext().getServerConfig();
        gotsPurchase = new GotsPurchaseItem(this);
        nuxeoManager = NuxeoManager.getInstance();
        nuxeoManager.initIfNew(this);
        gardenManager = GotsGardenManager.getInstance();
        gardenManager.initIfNew(this);
        seedManager = GotsSeedManager.getInstance();
        seedManager.initIfNew(this);
        allotmentManager = GotsAllotmentManager.getInstance();
        allotmentManager.initIfNew(this);
        gotsGrowingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(this);
        actionseedProvider = GotsActionSeedManager.getInstance();
        actionseedProvider.initIfNew(this);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        registerReceiver(nuxeoManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(gotsGrowingSeedManager, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(seedManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(seedManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(progressReceiver, new IntentFilter(BroadCastMessages.PROGRESS_UPDATE));
        registerReceiver(progressReceiver, new IntentFilter(BroadCastMessages.PROGRESS_FINISHED));
        activities.add(this);

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

        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            if (layout != null)
                layout.addView(ads.getAdsLayout());
        }

        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        try {
            currentGarden = gardenManager.getCurrentGarden();
        } catch (GardenNotFoundException e) {
            LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_LOW);
            String bestprovider = manager.getBestProvider(criteria, true);
            Location loc = manager.getLastKnownLocation(bestprovider);
            Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = geoCoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                currentGarden = new DefaultGarden(addresses.get(0));
            } catch (IOException e1) {
                currentGarden = new DefaultGarden(new Address(Locale.getDefault()));
                e1.printStackTrace();
            }
        }
        super.onResume();
    }

    private int progressCounter = 0;

    private BroadcastReceiver progressReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.PROGRESS_UPDATE.equals(intent.getAction())) {
                setProgressRefresh(true);
                progressCounter++;
            } else if (BroadCastMessages.PROGRESS_FINISHED.equals(intent.getAction())) {
                if (--progressCounter == 0)
                    setProgressRefresh(false);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activities.remove(this);
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();

        unregisterReceiver(nuxeoManager);
        unregisterReceiver(gardenManager);
        unregisterReceiver(allotmentManager);
        unregisterReceiver(seedManager);
        unregisterReceiver(progressReceiver);
        unregisterReceiver(gotsGrowingSeedManager);
        if (activities.size() == 0) {
            nuxeoManager.shutdown();
            gardenManager.finalize();
            seedManager.finalize();
            allotmentManager.finalize();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case R.id.refresh_seed:
            onRefresh(null);
            Log.d(TAG, getClass().getName());
            break;

        default:
            break;
        }

        return super.onOptionsItemSelected(item);
    }

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
            }
            itemRefresh = MenuItemCompat.setActionView(itemRefresh, progressView);
        } else {
            if (progressView != null) {
                progressView.clearAnimation();
            }
            itemRefresh = MenuItemCompat.setActionView(itemRefresh, null);
        }

    }

    protected void onRefresh(String AUTHORITY) {
        if (AUTHORITY == null || "".equals(AUTHORITY)) {
            Log.d(TAG, "You call onRefresh without Content Resolver Authority");
            return;
        }
        Account userAccount = gotsPrefs.getUserAccount();
        ContentResolver.setSyncAutomatically(userAccount, AUTHORITY, true);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(userAccount, AUTHORITY, bundle);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        setProgressRefresh(true);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        setProgressRefresh(false);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        setProgressRefresh(false);
        super.onNuxeoDataRetrieveFailed();
    }

    @Override
    public NuxeoContext getNuxeoContext() {
        return super.getNuxeoContext();
    }

}
