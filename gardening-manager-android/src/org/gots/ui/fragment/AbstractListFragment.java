package org.gots.ui.fragment;

import org.gots.allotment.AllotmentManager;
import org.gots.garden.GardenManager;
import org.gots.seed.GotsSeedManager;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class AbstractListFragment extends ListFragment {
    protected GotsSeedManager seedProvider;
    protected AllotmentManager allotmentManager;
    protected GardenManager gardenManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        seedProvider = GotsSeedManager.getInstance();
        seedProvider.initIfNew(getActivity());
        allotmentManager = AllotmentManager.getInstance();
        allotmentManager.initIfNew(getActivity());
        gardenManager=GardenManager.getInstance();
        gardenManager.initIfNew(getActivity());

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    
}
