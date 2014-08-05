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
import org.gots.provider.GardenContentProvider;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;

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

        try {
            GardenSync gardenSync = new GardenSync();
            gardenSync.execute(false);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        
        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    public BroadcastReceiver gardenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                try {
                    GardenSync gardenSync = new GardenSync();
                    gardenSync.execute(true);

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    };

    class GardenSync extends AsyncTask<Boolean, Void, List<GardenInterface>> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            setProgressRefresh(true);
            super.onPreExecute();
        }

        @Override
        protected List<GardenInterface> doInBackground(Boolean... force) {
            return gardenManager.getMyGardens(force[0]);
        }

        @Override
        protected void onPostExecute(List<GardenInterface> myGardens) {

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
            }
            setProgressRefresh(false);
            super.onPostExecute(myGardens);
        }

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(gardenBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
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

        case R.id.new_garden:
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
                    new AsyncTask<Void, Void, GardenInterface>() {
                        @Override
                        protected GardenInterface doInBackground(Void... params) {
                            gardenManager.removeGarden(gardenManager.getCurrentGarden());
                            return null;
                        }

                        protected void onPostExecute(GardenInterface result) {
                            sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));
                        };
                    }.execute();

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

    @Override
    protected void onRefresh(String AUTHORITY) {
        super.onRefresh(GardenContentProvider.AUTHORITY);
    }

}
