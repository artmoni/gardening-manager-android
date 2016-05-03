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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.util.Purchase;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

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
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.inapp.HolderSku;
import org.gots.inapp.OnPurchaseFinished;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.ui.fragment.BaseGotsFragment;
import org.gots.ui.fragment.LoginFragment;
import org.nuxeo.android.context.NuxeoContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author jcarsique
 */
public abstract class BaseGotsActivity extends GotsCoreActivity implements GotsContextProvider,
        OnBackStackChangedListener, LoginFragment.OnLoginEventListener {
    protected static final String TAG = BaseGotsActivity.class.getSimpleName();
    protected static final int LENGHT_SHORT = 3000;
    protected static final int LENGHT_LONG = 5000;
    private static ArrayList<BaseGotsActivity> activities = new ArrayList<BaseGotsActivity>();

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

    private GotsGrowingSeedManager gotsGrowingSeedManager;
    private View bottomRightButton;
    private TextView notificationText;
    private ImageView imageView;
    private CoordinatorLayout layoutNotification;
    private FloatingActionsMenu floatingActionsMenu;
    private GotsBillingDialog gotsBillingDialog;
    private BroadcastReceiver gardenBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())
                    || BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                try {
                    // closeAllManager();
                    // initAllManager();
                    currentGarden = gardenManager.getCurrentGarden();
                    Log.d(TAG, "Current garden is now " + currentGarden);
                    if (context instanceof GardenListener) {
                        ((GardenListener) context).onCurrentGardenChanged(currentGarden);
                        Log.d(TAG, "Send event onCurrentGardenChanged for activities");

                    }
                } catch (GardenNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };
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
    public void onAuthenticationSucceed(Account account) {
        showNotification(account.name + ", " + getResources().getString(R.string.login_connection_succeed), false);
    }

    @Override
    public void onAuthenticationFailed(String string) {
        showNotification(string, false);

    }

    public GotsContext getGotsContext() {
        return GotsContext.get(getApplicationContext());
    }

    protected synchronized GardenInterface getCurrentGarden() {
        if (currentGarden == null)
            try {
                currentGarden = gardenManager.getCurrentGarden();
            } catch (GardenNotFoundException e) {
                LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_LOW);
                String bestprovider = manager.getBestProvider(criteria, true);
                try {
                    Location loc = manager.getLastKnownLocation(bestprovider);
                    Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses;
                    addresses = geoCoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                    currentGarden = new DefaultGarden(getApplicationContext(), addresses.get(0));
                    gardenManager.addGarden(currentGarden);
                    gardenManager.setCurrentGarden(currentGarden);
                } catch (Exception e1) {
                    currentGarden = new DefaultGarden(getApplicationContext(), new Address(Locale.getDefault()));
                    gardenManager.addGarden(currentGarden);
                    gardenManager.setCurrentGarden(currentGarden);
                    e1.printStackTrace();
                }
            }
        return currentGarden;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_simple);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        ActionBar bar = getSupportActionBar();
