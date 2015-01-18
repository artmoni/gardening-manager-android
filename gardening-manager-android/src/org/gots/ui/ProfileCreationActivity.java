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

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.GardeningActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.provider.local.LocalAllotmentProvider;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.bean.Garden;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;

import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class ProfileCreationActivity extends BaseGotsActivity implements LocationListener, OnClickListener {
    public static final int OPTION_EDIT = 1;

    private LocationManager mlocManager;

    GardenInterface garden;

    private int mode = 0;

    private TextView editTextName;

    private GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (getIntent().getExtras() != null)
            mode = getIntent().getExtras().getInt("option");
        if (mode == OPTION_EDIT) {
            garden = getCurrentGarden();
        } else {
            garden = new Garden();
        }
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.profilecreation);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(false);
        bar.setTitle(R.string.profile_menu_localize);

        // garden.setLocality("");

        buildProfile();

        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng arg0) {
                setAddressFromLocation(arg0.latitude, arg0.longitude);
                focusGardenOnMap(garden.getGpsLatitude(), garden.getGpsLongitude());
            }
        });
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                Toast.makeText(getApplicationContext(), "Long click to set location", Toast.LENGTH_SHORT).show();

            }
        });
        if (garden.getGpsLatitude() == 0 || garden.getGpsLongitude() == 0) {
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            Location lastKnownLocation = mlocManager.getLastKnownLocation(locationProvider);
            if (lastKnownLocation != null) {
                setAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                focusGardenOnMap(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            } else
                mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);

        } else {
            focusGardenOnMap(garden.getGpsLatitude(), garden.getGpsLongitude());
        }
    }

    private void buildProfile() {
        editTextName = (TextView) findViewById(R.id.editTextGardenName);

        findViewById(R.id.buttonValidatePosition).setOnClickListener(this);

        // findViewById(R.id.buttonAddGarden).setOnClickListener(this);
        if (getCurrentGarden() != null)
            ((CheckBox) findViewById(R.id.checkboxSamples)).setChecked(false);

        if (mode == OPTION_EDIT && getCurrentGarden() != null
                && getCurrentGarden().getLocality() != null) {
            editTextName.setText(getCurrentGarden().getName());
        }

    };

    private void getPosition(boolean force) {
        setProgressRefresh(true);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if (mlocManager == null)
            return;
        try {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);
            if (force) {
                String bestProvider = mlocManager.getBestProvider(criteria, true);
                if ("gps".equals(bestProvider))
                    mlocManager.requestLocationUpdates(bestProvider, 60000, 0, this);
            }
        } catch (Exception e) {
            Log.e(ProfileCreationActivity.class.getName(), e.getMessage());
        }

    }

    private void setAddressFromLocation(double latitude, double longitude) {

        Geocoder geo = new Geocoder(ProfileCreationActivity.this);
        try {
            List<Address> adresses = geo.getFromLocation(latitude, longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address address = adresses.get(0);
                garden.setGpsLatitude(latitude);
                garden.setGpsLongitude(longitude);
                garden.setLocality(address.getLocality());
                garden.setAdminArea(address.getAdminArea());
                garden.setCountryName(address.getCountryName());
                garden.setCountryCode(address.getCountryCode());
            } else {
                // sinon on affiche un message d'erreur
                // ((TextView) findViewById(R.id.editTextLocality)).setHint(getResources().getString(
                // R.string.location_notfound));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        // this.location = location;
        setAddressFromLocation(location.getLatitude(), location.getLongitude());
        setProgressRefresh(false);
        mlocManager.removeUpdates(this);
        focusGardenOnMap(location.getLatitude(), location.getLongitude());
    }

    private void focusGardenOnMap(double latitude, double longitude) {
        LatLng gardenPOI = new LatLng(latitude, longitude);
        map.clear();
        MarkerOptions marker = new MarkerOptions().position(gardenPOI).title(garden.getName());
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.bt_dashboard_profile));
        map.addMarker(marker);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(gardenPOI, 17));
    }

    @Override
    public void onProviderDisabled(String provider) {
        /* this is called if/when the GPS is disabled in settings */
        Log.v(TAG, "Disabled");

        /* bring up the GPS settings */
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.v(TAG, "Enabled");
        Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
            Log.v(TAG, "Status Changed: Out of Service");
            Toast.makeText(this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
            break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.v(TAG, "Status Changed: Temporarily Unavailable");
            Toast.makeText(this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
            break;
        case LocationProvider.AVAILABLE:
            Log.v(TAG, "Status Changed: Available");
            Toast.makeText(this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
            break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        case R.id.buttonValidatePosition:
            if (mode == OPTION_EDIT)
                updateProfile();
            else
                createNewProfile();
            break;

        default:
            break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profilecreation, menu);

        return super.onCreateOptionsMenu(menu);
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
        case R.id.localize_gaden:
            getPosition(true);
            buildProfile();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean verifyForm() {
        garden.setName(editTextName.getText().toString());
        if (garden.getLocality() == null || "".equals(garden.getLocality())) {
            Toast.makeText(getApplicationContext(), "Please locate your garden on the map", Toast.LENGTH_LONG).show();
            findViewById(R.id.map).setBackground(getResources().getDrawable(R.drawable.border_red));

            return false;
        }
        if ("".equals(garden.getName())) {
            Toast.makeText(getApplicationContext(), "Please name your garden", Toast.LENGTH_LONG).show();
            findViewById(R.id.editTextGardenName).setBackground(getResources().getDrawable(R.drawable.border_red));

            return false;
        }
        return true;
    }

    private void createNewProfile() {
        if (!verifyForm())
            return;

        new AsyncTask<Void, Void, GardenInterface>() {
            @Override
            protected GardenInterface doInBackground(Void... params) {
                // garden = buildGarden(new Garden());
                if (((RadioGroup) findViewById(R.id.radioGardenType)).getCheckedRadioButtonId() == findViewById(
                        R.id.radioGardenIncredibleEdible).getId()) {
                    garden.setIncredibleEdible(true);
                }
                garden = gardenManager.addGarden(garden);
                if (garden.isIncredibleEdible())
                    gardenManager.share(garden, "members", "ReadWrite");
                gardenManager.setCurrentGarden(garden);
                return garden;
            }

            protected void onPostExecute(GardenInterface result) {
                if (result == null)
                    Toast.makeText(getApplicationContext(),
                            "Error creating new garden, please verify your connection.", Toast.LENGTH_SHORT).show();
                else {
//                    sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));
//                    sendBroadcast(new Intent(BroadCastMessages.GARDEN_CURRENT_CHANGED));
                    ProfileCreationActivity.this.finish();
                }

            };
        }.execute();

        // SAMPLE GARDEN
        CheckBox samples = (CheckBox) findViewById(R.id.checkboxSamples);
        if (samples.isChecked()) {
            GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
            tracker.trackEvent("Garden", "sample", garden.getLocality(), 0);

            // Allotment
            BaseAllotmentInterface newAllotment = new Allotment();
            newAllotment.setName("" + new Random().nextInt());

            LocalAllotmentProvider helper = new LocalAllotmentProvider(this);
            helper.createAllotment(newAllotment);

            // Seed
            GotsSeedProvider seedHelper = new LocalSeedProvider(getApplicationContext());

            int nbSeed = seedHelper.getVendorSeeds(false, 0, 25).size();
            Random random = new Random();
            for (int i = 1; i <= 5 && i < nbSeed; i++) {
                int alea = random.nextInt(nbSeed);

                GrowingSeedInterface seed = (GrowingSeedInterface) seedHelper.getSeedById(alea % nbSeed + 1);
                if (seed != null) {
                    seed.setNbSachet(alea % 3 + 1);
                    seedHelper.updateSeed(seed);

                    GotsActionProvider actionHelper = GotsActionManager.getInstance().initIfNew(getApplicationContext());
                    BaseActionInterface bakering = actionHelper.getActionByName("beak");
                    GardeningActionInterface sowing = (GardeningActionInterface) actionHelper.getActionByName("sow");

                    sowing.execute(newAllotment, seed);

                    Calendar cal = new GregorianCalendar();
                    cal.setTime(Calendar.getInstance().getTime());
                    cal.add(Calendar.MONTH, -3);
                    seed.setDateSowing(cal.getTime());

                    GotsActionSeedProvider actionsHelper = GotsActionSeedManager.getInstance().initIfNew(this);
                    actionsHelper.insertAction(seed, bakering);
                }
            }
        }
        finish();

    }

    private void updateProfile() {
        if (!verifyForm())
            return;

        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... params) {

                // garden = buildGarden(gardenManager.getCurrentGarden());
                gardenManager.updateCurrentGarden(garden);
                return null;
            }

            protected void onPostExecute(Void result) {
                ProfileCreationActivity.this.finish();
                sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));
            };
        }.execute();

    }
}
