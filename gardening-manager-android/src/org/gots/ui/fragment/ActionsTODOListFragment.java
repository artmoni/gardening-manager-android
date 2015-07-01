package org.gots.ui.fragment;

import java.util.List;

import org.gots.action.ActionOnSeed;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeedInterface;

public class ActionsTODOListFragment extends AbstractListFragment {
    private boolean force = false;

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return actionseedProvider.getActionsToDo(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        getListView().setAdapter(
                new ListAllActionAdapter(getActivity(), (List<ActionOnSeed>) data, ListAllActionAdapter.STATUS_TODO));
    }
}
