/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.ui.fragment;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GardeningActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.allotment.provider.local.LocalAllotmentProvider;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.garden.view.OnProfileEventListener;
import org.gots.seed.BaseSeed;
import org.gots.seed.GrowingSeed;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.weather.WeatherManager;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class ProfileEditorFragment extends BaseGotsFragment implements LocationListener, OnClickListener {
    public static final int OPTION_EDIT = 1;

    private static final String TAG = ProfileEditorFragment.class.getSimpleName();
    public static final String PROFILE_EDITOR_MODE = "option";

    private LocationManager mlocManager;

    GardenInterface garden;

    private int mode = 0;

    private TextView editTextName;

    private GotsGardenManager gardenManager;

    private OnProfileEventListener mCallback;

//    private TextView editTextLocality;

//    private ActionWidget buttonLocalize;

    private TextView editTextWeatherLocality;

    private ActionWidget buttonWeatherState;

    private View buttonValidate;
    private int nbAllotments = 0;
    private TextView textViewNbAllotments;

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
            mode = getArguments().getInt(PROFILE_EDITOR_MODE);
        if (mode == OPTION_EDIT) {
            garden = getCurrentGarden();
        } else {
            garden = new Garden();
        }

        textViewNbAllotments = (TextView) view.findViewById(R.id.textViewNbAllotments);

        editTextName = (TextView) view.findViewById(R.id.editTextGardenName);
        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                garden.setName(charSequence.toString());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

//        editTextLocality = (TextView) view.findViewById(R.id.editTextGardenLocality );
//        editTextLocality.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                garden.setLocality(charSequence.toString());
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        editTextWeatherLocality = (TextView) view.findViewById(R.id.editTextGardenWeatherLocality);

//        buttonLocalize = (ActionWidget) view.findViewById(R.id.imageViewLocalize);
//        buttonLocalize.setActionImage(R.drawable.bt_localize_garden);
//        buttonLocalize.setOnClickListener(this);
        buttonWeatherState = (ActionWidget) view.findViewById(R.id.imageViewWeatherState);
        buttonWeatherState.setOnClickListener(this);
        buttonWeatherState.setActionImage(R.drawable.weather_disconnected);

        buttonValidate = view.findViewById(R.id.buttonValidatePosition);
        buttonValidate.setOnClickListener(this);

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

        if (getCurrentGarden() != null)
            ((CheckBox) getView().findViewById(R.id.checkboxSamples)).setChecked(false);

        if (mode == OPTION_EDIT && getCurrentGarden() != null && getCurrentGarden().getLocality() != null) {
            editTextName.setText(getCurrentGarden().getName());
        }

        if (garden.getGpsLatitude() == 0 || garden.getGpsLongitude() == 0) {
            List<String> providers = mlocManager.getAllProviders();
            Location lastKnownLocation = null;

            for (int i = 0; i < providers.size(); i++) {

                lastKnownLocation = mlocManager.getLastKnownLocation(providers.get(i));
                if (lastKnownLocation != null && lastKnownLocation.getLatitude() != 0
                        && lastKnownLocation.getLongitude() != 0) {
                    Log.d(TAG, "Provider: " + providers.get(i) + ", time=" + lastKnownLocation.getTime());
                    break;
                }
            }

            if (lastKnownLocation != null) {
                setAddressFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
            }
//            if ("".equals(garden.getLocality()))
//                editTextLocality.setEnabled(false);
        } else {
            fetchWeatherAsync();
            // setAddressFromLocation(garden.getGpsLatitude(), garden.getGpsLongitude());
//            editTextLocality.setEnabled(true);
        }

//        editTextLocality.setText(garden.getLocality());


    }

    ;

    private void getPosition(boolean force) {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        buttonWeatherState.setActionImage(R.drawable.bt_update);
        Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        buttonWeatherState.startAnimation(rotation);

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
        if (!isAdded())
            return;
        Geocoder geo = new Geocoder(getActivity());
        try {
            List<Address> adresses = geo.getFromLocation(latitude, longitude, 1);
            if (adresses != null && adresses.size() == 1) {
                Address address = adresses.get(0);
                garden.setGpsLatitude(latitude);
                garden.setGpsLongitude(longitude);
                garden.setLocality(address.getLocality().replaceAll("\\s+$", ""));//remove space,tab,new line at the end
                garden.setAdminArea(address.getAdminArea());
                garden.setCountryName(address.getCountryName());
                garden.setCountryCode(address.getCountryCode());
                // force forecast locality when geolocalized
                garden.setLocalityForecast(address.getLocality());
                buttonWeatherState.setState(ActionState.NORMAL);
                fetchWeatherAsync();
            } else {
                buttonWeatherState.setState(ActionState.CRITICAL);
                // sinon on affiche un message d'erreur
                // ((TextView) findViewById(R.id.editTextLocality)).setHint(getResources().getString(
                // R.string.location_notfound));
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void fetchWeatherAsync() {
        if (isAdded())
            new AsyncTask<Void, Void, Short>() {

                protected void onPreExecute() {
//                    garden.setLocality(editTextWeatherLocality.getText().toString());

                    buttonWeatherState.setActionImage(R.drawable.bt_update);
                    Animation rotation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                    rotation.setRepeatCount(Animation.INFINITE);
                    buttonWeatherState.startAnimation(rotation);
                }

                ;

                @Override
                protected Short doInBackground(Void... params) {
                    WeatherManager weatherManager = new WeatherManager(getActivity());
                    return weatherManager.fetchWeatherForecast(garden);
                }

                protected void onPostExecute(Short result) {
                    buttonWeatherState.clearAnimation();
                    if (result.shortValue() == WeatherManager.WEATHER_OK) {
                        buttonWeatherState.setActionImage(R.drawable.weather_connected);
                        buttonWeatherState.setState(ActionState.NORMAL);

                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(garden.getLocality());
                        if (garden.getAdminArea()!= null) {
                            stringBuilder.append(", ");
                            stringBuilder.append(garden.getAdminArea());
                        }
                        editTextWeatherLocality.setText(stringBuilder.toString());
                    } else {
                        buttonWeatherState.setActionImage(R.drawable.weather_disconnected);
                        buttonWeatherState.setState(ActionState.CRITICAL);
                    }
                    buttonWeatherState.invalidate();
                }

                ;
            }.execute();
    }

    @Override
    public void onLocationChanged(Location location) {
        setAddressFromLocation(location.getLatitude(), location.getLongitude());
        mlocManager.removeUpdates(this);

        buttonWeatherState.clearAnimation();
        buttonWeatherState.setActionImage(R.drawable.bt_localize_garden);
    }

    @Override
    public void onPause() {
        mlocManager.removeUpdates(this);
        super.onPause();
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
//            case R.id.imageViewLocalize:
//
//                break;
            case R.id.imageViewWeatherState:
                if (garden.getGpsLongitude() == 0 | garden.getGpsLatitude() == 0)
                    getPosition(true);
                else
                    fetchWeatherAsync();
                break;
            default:
                break;
        }
    }

    private boolean verifyForm() {
        garden.setName(editTextName.getText().toString());
        garden.setLocalityForecast(editTextWeatherLocality.getText().toString());
        if (garden.getLocality() == null || "".equals(garden.getLocality())) {
            Toast.makeText(getActivity(), "Please localize your garden", Toast.LENGTH_LONG).show();
//            buttonWeatherState.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_from_bottom));
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
//                garden.setLocality(editTextLocality.getText().toString());
                garden.setLocalityForecast(editTextWeatherLocality.getText().toString());
                garden = gardenManager.addGarden(garden);
                if (garden.isIncredibleEdible())
                    gardenManager.share(garden, "members", "ReadWrite");
                //gardenManager.setCurrentGarden(garden);

                return garden;
            }

            protected void onPostExecute(GardenInterface result) {
                if (result == null)
                    Toast.makeText(getActivity(), "Error creating new garden, please verify your connection.",
                            Toast.LENGTH_SHORT).show();
                else {
                    mCallback.onProfileCreated(result);
                }

            }

            ;
        }.execute();

    }

    private void updateProfile() {
        if (!verifyForm())
            return;

        new AsyncTask<String, Integer, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                gardenManager.updateCurrentGarden(garden);
                return null;
            }

            protected void onPostExecute(Void result) {
                mCallback.onProfileSelected(garden);
            }

            ;
        }.execute();

    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        GotsAllotmentManager gotsAllotmentManager = GotsAllotmentManager.getInstance();
        nbAllotments = gotsAllotmentManager.getMyAllotments(true).size();

        return super.retrieveNuxeoData();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        textViewNbAllotments.setText(String.valueOf(nbAllotments));
        super.onNuxeoDataRetrieved(data);
    }

    public void updateGarden(GardenInterface garden) {
        this.garden = garden;
        initProfileView();
    }
}
