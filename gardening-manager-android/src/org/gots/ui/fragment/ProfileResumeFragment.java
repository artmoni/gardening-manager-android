/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
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
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.gots.R;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.garden.view.OnProfileEventListener;
import org.gots.ui.view.MyTextView;
import org.gots.weather.WeatherManager;

import java.io.IOException;
import java.util.List;

public class ProfileResumeFragment extends BaseGotsFragment implements LocationListener, OnClickListener {
    public static final int OPTION_EDIT = 1;
    public static final String PROFILE_EDITOR_MODE = "option";
    private static final String TAG = ProfileResumeFragment.class.getSimpleName();
    GardenInterface garden;
    private LocationManager mlocManager;
    private int mode = 0;

    private MyTextView textGardenName;

    private GotsGardenManager gardenManager;

    private OnProfileEventListener mCallback;


    private TextView textWeatherLocality;

    private FloatingActionButton buttonWeatherState;

    private int nbAllotments = 0;
    private TextView textViewNbAllotments;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profile_resume, container, false);
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

        textGardenName = (MyTextView) view.findViewById(R.id.myTextViewGardenName);
        textGardenName.addTextChangedListener(new TextWatcher() {
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

        textWeatherLocality = (TextView) view.findViewById(R.id.editTextGardenWeatherLocality);

        buttonWeatherState = (FloatingActionButton) view.findViewById(R.id.imageViewWeatherState);
        buttonWeatherState.setOnClickListener(this);
        buttonWeatherState.setImageResource(R.drawable.weather_disconnected);
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


        if (mode == OPTION_EDIT) {
            textGardenName.setText(garden.getName());
        }

        textViewNbAllotments.setText(String.valueOf(nbAllotments));

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
                fetchWeatherAsync();
            }
        } else {
            fetchWeatherAsync();
        }


    }

    ;

    private void getPosition(boolean force) {

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        buttonWeatherState.setImageResource(R.drawable.bt_update);
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
                buttonWeatherState.setColorNormal(getResources().getColor(R.color.green_light));
                fetchWeatherAsync();
            } else {
                buttonWeatherState.setColorNormal(getResources().getColor(R.color.action_warning_color));
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
//                    garden.setLocality(textWeatherLocality.getText().toString());

                    buttonWeatherState.setImageResource(R.drawable.bt_update);
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
                        buttonWeatherState.setImageResource(R.drawable.weather_connected);
                        buttonWeatherState.setColorNormal(getResources().getColor(R.color.green_light));
                        textWeatherLocality.setText(garden.getAddress().toString());
                    } else {
                        buttonWeatherState.setImageResource(R.drawable.weather_disconnected);
                        buttonWeatherState.setColorNormal(getResources().getColor(R.color.action_warning_color));
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
        buttonWeatherState.setImageResource(R.drawable.bt_localize_garden);
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
        nbAllotments = gotsAllotmentManager.getMyAllotments(false).size();

        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        initProfileView();
        super.onNuxeoDataRetrieved(data);
    }

    public void updateGarden(GardenInterface garden) {
        this.garden = garden;
        initProfileView();
    }
}
