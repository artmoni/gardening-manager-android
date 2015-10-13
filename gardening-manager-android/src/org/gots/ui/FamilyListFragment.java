package org.gots.ui;

import android.os.Bundle;
import android.view.View;

import org.gots.exception.NotImplementedException;
import org.gots.seed.BaseSeed;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.fragment.AbstractListFragment;

import java.util.ArrayList;

public class FamilyListFragment extends AbstractListFragment {

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        getListView().setAdapter(new VendorSeedListAdapter(getActivity(), new ArrayList<BaseSeed>()));
        super.onViewCreated(getListView(), savedInstanceState);
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

}
