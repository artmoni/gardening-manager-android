package org.gots.sensor;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.sensor.parrot.ParrotLocation;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SensorListFragment extends ListFragment {

    private List<ParrotLocation> parrotLocations;

    private LocationListAdapter sensorListAdapter;

    public SensorListFragment() {
        this.parrotLocations = new ArrayList<ParrotLocation>();
    }

    public SensorListFragment(List<ParrotLocation> locations) {
        this.parrotLocations = locations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        sensorListAdapter = new LocationListAdapter(getActivity(), parrotLocations);
        setListAdapter(sensorListAdapter);
        return view;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        ft.replace(R.id.idFragmentSensor, new SensorChartFragment(sensorListAdapter.getItem(position)));
        ft.commit();
    }
}
