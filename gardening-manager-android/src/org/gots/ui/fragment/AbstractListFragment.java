package org.gots.ui.fragment;

import org.gots.allotment.AllotmentManager;
import org.gots.garden.GardenManager;
import org.gots.seed.GotsSeedManager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AbstractListFragment extends Fragment {
    protected GotsSeedManager seedProvider;

    protected AllotmentManager allotmentManager;

    protected GardenManager gardenManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        seedProvider = GotsSeedManager.getInstance();
        seedProvider.initIfNew(getActivity());
        allotmentManager = AllotmentManager.getInstance();
        allotmentManager.initIfNew(getActivity());
        gardenManager = GardenManager.getInstance();
        gardenManager.initIfNew(getActivity());
        super.onCreate(savedInstanceState);
    }


}
