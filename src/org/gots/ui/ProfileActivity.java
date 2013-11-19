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
import org.gots.garden.adapter.ProfileAdapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

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

        profileList = (ListView) findViewById(R.id.IdGardenProfileList);

        this.registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));

        if (!gotsPrefs.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    public BroadcastReceiver gardenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                // force_refresh = true;
                try {
                    GardenSync gardenSync = new GardenSync(true);
                    gardenSync.execute(getApplicationContext());

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    };

    class GardenSync extends AsyncTask<Context, Void, List<GardenInterface>> {
        ProgressDialog dialog;

        private boolean force_refresh = true;

        public GardenSync(boolean force) {
            force_refresh = force;
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(ProfileActivity.this, "", getResources().getString(R.string.gots_loading),
                    true);
            dialog.setCanceledOnTouchOutside(true);
            // dialog.show();
            super.onPreExecute();
        }

        @Override
        protected List<GardenInterface> doInBackground(Context... params) {
            return gardenManager.getMyGardens(force_refresh);
        }

        @Override
        protected void onPostExecute(List<GardenInterface> myGardens) {
            force_refresh = false;
            try {
                dialog.dismiss();
                dialog = null;
            } catch (Exception e) {
                // nothing
            }
            profileAdapter = new ProfileAdapter(ProfileActivity.this, myGardens);
            profileList.setAdapter(profileAdapter);
            profileAdapter.notifyDataSetChanged();
            if (profileAdapter != null && profileAdapter.getCount() == 0) {
                Intent intentCreation = new Intent(getApplicationContext(), ProfileCreationActivity.class);
                startActivity(intentCreation);
            } else {
                // Select default current garden
                if (gardenManager.getCurrentGarden() == null || gardenManager.getCurrentGarden() != null
                        && gardenManager.getCurrentGarden().getId() == -1) {
                    gardenManager.setCurrentGarden(profileAdapter.getItem(0));
                }
                super.onPostExecute(myGardens);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        // new GardenClass().execute(this);
        try {
            GardenSync gardenSync = new GardenSync(false);
            gardenSync.execute(this);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
//        if (gardenManager.getCurrentGarden() == null || gardenManager.getCurrentGarden() != null
//                && gardenManager.getCurrentGarden().getId() == -1) {
//            findViewById(R.id.idSelectGarden).setVisibility(View.VISIBLE);
//        } else
//            findViewById(R.id.idSelectGarden).setVisibility(View.GONE);

        // buildGardenList();
        // weatherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_weather));
        // weatherState.setImageDrawable(getResources().getDrawable(R.drawable.weather_updating));
        // startService(weatherIntent);
        // registerReceiver(weatherBroadcastReceiver, new
        // IntentFilter(WeatherUpdateService.BROADCAST_ACTION));
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onPause() {

        super.onPause();
        // unregisterReceiver(weatherBroadcastReceiver);
        // stopService(weatherIntent);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(gardenBroadcastReceiver);
        super.onDestroy();
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
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
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
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Delete");
            builder.setMessage("Are you sure?");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    gardenManager.removeGarden(gardenManager.getCurrentGarden());

                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            // profileAdapter.notifyDataSetChanged();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
