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
import org.gots.analytics.GotsAnalytics;
import org.gots.garden.GardenInterface;
import org.gots.garden.adapter.ProfileAdapter;
import org.gots.help.HelpUriBuilder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ProfileActivity extends AbstractActivity {

    private static final String TAG = "ProfileActivity";

    private ProfileAdapter profileAdapter;

    private ListView profileList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_profile_name);

        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

        profileList = (ListView) findViewById(R.id.IdGardenProfileList);

        if (!gotsPrefs.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }
    }

    class GardenSync extends AsyncTask<Context, Void, List<GardenInterface>> {
        ProgressDialog dialog;


        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ProfileActivity.this, "", getResources().getString(R.string.gots_loading), true);
            dialog.setCanceledOnTouchOutside(true);
//            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected List<GardenInterface> doInBackground(Context... params) {
            return gardenManager.getMyGardens(false);
        }

        @Override
        protected void onPostExecute(List<GardenInterface> myGardens) {
            profileAdapter = new ProfileAdapter(ProfileActivity.this, myGardens);
            profileList.setAdapter(profileAdapter);
            profileAdapter.notifyDataSetChanged();
            if (dialog.isShowing())
                dialog.dismiss();
            super.onPostExecute(myGardens);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // new GardenClass().execute(this);
        try {
            GardenSync gardenSync = new GardenSync();
            gardenSync.execute(this);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        // buildGardenList();
        // weatherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_weather));
        // weatherState.setImageDrawable(getResources().getDrawable(R.drawable.weather_updating));
        // startService(weatherIntent);
        // registerReceiver(weatherBroadcastReceiver, new
        // IntentFilter(WeatherUpdateService.BROADCAST_ACTION));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregisterReceiver(weatherBroadcastReceiver);
        // stopService(weatherIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

        case android.R.id.home:
            finish();
            return true;

        case R.id.help:
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
            startActivity(browserIntent);
            return true;

        case R.id.new_gaden:
            Intent i = new Intent(this, ProfileCreationActivity.class);
            startActivity(i);
            return true;
        case R.id.edit_garden:
            Intent intent = new Intent(this, ProfileCreationActivity.class);
            intent.putExtra("option", ProfileCreationActivity.OPTION_EDIT);
            startActivity(intent);

            return true;
        case R.id.delete_garden:
           
            if (profileAdapter.getCount()==0) {
                Intent intentCreation = new Intent(this, ProfileCreationActivity.class);
                intentCreation.putExtra("option", ProfileCreationActivity.OPTION_EDIT);
                startActivity(intentCreation);
            } else {
                new AsyncTask<GardenInterface, Integer, Void>() {
                    @Override
                    protected Void doInBackground(GardenInterface... params) {
                        gardenManager.removeGarden(params[0]);
                        return null;
                    }
                }.execute(gardenManager.getCurrentGarden());
                try {
                    GardenSync gardenSync = new GardenSync();
                    gardenSync.execute(this);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            profileAdapter.notifyDataSetChanged();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
        super.onDestroy();
    }
}
