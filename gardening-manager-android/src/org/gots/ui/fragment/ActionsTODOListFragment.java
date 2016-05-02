package org.gots.ui.fragment;

import android.os.Bundle;

import org.gots.action.ActionOnSeed;
import org.gots.action.adapter.ListAllActionAdapter;

import java.util.List;

public class ActionsTODOListFragment extends BaseGotsListFragment {
    private boolean force = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void update() {

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return actionseedProvider.getActionsToDo(force);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        listView.setAdapter(
                new ListAllActionAdapter(getActivity(), (List<ActionOnSeed>) data, ListAllActionAdapter.STATUS_TODO));
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onListItemClicked(int i) {

    }

    @Override
    protected void doRefresh() {

    }
}
