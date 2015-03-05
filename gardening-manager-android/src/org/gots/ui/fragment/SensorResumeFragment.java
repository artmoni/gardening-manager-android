package org.gots.ui.fragment;

import java.util.List;

import org.gots.R;
import org.gots.sensor.LocationListAdapter;
import org.gots.sensor.SensorLocationWidget;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSensor;
import org.gots.sensor.parrot.ParrotSensorProvider;
import org.gots.ui.ProfileActivity;
import org.gots.ui.SensorActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SensorResumeFragment extends BaseGotsFragment {

    private LinearLayout sensorListview;

    private Button button;

    private List<ParrotLocation> mySensorLocations;

    private OnSensorClickListener mCallBack;

    // private List<ParrotSensor> mySensors;
    public interface OnSensorClickListener {
        public void OnSensorClick(ParrotLocation locationSensor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sensor_resume, null);
    }

    @Override
    public void onAttach(Activity activity) {
        if (activity instanceof OnSensorClickListener) {
            mCallBack = (OnSensorClickListener) activity;

        } else
            throw new ClassCastException(SensorResumeFragment.class.getSimpleName()
                    + " must implements OnSensorClickListener");
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sensorListview = (LinearLayout) view.findViewById(R.id.SensorListContainer);
        button = (Button) view.findViewById(R.id.buttonSensor);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SensorActivity.class));
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        ParrotSensorProvider sensorProvider = new ParrotSensorProvider(getActivity());
        mySensorLocations = sensorProvider.getLocations();
        // mySensors = sensorProvider.getSensors();
        return sensorProvider.getStatus();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<ParrotLocationsStatus> status = (List<ParrotLocationsStatus>) data;
        sensorListview.removeAllViews();
        for (ParrotLocationsStatus parrotLocationsStatus : status) {
            ParrotLocation sensorLocation = null;

            for (ParrotLocation location : mySensorLocations) {
                if (location.getLocation_identifier().equals(parrotLocationsStatus.getLocation_identifier())) {
                    sensorLocation = location;
                    break;
                    // sensorWidget.setSensor(location.get);
                }
            }

            if (sensorLocation == null)
                continue;

            if ("status_warning".equals(parrotLocationsStatus.getSoil_moisture().getStatus_key())
                    || "status_warning".equals(parrotLocationsStatus.getFertilizer().getStatus_key())
                    || "status_warning".equals(parrotLocationsStatus.getAir_temperature().getStatus_key())
                    || "status_warning".equals(parrotLocationsStatus.getLight().getStatus_key())
                    || "status_critical".equals(parrotLocationsStatus.getSoil_moisture().getStatus_key())
                    || "status_critical".equals(parrotLocationsStatus.getFertilizer().getStatus_key())
                    || "status_critical".equals(parrotLocationsStatus.getAir_temperature().getStatus_key())
                    || "status_critical".equals(parrotLocationsStatus.getLight().getStatus_key())) {
                SensorLocationWidget sensorWidget = new SensorLocationWidget(getActivity());
                sensorWidget.setSensor(sensorLocation, parrotLocationsStatus);

                sensorListview.addView(sensorWidget);
                final ParrotLocation selectedLocation = sensorLocation;
                sensorWidget.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mCallBack.OnSensorClick(selectedLocation);
                    }
                });
            }
            // for (ParrotSensor sensor : mySensors) {
            // if (sensor.getSensor_serial().equals(location.getSensor_serial())) {
            // break;
            // }
            // }
        }

        // final LocationListAdapter locationAdapter = new LocationListAdapter(getActivity(), locations);

        // sensorGallery.setAdapter(locationAdapter);
        // if (status != null) {
        // for (ParrotLocationsStatus locationsStatus : status) {
        // if (locationsStatus.getSoil_moisture().getInstruction_key() != null
        // && locationsStatus.getSoil_moisture().getInstruction_key().contains("low")
        // || locationsStatus.getFertilizer().getInstruction_key() != null
        // && locationsStatus.getFertilizer().getInstruction_key().contains("low"))
        // for (int i = 0; i < locations.size(); i++) {
        // if (locations.get(i).getLocation_identifier().equals(locationsStatus.getLocation_identifier())) {
        // if (sensorGallery.getChildAt(i) != null) {
        // sensorGallery.getChildAt(i).setBackgroundColor(
        // getResources().getColor(R.color.action_error_color));
        // }
        // break;
        // }
        // }
        // Log.d(getTag(), locationsStatus.getLocation_identifier() + "> getSoil_moisture="
        // + locationsStatus.getSoil_moisture().getInstruction_key() + " getFertilizer="
        // + locationsStatus.getFertilizer().getInstruction_key() + " getLight="
        // + locationsStatus.getLight().getInstruction_key() + " getAir_temperature="
        // + locationsStatus.getAir_temperature().getInstruction_key());
        // }
        // }

        super.onNuxeoDataRetrieved(data);
    }
}
