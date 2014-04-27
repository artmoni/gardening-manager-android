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

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.ads.GotsAdvertisement;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.inapp.AppRater;
import org.gots.inapp.GotsBillingDialog;
import org.gots.weather.service.WeatherUpdateService;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DashboardActivity extends AbstractActivity implements OnClickListener, ActionBar.OnNavigationListener {
    // GoogleAnalyticsTracker tracker;
    GotsAdvertisement adView;

    private WeatherWidget weatherWidget;

    private WeatherWidget weatherWidget2;

    private LinearLayout handle;

    private LinearLayout weatherWidgetLayout;

    private Intent weatherIntent;

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

        // NewRelic.withApplicationToken( "AA89617084bf906d3a0425f6cf6a382ce574b3acd8" ).start(this.getApplication());
        setContentView(R.layout.dashboard);

        // attach event handler to dash buttons
        findViewById(R.id.dashboard_button_hut).setOnClickListener(this);
        findViewById(R.id.dashboard_button_allotment).setOnClickListener(this);
        findViewById(R.id.dashboard_button_action).setOnClickListener(this);
        findViewById(R.id.dashboard_button_profile).setOnClickListener(this);

        handle = (LinearLayout) findViewById(R.id.handle);

        weatherWidgetLayout = (LinearLayout) findViewById(R.id.WeatherWidget);

        checkPremiumAds();
        weatherIntent = new Intent(this, WeatherUpdateService.class);

        registerReceiver(weatherBroadcastReceiver, new IntentFilter(BroadCastMessages.WEATHER_DISPLAY_EVENT));
        registerReceiver(weatherBroadcastReceiver, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
    }

    protected void displayGardenMenu(final ActionBar actionBar) {
        myGardens = gardenManager.getMyGardens(false);
        GardenInterface currentGarden = gardenManager.getCurrentGarden();

        if (currentGarden == null)
            if (myGardens.size() > 0)
                gardenManager.setCurrentGarden(myGardens.get(0));
            else {
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
            }

        int selectedGardenIndex = 0;
        String[] dropdownValues = new String[myGardens.size()];
        for (int i = 0; i < myGardens.size(); i++) {
            GardenInterface garden = myGardens.get(i);
            dropdownValues[i] = garden.getName();
            if (garden != null && currentGarden != null && garden.getId() == currentGarden.getId())
                selectedGardenIndex = i;
        }
        if (dropdownValues.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,
                    android.R.id.text1, dropdownValues);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
            actionBar.setListNavigationCallbacks(adapter, this);
            actionBar.setSelectedNavigationItem(selectedGardenIndex);
        }
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

                updateUI(intent);
            } else if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())) {
                refreshConnectionState();

            }
        }

    };

    protected void refreshConnectionState() {
        if (itemConnected == null) {
            return;
        }
        if (gotsPrefs.isConnectedToServer())
            itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_connected));
        else
            itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_disconnected));
    }

    private void updateUI(Intent intent) {
        boolean isError = intent.getBooleanExtra("error", true);
        Log.d(TAG, "=>" + isError);

        handle.removeAllViews();
        weatherWidgetLayout.removeAllViews();

        if (isError) {
            TextView txtError = new TextView(this);
            txtError.setText(getResources().getText(R.string.weather_citynotfound));
            txtError.setTextColor(getResources().getColor(R.color.text_color_light));
            handle.addView(txtError);

        } else {
            weatherWidget2 = new WeatherWidget(this, WeatherView.IMAGE);
            handle.addView(weatherWidget2);
            weatherWidget = new WeatherWidget(this, WeatherView.TEXT);
            weatherWidgetLayout.addView(weatherWidget);
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
        stopService(weatherIntent);
        // if (buyHelper != null)
        // buyHelper.dispose();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPremiumAds();

        GoogleAnalyticsTracker.getInstance().dispatch();

        startService(weatherIntent);

        // if (gotsPrefs.getCurrentGardenId() == -1) {

        // Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
        // findViewById(R.id.dashboard_button_profile).startAnimation(myFadeInAnimation);
        // } else
        // findViewById(R.id.dashboard_button_profile).clearAnimation();

        displayGardenMenu(getSupportActionBar());
        refreshConnectionState();

        // new AsyncTask<Void, Void, GardenInterface>() {
        // ActionBar bar = getSupportActionBar();
        //
        // @Override
        // protected GardenInterface doInBackground(Void... params) {
        // return gardenManager.getCurrentGarden();
        // }
        //
        // @Override
        // protected void onPostExecute(GardenInterface currentGarden) {
        // if (currentGarden != null) {
        // bar.setTitle(currentGarden.getLocality());
        // } else {
        // bar.setTitle(gotsPrefs.getGardeningManagerAppname());
        // }
        // startService(weatherIntent);
        //
        // // if (gotsPrefs.getCurrentGardenId() == -1) {
        // if (gardenManager.getCurrentGarden() == null) {
        // Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
        // startActivity(intent);
        // Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
        // findViewById(R.id.dashboard_button_profile).startAnimation(myFadeInAnimation);
        // } else
        // findViewById(R.id.dashboard_button_profile).clearAnimation();
        //
        // refreshConnectionState();
        // displayGardenMenu(getSupportActionBar());
        // super.onPostExecute(currentGarden);
        // }
        // }.execute();

    }

    @Override
    protected void onPause() {
        super.onPause();

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
            // case R.id.login:
            // // Intent loginIntent = new Intent(this, LoginDialogFragment.class);
            // // startActivity(loginIntent);
            // LoginDialogFragment login = new LoginDialogFragment();
            // login.show(getSupportFragmentManager(), TAG);
            // return true;
        case R.id.premium:
            FragmentManager fm = getSupportFragmentManager();
            GotsBillingDialog editNameDialog = new GotsBillingDialog();
            editNameDialog.show(fm, "fragment_edit_name");
            return true;

            // case R.id.settings:
            // // Intent settingsIntent = new Intent(this, SettingsActivity.class);
            // // startActivity(settingsIntent);
            // // FragmentTransaction ft = getFragmentManager().beginTransaction();
            // // ft.setCustomAnimations(android.R.animator.fade_in,
            // // android.R.animator.fade_out);
            // // ft.replace(R.id.idContent,new PreferenceActivity()).addToBackStack("back").commit();
            // return true;

        case R.id.connection:
            LoginDialogFragment login = new LoginDialogFragment();
            login.show(getSupportFragmentManager(), TAG);
            // if (gotsPrefs.isConnectedToServer())
            // Toast.makeText(this, getResources().getString(R.string.login_connect_state), Toast.LENGTH_SHORT).show();
            // else
            // Toast.makeText(this, getResources().getString(R.string.login_disconnect_state),
            // Toast.LENGTH_SHORT).show();
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
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        // if (!buyHelper.handleActivityResult(requestCode, resultCode, data)) {
        // // not handled, so handle it ourselves (here's where you'd
        // // perform any handling of activity results not related to in-app
        // // billing...
        // super.onActivityResult(requestCode, resultCode, data);
        // } else {
        // Log.d(TAG, "onActivityResult handled by IABUtil.");
        // }
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        // Toast.makeText(getBaseContext(), "You selected : " + myGardens.get(itemPosition), Toast.LENGTH_SHORT).show();
        gardenManager.setCurrentGarden(myGardens.get(itemPosition));
        startService(weatherIntent);
        return false;
    }

}
