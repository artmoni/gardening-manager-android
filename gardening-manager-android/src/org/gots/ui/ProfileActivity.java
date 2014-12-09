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

import java.util.Iterator;
import java.util.List;

import org.gots.R;
import org.gots.ads.GotsAdvertisement;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.garden.adapter.ProfileAdapter;
import org.gots.provider.GardenContentProvider;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class ProfileActivity extends BaseGotsActivity {

    private static final String TAG = "ProfileActivity";

    private ProfileAdapter profileAdapter;

    private ListView profileList;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_profile_name);

        profileList = (ListView) findViewById(R.id.IdGardenProfileList);

        this.registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));
        this.registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));

        try {
            // GardenSync gardenSync = new GardenSync();
            // gardenSync.execute(false);
            runAsyncDataRetrieval();

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

        // ******** MAP
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng arg0) {
                gardenManager.getCurrentGarden().setGpsLatitude(arg0.latitude);
                gardenManager.getCurrentGarden().setGpsLongitude(arg0.longitude);

                Intent intent = new Intent(getApplicationContext(), ProfileCreationActivity.class);
                intent.putExtra("option", ProfileCreationActivity.OPTION_EDIT);
                startActivity(intent);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker arg0) {
                
                return false;
            }
        });
        try {
            MapsInitializer.initialize(getApplicationContext());
            displayGardensOnMap();
            focusGardenOnMap(gardenManager.getCurrentGarden());
        } catch (GooglePlayServicesNotAvailableException e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void focusGardenOnMap(GardenInterface garden) {
        LatLng gardenPOI = new LatLng(garden.getGpsLatitude(), garden.getGpsLongitude());
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(gardenPOI, 17));
    }

    protected void displayGardensOnMap() {
        // map.setMyLocationEnabled(true);

        map.clear();
        for (GardenInterface garden : gardenManager.getMyGardens(false)) {
            LatLng gardenPOI = new LatLng(garden.getGpsLatitude(), garden.getGpsLongitude());
            map.addMarker(new MarkerOptions().title(garden.getName()).snippet(garden.getDescription()).position(
                    gardenPOI));

        }
    }

    public BroadcastReceiver gardenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                try {
                    runAsyncDataRetrieval();
                    displayGardensOnMap();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
            if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
                focusGardenOnMap(gardenManager.getCurrentGarden());
            }
        }
    };

    protected boolean requireAsyncDataRetrieval() {
        return true;
    };

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return gardenManager.getMyGardens(true);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object myGardens) {
        profileAdapter = new ProfileAdapter(ProfileActivity.this, (List<GardenInterface>) myGardens);
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
        super.onNuxeoDataRetrieved(myGardens);
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
        case R.id.delete_allotment:
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