toolbar.setTitle("tete");
//        if (activities.size() > 0)
//            bar.setDisplayHomeAsUpEnabled(true);

        // TODO All this should be part of the application/service/...
        gotsPrefs = getGotsContext().getServerConfig();
        gotsPurchase = new GotsPurchaseItem(this);
        nuxeoManager = NuxeoManager.getInstance();
        gardenManager = GotsGardenManager.getInstance();
        seedManager = GotsSeedManager.getInstance();
        allotmentManager = GotsAllotmentManager.getInstance();
        gotsGrowingSeedManager = GotsGrowingSeedManager.getInstance();
        actionseedProvider = GotsActionSeedManager.getInstance();

        registerReceiver(nuxeoManager, new IntentFilter("NuxeoServerConnectivityChanged"));
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(gardenManager, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(allotmentManager, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(gotsGrowingSeedManager, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(seedManager, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(seedManager, new IntentFilter(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
        registerReceiver(progressReceiver, new IntentFilter(BroadCastMessages.PROGRESS_UPDATE));
        registerReceiver(progressReceiver, new IntentFilter(BroadCastMessages.PROGRESS_FINISHED));
        registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));
        registerReceiver(actionseedProvider, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(actionseedProvider, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));

        initAllManager();

        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            if (layout != null)
                layout.addView(ads.getAdsLayout());
        }

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        notificationText = (TextView) findViewById(R.id.textViewProgress);
        imageView = (ImageView) findViewById(R.id.imageViewRefresh);
        layoutNotification = (CoordinatorLayout) findViewById(R.id.layoutNotification);
        floatingActionsMenu = (FloatingActionsMenu) findViewById(R.id.idFAB);
    }

    protected void initAllManager() {
        gardenManager.initIfNew(this);
        seedManager.initIfNew(this);
        gotsGrowingSeedManager.initIfNew(this);
        nuxeoManager.initIfNew(this);
        allotmentManager.initIfNew(this);
        actionseedProvider.initIfNew(this);
    }

    protected void closeAllManager() {
        gardenManager.reset();
        seedManager.reset();
        gotsGrowingSeedManager.reset();
        nuxeoManager.shutdown();
        allotmentManager.reset();
        actionseedProvider.reset();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        activities.add(this);

        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

        try {
            final String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            GoogleAnalyticsTracker.getInstance().setCustomVar(1, "App Version", versionName, 1);
            if (versionName != null && versionName.contains("SNAPSHOT")) {
                GoogleAnalyticsTracker.getInstance().setDryRun(true);
            }
        } catch (NameNotFoundException e) {
        }

        if (gotsPurchase.isPremium()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Premium", 1);
        } else
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Free", 1);

        if (gotsPurchase.getFeatureParrot()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Type", "Parrot", 1);
        }

        if (gotsPrefs.isConnectedToServer()) {
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Connected", "Connected", 1);
        } else
            GoogleAnalyticsTracker.getInstance().setCustomVar(2, "Member Connected", "Guest", 1);


        super.onPostCreate(savedInstanceState);
    }

    /**
     * @return true if floating button needs to be shown
     */
    protected abstract boolean requireFloatingButton();

    protected void createFloatingMenu() {
        //The floating button might be already created, then toggle if it is an expanded menu
        if (bottomRightButton != null) {
            if (bottomRightButton instanceof FloatingActionsMenu && ((FloatingActionsMenu) bottomRightButton).isExpanded())
                ((FloatingActionsMenu) bottomRightButton).toggle();
            return;
        } else
            bottomRightButton = new View(getApplicationContext());

        List<FloatingItem> items = onCreateFloatingMenu();
        if (items == null || (items != null && items.size() == 0))
            return;

        for (FloatingItem floatingItem : items) {
            FloatingActionButton button = new FloatingActionButton(getApplicationContext());
            button.setSize(FloatingActionButton.SIZE_MINI);
            button.setColorNormalResId(R.color.action_error_color);
            button.setColorPressedResId(R.color.action_warning_color);
            button.setIcon(floatingItem.getRessourceId());
            button.setTitle(floatingItem.getTitle());

            button.setStrokeVisible(false);
            button.setOnLongClickListener(floatingItem.getOnLongClickListener());
            button.setOnClickListener(floatingItem.getOnClickListener());
            floatingActionsMenu.addButton(button);

            floatingActionsMenu.setColorNormalResId(R.color.text_color_dark);
            floatingActionsMenu.setColorPressedResId(R.color.green_light);
            bottomRightButton = floatingActionsMenu;
        }

    }

    protected List<FloatingItem> onCreateFloatingMenu() {
        return null;
    }

    @Override
    protected void onDestroy() {
        activities.remove(this);
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();

        if (activities.size() == 0) {
            Log.d(TAG, "onDestroy last activitie");
            closeAllManager();
        } else {
            Log.d(TAG, "No shutdown, remaining activities: " + activities);
        }
        unregisterReceiver(nuxeoManager);
        unregisterReceiver(gardenManager);
        unregisterReceiver(allotmentManager);
        unregisterReceiver(seedManager);
        unregisterReceiver(progressReceiver);
        unregisterReceiver(gotsGrowingSeedManager);
        unregisterReceiver(gardenBroadcastReceiver);
        unregisterReceiver(actionseedProvider);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (gotsBillingDialog != null && gotsBillingDialog.isAdded())
            gotsBillingDialog.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void displayPurchaseFragment(final List<HolderSku> skuList, final OnPurchaseFinished onPurchaseFinished) {
        FragmentManager fm = getSupportFragmentManager();
        gotsBillingDialog = new GotsBillingDialog();
        if (skuList != null) {
            for (HolderSku sku : skuList)
                gotsBillingDialog.addSKUFeature(sku);
        }
        gotsBillingDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);


        gotsBillingDialog.setOnPurchasedFinishedListener(new OnPurchaseFinished() {
            @Override
            public void onPurchaseFailed(Purchase sku) {
                showNotification(getResources().getString(R.string.inapp_purchase_failed), false);
                if (onPurchaseFinished != null)
                    onPurchaseFinished.onPurchaseFailed(sku);
            }

            @Override
            public void onPurchaseSucceed(Purchase purchase) {
                if (GotsPurchaseItem.SKU_PREMIUM.equals(purchase.getSku())) {
                    gotsPurchase.setPremium(true);
                }
                if (onPurchaseFinished != null) {
                    onPurchaseFinished.onPurchaseSucceed(purchase);
                }

                //check if sku has been set consumable and consume
                for (HolderSku sku : skuList) {
                    if (sku.getSku() != null & sku.getSku().equals(purchase.getSku()) && sku.isConsumable())
                        gotsBillingDialog.consumePurchase(purchase);
                }
                showNotification(getResources().getString(R.string.inapp_purchase_succeed) + ": " + purchase.getSku(), false);
                runAsyncDataRetrieval();
                gotsBillingDialog.dismiss();
            }
        });
        gotsBillingDialog.show(fm, "fragment_edit_name");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common, menu);
        this.menu = menu;
        if (requireRefreshSyncAuthority() == null)
            menu.findItem(R.id.refresh_seed).setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.refresh_seed:
                onRefresh(requireRefreshSyncAuthority());
                break;
            case R.id.help:
                Intent browserIntent = new Intent(this, WebHelpActivity.class);
                browserIntent.putExtra(WebHelpActivity.URL_CLASSNAME, getClass().getSimpleName());
                startActivity(browserIntent);
                return true;
            case android.R.id.home:
                if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                    getSupportFragmentManager().popBackStack();
                else
                    finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setProgressRefresh(boolean refresh) {

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

    private void onRefresh(String AUTHORITY) {
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

    protected String requireRefreshSyncAuthority() {
        return null;
    }

    /*
     * (non-Javadoc)
     * @see org.nuxeo.android.activities.BaseNuxeoActivity#requireAsyncDataRetrieval()
     * Set to true if you need Floating Menu
     */
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
        if (requireFloatingButton()) {
            createFloatingMenu();
        }
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

    protected void setTitleBar(final int dashboardAllotmentsName) {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(dashboardAllotmentsName);
    }

    private int getMainLayout() {
        return R.id.mainLayout;
    }

    private int getContentLayout() {
        if (findViewById(R.id.contentLayout) != null)
            return R.id.contentLayout;
        else
            return getMainLayout();
    }

    protected void addContentLayout(Fragment contentFragment, Bundle options) {
        if (!contentFragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
            transaction.addToBackStack(null);
            if (options != null)
                contentFragment.setArguments(options);
            contentFragment.setRetainInstance(false);
            transaction.replace(getContentLayout(), contentFragment).commitAllowingStateLoss();
        }
        if (findViewById(R.id.contentScrollView) != null)
            findViewById(R.id.contentScrollView).setVisibility(View.VISIBLE);
    }

    protected void addResumeLayout(Fragment resumeFragment, Bundle options) {
        if (!resumeFragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            if (options != null)
                resumeFragment.setArguments(options);
            resumeFragment.setRetainInstance(true);
            if (findViewById(R.id.resumeLayout) != null)
                transaction.replace(R.id.resumeLayout, resumeFragment).commitAllowingStateLoss();
        }
    }

    protected void hideResumeLayout() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.resumeLayout);

        if (fragment != null) {

//            findViewById(R.id.resumeLayout).animate().translationY(-findViewById(R.id.resumeLayout).getHeight()).setInterpolator(new DecelerateInterpolator(2)).start();
//            findViewById(R.id.mainLayout).animate().y(-findViewById(R.id.resumeLayout).getHeight()).setInterpolator(new DecelerateInterpolator(2)).start();
            transaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
            transaction.remove(fragment).commitAllowingStateLoss();
        }

    }

    protected void addMainLayout(Fragment contentFragment, Bundle options) {
        if (!contentFragment.isAdded()) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.abc_slide_in_bottom, R.anim.abc_slide_out_top);
            if (options != null)
                contentFragment.setArguments(options);
            contentFragment.setRetainInstance(true);
            transaction.replace(getMainLayout(), contentFragment).commitAllowingStateLoss();
        }
    }

    @Override
    public void onBackStackChanged() {
//        if (getSupportFragmentManager().getBackStackEntryCount() == 0 && findViewById(R.id.contentLayout) != null) {
//            findViewById(R.id.contentScrollView).setVisibility(View.GONE);
//        }
        if (floatingActionsMenu != null && floatingActionsMenu.isExpanded())
            floatingActionsMenu.collapse();
    }

    protected BaseGotsFragment getContentFragment() {
        if (getSupportFragmentManager().findFragmentById(R.id.contentLayout) instanceof BaseGotsFragment)
            return (BaseGotsFragment) getSupportFragmentManager().findFragmentById(R.id.contentLayout);
        else
            return null;
    }

    public void showNotification(String message, boolean animation) {
        showNotification(message, animation, LENGHT_LONG);
    }

    public void showNotification(String message, boolean animation, final int millis) {
        Snackbar snackbar = Snackbar
                .make(layoutNotification, message, Snackbar.LENGTH_LONG);

        snackbar.show();
//        notificationText.setText(message);
//        if (animation) {
//            Animation myRotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
//            myRotateAnimation.setRepeatCount(Animation.INFINITE);
//            imageView.startAnimation(myRotateAnimation);
//
//        } else {
//            imageView.clearAnimation();
//            imageView.setVisibility(View.GONE);
//            notificationText.clearAnimation();
//        }
//
//        layoutNotification.setVisibility(View.VISIBLE);
//        layoutNotification.setAlpha(0.0f);
//
////        Animation animationIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_up);
////        layoutNotification.setAnimation(animationIn);
//        layoutNotification.animate()
//                .alpha(1.0f)
//                .setDuration(500)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        super.onAnimationEnd(animation);
//                        Animation animationOut = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_up);
//                        layoutNotification.animate()
//                                .alpha(0.0f)
//                                .setDuration(millis).setListener(new AnimatorListenerAdapter() {
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                layoutNotification.setVisibility(View.GONE);
//                                super.onAnimationEnd(animation);
//                            }
//                        });
////                        layoutNotification.setVisibility(View.GONE);
//
//                    }
//                });

    }

    public interface GardenListener {
        public void onCurrentGardenChanged(GardenInterface garden);
    }
}
