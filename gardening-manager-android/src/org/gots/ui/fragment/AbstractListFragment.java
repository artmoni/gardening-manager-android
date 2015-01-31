package org.gots.ui.fragment;

import org.gots.allotment.GotsAllotmentManager;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GotsGardenManager;
import org.gots.seed.GotsSeedManager;
import org.nuxeo.android.fragments.BaseListFragment;

import android.content.Intent;
import android.os.Bundle;

public abstract class AbstractListFragment extends BaseListFragment {
    protected GotsSeedManager seedProvider;

    protected GotsAllotmentManager allotmentManager;

    protected GotsGardenManager gardenManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        seedProvider = GotsSeedManager.getInstance();
        seedProvider.initIfNew(getActivity());
        allotmentManager = GotsAllotmentManager.getInstance();
        allotmentManager.initIfNew(getActivity());
        gardenManager = GotsGardenManager.getInstance();
        gardenManager.initIfNew(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
//        getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
//        if (isAdded())
//            getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onListItemClicked(int listItemPosition) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void doRefresh() {
        // TODO Auto-generated method stub
        
    }
}
