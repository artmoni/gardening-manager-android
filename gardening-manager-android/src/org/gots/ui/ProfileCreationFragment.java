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
import java.util.Random;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GardeningActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.provider.local.LocalAllotmentProvider;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.garden.view.OnProfileEventListener;
import org.gots.seed.GrowingSeed;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.ui.fragment.BaseGotsFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ProfileCreationFragment extends BaseGotsFragment implements LocationListener, OnClickListener {
    public static final int OPTION_EDIT = 1;

    private static final String TAG = ProfileCreationFragment.class.getSimpleName();

    private LocationManager mlocManager;

    GardenInterface garden;

    private int mode = 0;

    private TextView editTextName;

    // private GoogleMap map;

    private GotsGardenManager gardenManager;

    private OnProfileEventListener mCallback;

    private TextView editTextLocality;

    private ImageView buttonLocalize;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profilecreation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (getArguments() != null)
            mode = getArguments().getInt("option");
        if (mode == OPTION_EDIT) {
            garden = getCurrentGarden();
        } else {
            garden = new Garden();
        }

        editTextName = (TextView) view.findViewById(R.id.editTextGardenName);
        editTextLocality = (TextView) view.findViewById(R.id.editTextGardenLocality);
        buttonLocalize = (ImageView) view.findViewById(R.id.imageViewLocalize);
        buttonLocalize.setOnClickListener(this);

        initProfileView();

    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnProfileEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProfileEventListener");
        }
        super.onAttach(activity);
    }

    private void initProfileView() {

        getView().findViewById(R.id.buttonValidatePosition).setOnClickListener(this);

        if (getCurrentGarden() != null)
            ((CheckBox) getView().findViewById(R.id.checkboxSamples)).setChecked(false);

        if (mode == OPTION_EDIT && getCurrentGarden() != null && getCurrentGarden().getLocality() != null) {
            editTextName.setText(getCurrentGarden().getName());
        }

        if (garden.getGpsLatitude() == 0 || garden.getGpsLongitude() == 0) {
            String locationProvider = LocationManager.NETWORK_PROVIDER;
            // Location lastKnownLocation = mlocManager.getLastKnownLocation(locationProvider);
            List<String> providers = mlocManager.getAllProviders();
            Location lastKnownLocation = null;

            for (int i = 0; i < providers.size(); i++) {

                lastKnownLocation = mlocManager.getLastKnownLocation(providers.get(i));
                if (lastKnownLocation != null) {
                    lastKnownLocation.getTime();
                    Log.d(TAG, "Provider: " + providers.get(i) + ", time=" + lastKnownLocation.getTime());
                }
                /*
                 * put your code here
                 * compare loc from providers to get the most
                 * recent location
                 */
            }

            if (lastKnownLocation != null) {
                setAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
            // else if (mlocManager.isProviderEnabled(locationProvider))
            // ;
            // mlocManager.requestLocationUpdates(locationProvider, 60000, 0, this);
            if ("".equals(garden.getLocality()))
                editTextLocality.setEnabled(false);
        } else {
            setAddressFromLocation(garden.getGpsLatitude(), garden.getGpsLongitude());
        }

    };

    private void getPosition(boolean force) {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        if (mlocManager == null)
            return;
        try {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 0, this);
            if (force) {
                String bestProvider = mlocManager.getBestProvider(criteria, true);
                mlocManager.requestLocationUpdates(bestProvider, 60000, 0, this);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

    }

    private void setAddressFromLocation(double latitude, double longitude) {

        Geocoder geo = new Geocoder(getActivity());
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
                editTextLocality.setText(garden.getLocality());
                editTextLocality.setEnabled(true);
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
        mlocManager.removeUpdates(this);
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
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        switch (status) {
        case LocationProvider.OUT_OF_SERVICE:
            Log.v(TAG, "Status Changed: Out of Service");
            Toast.makeText(getActivity(), "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
            break;
        case LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.v(TAG, "Status Changed: Temporarily Unavailable");
            Toast.makeText(getActivity(), "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
            break;
        case LocationProvider.AVAILABLE:
            Log.v(TAG, "Status Changed: Available");
            Toast.makeText(getActivity(), "Status Changed: Available", Toast.LENGTH_SHORT).show();
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
        case R.id.imageViewLocalize:
            getPosition(true);
        default:
            break;
        }
    }

    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    //
    // MenuInflater inflater = getMenuInflater();
    // inflater.inflate(R.menu.menu_profilecreation, menu);
    //
    // return super.onCreateOptionsMenu(menu);
    // }

    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // // Handle item selection
    // switch (item.getItemId()) {
    // case R.id.help:
    // Intent browserIntent = new Intent(getActivity(), WebHelpActivity.class);
    // browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
    // startActivity(browserIntent);
    // return true;
    // case R.id.localize_gaden:
    // getPosition(true);
    // buildProfile();
    // return true;
    //
    // default:
    // return super.onOptionsItemSelected(item);
    // }
    // }

    private boolean verifyForm() {
        garden.setName(editTextName.getText().toString());
        if (garden.getLocality() == null || "".equals(garden.getLocality())) {
            Toast.makeText(getActivity(), "Please localize your garden", Toast.LENGTH_LONG).show();
            buttonLocalize.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_from_bottom));
            return false;
        }
        if ("".equals(garden.getName())) {
            Toast.makeText(getActivity(), "Please name your garden", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= 16) {
                editTextName.setBackground(getResources().getDrawable(R.drawable.border_red));
            } else {
                editTextName.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_red));
            }

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
                if (((RadioGroup) getView().findViewById(R.id.radioGardenType)).getCheckedRadioButtonId() == getView().findViewById(
                        R.id.radioGardenIncredibleEdible).getId()) {
                    garden.setIncredibleEdible(true);
                }
                garden.setLocality(editTextLocality.getText().toString());
                garden = gardenManager.addGarden(garden);
                if (garden.isIncredibleEdible())
                    gardenManager.share(garden, "members", "ReadWrite");
                gardenManager.setCurrentGarden(garden);

                return garden;
            }

            protected void onPostExecute(GardenInterface result) {
                if (result == null)
                    Toast.makeText(getActivity(), "Error creating new garden, please verify your connection.",
                            Toast.LENGTH_SHORT).show();
                else {
                    // TODO ProfileCreationFragment.this.finish();
                    mCallback.onProfileCreated(garden);
                }

            };
        }.execute();

        // SAMPLE GARDEN
        CheckBox samples = (CheckBox) getView().findViewById(R.id.checkboxSamples);
        if (samples.isChecked()) {
            GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
            tracker.trackEvent("Garden", "sample", garden.getLocality(), 0);

            // Allotment
            BaseAllotmentInterface newAllotment = new Allotment();
            newAllotment.setName("" + new Random().nextInt());

            LocalAllotmentProvider helper = new LocalAllotmentProvider(getActivity());
            helper.createAllotment(newAllotment);

            // Seed
            GotsSeedProvider seedHelper = new LocalSeedProvider(getActivity());

            int nbSeed = seedHelper.getVendorSeeds(false, 0, 25).size();
            Random random = new Random();
            for (int i = 1; i <= 5 && i < nbSeed; i++) {
                int alea = random.nextInt(nbSeed);

                GrowingSeed seed = (GrowingSeed) seedHelper.getSeedById(alea % nbSeed + 1);
                if (seed != null) {
                    seed.setNbSachet(alea % 3 + 1);
                    seedHelper.updateSeed(seed);

                    GotsActionProvider actionHelper = GotsActionManager.getInstance().initIfNew(getActivity());
                    BaseAction bakering = actionHelper.getActionByName("beak");
                    GardeningActionInterface sowing = (GardeningActionInterface) actionHelper.getActionByName("sow");

                    sowing.execute(newAllotment, seed);

                    Calendar cal = new GregorianCalendar();
                    cal.setTime(Calendar.getInstance().getTime());
                    cal.add(Calendar.MONTH, -3);
                    seed.setDateSowing(cal.getTime());

                    GotsActionSeedProvider actionsHelper = GotsActionSeedManager.getInstance().initIfNew(getActivity());
                    actionsHelper.insertAction(seed, (ActionOnSeed) bakering);
                }
            }
        }
        // TODO Close finish();

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
                // ProfileCreationFragment.this.finish();
                // TODO finish
            };
        }.execute();

    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        // TODO Auto-generated method stub
        return false;
    }

    public void updateGarden(GardenInterface garden) {
        this.garden = garden;
        initProfileView();
    }
}
