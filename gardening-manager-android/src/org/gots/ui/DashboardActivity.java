/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.util.List;

import org.gots.R;
import org.gots.ads.GotsAdvertisement;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.inapp.AppRater;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.provider.WeatherContentProvider;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DashboardActivity extends BaseGotsActivity implements OnClickListener, ActionBar.OnNavigationListener {
    public static final String LAUNCHER_ACTION = "org.gots.dashboard.action";

    public static final String LAUNCHER_CATALOGUE = "org.gots.dashboard.catalogue";

    // GoogleAnalyticsTracker tracker;
    GotsAdvertisement adView;

    private WeatherWidget weatherWidget;

    private WeatherWidget weatherWidget2;

    private LinearLayout handle;

    private LinearLayout weatherWidgetLayout;

    // private Intent weatherIntent;

    private String TAG = "DashboardActivity";

    private MenuItem itemConnected;

    private List<GardenInterface> myGardens;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppRater.app_launched(this);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        setContentView(R.layout.dashboard);

        // attach event handler to dash buttons
        findViewById(R.id.dashboard_button_hut).setOnClickListener(this);
        findViewById(R.id.dashboard_button_allotment).setOnClickListener(this);
        findViewById(R.id.dashboard_button_action).setOnClickListener(this);
        findViewById(R.id.dashboard_button_profile).setOnClickListener(this);
        findViewById(R.id.dashboard_button_sensor).setOnClickListener(this);

        handle = (LinearLayout) findViewById(R.id.handle);

        weatherWidgetLayout = (LinearLayout) findViewById(R.id.WeatherWidget);

        checkPremiumAds();
        // weatherIntent = new Intent(this, WeatherUpdateService.class);
        // startService(weatherIntent);

        registerReceiver(weatherBroadcastReceiver, new IntentFilter(BroadCastMessages.WEATHER_DISPLAY_EVENT));
        registerReceiver(weatherBroadcastReceiver, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(weatherBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));

        refreshGardenMenu(getSupportActionBar());
        Log.d(TAG, "onCreate");

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (LAUNCHER_ACTION.equals(getIntent().getAction())) {
            startActivity(new Intent(this, ActionActivity.class));
        } else if (LAUNCHER_CATALOGUE.equals(getIntent().getAction()))
            startActivity(new Intent(this, HutActivity.class));

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setSyncAutomatically(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, bundle);
    }

    protected void refreshGardenMenu(final ActionBar actionBar) {

        new AsyncTask<Void, Void, GardenInterface>() {

            @Override
            protected GardenInterface doInBackground(Void... params) {
                myGardens = gardenManager.getMyGardens(false);
                GardenInterface currentGarden = gardenManager.getCurrentGarden();

                if (currentGarden == null)
                    myGardens = gardenManager.getMyGardens(true);

                return currentGarden;
            }

            protected void onPostExecute(GardenInterface currentGarden) {
                if (currentGarden == null)

                    if (myGardens.size() > 0) {
                        gardenManager.setCurrentGarden(myGardens.get(0));
                        sendBroadcast(new Intent(BroadCastMessages.GARDEN_CURRENT_CHANGED));
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ProfileCreationActivity.class);
                        startActivity(intent);
                        // AccountManager accountManager = AccountManager.get(getApplicationContext()
                        // );
                        // accountManager.addAccount(accountType, authTokenType, requiredFeatures, addAccountOptions,
                        // activity, callback, handler)
                    }
                int selectedGardenIndex = 0;
                String[] dropdownValues = new String[myGardens.size()];
                for (int i = 0; i < myGardens.size(); i++) {
                    GardenInterface garden = myGardens.get(i);
                    dropdownValues[i] = garden.getName() != null ? garden.getName() : garden.getLocality();
                    if (garden != null && currentGarden != null && garden.getId() == currentGarden.getId()) {
                        selectedGardenIndex = i;
                    }
                }
                if (dropdownValues.length > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DashboardActivity.this,
                            android.R.layout.simple_spinner_item, android.R.id.text1, dropdownValues);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    actionBar.setListNavigationCallbacks(adapter, DashboardActivity.this);
                    actionBar.setSelectedNavigationItem(selectedGardenIndex);
                }
            };
        }.execute();

    }

    protected void checkPremiumAds() {
        // ADMOB
        LinearLayout layout = (LinearLayout) findViewById(R.id.bannerAd);
        if (!gotsPurchase.isPremium()) {
            adView = new GotsAdvertisement(this);
            View ads = adView.getPremiumAds(layout);
            ads.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    FragmentManager fm = getSupportFragmentManager();
                    GotsBillingDialog purchaseDialog = new GotsBillingDialog();
                    purchaseDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                    purchaseDialog.show(fm, "fragment_edit_name");

                }
            });
        } else {
            layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dashboard_top));
            ImageView logo = (ImageView) findViewById(R.id.idImageLogo);
            logo.setBackgroundDrawable(getResources().getDrawable(R.drawable.bt_logo_premium));
        }
    }

    private BroadcastReceiver weatherBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.WEATHER_DISPLAY_EVENT.equals(intent.getAction())) {
                refreshWeatherWidget(intent);
            } else if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())) {
                refreshConnectionState();
                refreshGardenMenu(getSupportActionBar());
                refreshWeatherWidget(intent);
            } else if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                refreshGardenMenu(getSupportActionBar());
            }
        }

    };

    private int currentItemPosition = 0;

    protected void refreshConnectionState() {
        if (itemConnected == null) {
            return;
        }
        if (gotsPrefs.isConnectedToServer())
            itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_connected));
        else
            itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_disconnected));
    }

    private void refreshWeatherWidget(Intent intent) {
        boolean isError = intent.getBooleanExtra("error", false);

        handle.removeAllViews();
        weatherWidgetLayout.removeAllViews();

        if (isError) {
            TextView txtError = new TextView(this);
            txtError.setText(getResources().getText(R.string.weather_citynotfound));
            txtError.setTextColor(getResources().getColor(R.color.text_color_light));
            handle.addView(txtError);
            Log.d(TAG, "WeatherWidget display error");

        } else {
            weatherWidget2 = new WeatherWidget(this, WeatherView.IMAGE);
            handle.addView(weatherWidget2);
            weatherWidget = new WeatherWidget(this, WeatherView.TEXT);
            weatherWidgetLayout.addView(weatherWidget);
            Log.d(TAG, "WeatherWidget display ok");
        }

    }

    @Override
    public void onClick(View v) {

        Intent i = null;
        switch (v.getId()) {
        case R.id.dashboard_button_hut:

            i = new Intent(v.getContext(), HutActivity.class);
            break;
        case R.id.dashboard_button_allotment:

            i = new Intent(v.getContext(), MyMainGarden.class);
            break;
        case R.id.dashboard_button_action:

            i = new Intent(v.getContext(), ActionActivity.class);

            break;
        case R.id.dashboard_button_profile:

            i = new Intent(v.getContext(), org.gots.ui.ProfileActivity.class);
            break;
        case R.id.dashboard_button_sensor:
            GotsPurchaseItem purchaseItem = new GotsPurchaseItem(this);

            if (purchaseItem.getFeatureParrot() ? true : purchaseItem.isPremium()) {
                i = new Intent(this, SensorActivity.class);
            } else {
                // if (!purchaseItem.getFeatureParrot() && !purchaseItem.isPremium()) {
                FragmentManager fm = getSupportFragmentManager();
                GotsBillingDialog editNameDialog = new GotsBillingDialog(GotsPurchaseItem.SKU_FEATURE_PARROT);
                editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                editNameDialog.show(fm, "fragment_edit_name");
            }
            break;
        default:
            break;
        }
        if (i != null) {
            startActivity(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(weatherBroadcastReceiver);
        // stopService(weatherIntent);
        // if (buyHelper != null)
        // buyHelper.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPremiumAds();

        GoogleAnalyticsTracker.getInstance().dispatch();

        // if (gotsPrefs.getCurrentGardenId() == -1) {

        // Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
        // findViewById(R.id.dashboard_button_profile).startAnimation(myFadeInAnimation);
        // } else
        // findViewById(R.id.dashboard_button_profile).clearAnimation();

        // refreshConnectionState();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

        case R.id.help:
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);

            return true;
        case R.id.about:
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);

            return true;
        case R.id.premium:
            FragmentManager fm = getSupportFragmentManager();
            GotsBillingDialog editNameDialog = new GotsBillingDialog();
            editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            editNameDialog.show(fm, "fragment_edit_name");
            return true;

        case R.id.settings:
            Intent settingsIntent = new Intent(this, PreferenceActivity.class);
            startActivity(settingsIntent);
            // FragmentTransaction ft = getFragmentManager().beginTransaction();
            // ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            // ft.replace(R.id.idContent, new PreferenceActivity()).addToBackStack("back").commit();
            return true;

        case R.id.connection:
            LoginDialogFragment login = new LoginDialogFragment();
            login.show(getSupportFragmentManager(), TAG);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        itemConnected = (MenuItem) menu.findItem(R.id.connection);
        refreshConnectionState();
        if (gotsPurchase.isPremium())
            menu.findItem(R.id.premium).setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        if (itemPosition != currentItemPosition) {
            currentItemPosition = itemPosition;
            gardenManager.setCurrentGarden(myGardens.get(itemPosition));
            sendBroadcast(new Intent(BroadCastMessages.GARDEN_CURRENT_CHANGED));

            // startService(weatherIntent);
            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
            ContentResolver.setSyncAutomatically(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, true);
            ContentResolver.requestSync(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, bundle);
        }
        Log.d(TAG, "onNavigationItemSelected");
        return false;
    }

    @Override
    protected void onRefresh(String AUTHORITY) {
        // startService(weatherIntent);
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.setSyncAutomatically(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, bundle);
        Log.d(TAG, "onRefresh");
    }
}
