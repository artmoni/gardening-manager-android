package org.gots.sensor.fragment;

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
import org.gots.ui.fragment.BaseGotsFragment;

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

public abstract class SensorResumeFragment extends BaseGotsFragment {

    private LinearLayout sensorListview;

    private Button button;

    private OnSensorClickListener mCallBack;

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
        onMenuButtonCreated(button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onMenuButtonClicked();
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    private class Holder {
        public List<ParrotLocation> parrotLocations;

        public List<ParrotLocationsStatus> parrotLocationsStatus;
    }

    @Override
    protected Holder retrieveNuxeoData() throws Exception {
        ParrotSensorProvider sensorProvider = new ParrotSensorProvider(getActivity());
        Holder holder = new Holder();
        holder.parrotLocations = sensorProvider.getLocations();
        holder.parrotLocationsStatus = sensorProvider.getStatus();
        return holder;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        Holder retrieveHolder = (Holder) data;
        sensorListview.removeAllViews();
        if (isAdded())
            onSensorStatusRetrieved(retrieveHolder.parrotLocations, retrieveHolder.parrotLocationsStatus);
        super.onNuxeoDataRetrieved(data);
    }

    protected ViewGroup getSensorView() {
        return sensorListview;
    }

    protected OnSensorClickListener getCallBackListener() {
        return mCallBack;
    }

    protected abstract void onSensorStatusRetrieved(List<ParrotLocation> parrotLocations,
            List<ParrotLocationsStatus> parrotLocationsStatus);

    protected abstract void onMenuButtonClicked();

    protected abstract void onMenuButtonCreated(Button button2);
}
