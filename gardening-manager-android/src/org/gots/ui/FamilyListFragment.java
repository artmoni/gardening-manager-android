package org.gots.ui;

import java.util.ArrayList;

import org.gots.exception.NotImplementedException;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.fragment.AbstractListFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

public class FamilyListFragment extends AbstractListFragment {

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        listView = new ListView(getActivity());
        listView.setAdapter(new VendorSeedListAdapter(getActivity(), new ArrayList<BaseSeedInterface>()));
        super.onViewCreated(listView, savedInstanceState);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void runAsyncDataRetrieval() {
        try {
            seedProvider.getSpecies(true);
        } catch (NotImplementedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.runAsyncDataRetrieval();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        super.onNuxeoDataRetrieved(data);
    }

}
