package org.gots.sensor;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.sensor.parrot.ParrotLocation;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SensorListFragment extends ListFragment {

    private List<ParrotLocation> parrotLocations;

    public SensorListFragment() {
        this.parrotLocations = new ArrayList<ParrotLocation>();
    }

    public SensorListFragment(List<ParrotLocation> locations) {
        this.parrotLocations = locations;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocationListAdapter sensorListAdapter = new LocationListAdapter(getActivity(), parrotLocations);
        setListAdapter(sensorListAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.idFragmentSensor, new SensorFragment());
        ft.commit();
    }
}
