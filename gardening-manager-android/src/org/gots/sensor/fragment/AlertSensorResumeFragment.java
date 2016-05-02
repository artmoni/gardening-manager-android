package org.gots.sensor.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import org.gots.sensor.SensorLocationWidget;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.ui.SensorActivity;

import java.util.List;

public class AlertSensorResumeFragment extends SensorResumeFragment {
    @Override
    protected void onSensorStatusRetrieved(List<ParrotLocation> parrotLocations,
                                           List<ParrotLocationsStatus> parrotLocationsStatus) {
        for (ParrotLocationsStatus locationsStatus : parrotLocationsStatus) {
            ParrotLocation sensorLocation = null;

            for (ParrotLocation location : parrotLocations) {
                if (location.getLocation_identifier().equals(locationsStatus.getLocation_identifier())) {
                    sensorLocation = location;
                    break;
                }
            }

            if (sensorLocation == null)
                continue;

            if (locationsStatus.isWarning() || locationsStatus.isCritical()) {
                SensorLocationWidget sensorWidget = new SensorLocationWidget(getActivity());
                sensorWidget.setSensor(sensorLocation, locationsStatus);

                getSensorView().addView(sensorWidget);
                final ParrotLocation selectedLocation = sensorLocation;
                sensorWidget.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        getCallBackListener().OnSensorClick(selectedLocation);
                    }
                });
            }
        }
    }


    @Override
    protected void onMenuButtonCreated(Button button2) {
        button2.setText("All my sensors");
    }

    @Override
    protected void onMenuButtonClicked() {
        startActivity(new Intent(getActivity(), SensorActivity.class));
    }
}
