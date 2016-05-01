package org.gots.ui;

import android.os.Bundle;
import android.view.View;

import org.gots.seed.BaseSeed;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.fragment.BaseGotsListFragment;

import java.util.ArrayList;

public class FamilyListFragment extends BaseGotsListFragment {

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        listView.setAdapter(new VendorSeedListAdapter(getActivity(), new ArrayList<BaseSeed>()));
        super.onViewCreated(listView, savedInstanceState);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return seedProvider.getAllFamilies();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onListItemClicked(int i) {

    }

    @Override
    protected void doRefresh() {

    }

    @Override
    public void update() {

    }
}
