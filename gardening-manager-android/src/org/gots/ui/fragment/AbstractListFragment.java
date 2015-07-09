package org.gots.ui.fragment;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.garden.GotsGardenManager;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.nuxeo.android.fragments.BaseListFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;

public abstract class AbstractListFragment extends BaseListFragment implements ListView.OnScrollListener {
    protected GotsSeedManager seedProvider;

    protected GotsAllotmentManager allotmentManager;

    protected GotsGardenManager gardenManager;

    protected GotsActionSeedProvider actionseedProvider;

    protected GotsGrowingSeedManager growingSeedManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        seedProvider = GotsSeedManager.getInstance();
        seedProvider.initIfNew(getActivity());
        allotmentManager = GotsAllotmentManager.getInstance();
        allotmentManager.initIfNew(getActivity());
        gardenManager = GotsGardenManager.getInstance();
        gardenManager.initIfNew(getActivity());

        actionseedProvider = GotsActionSeedManager.getInstance().initIfNew(getActivity());
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = new ListView(getActivity());
        listView.setOnScrollListener(this);
        listView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        return listView;
    }

    @Override
    protected void onListItemClicked(int listItemPosition) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void doRefresh() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // int lastItem = firstVisibleItem + visibleItemCount - 1;
        // if (mReady) {
        // char firstLetter = mStrings[firstVisibleItem].charAt(0);
        //
        // if (!mShowing && firstLetter != mPrevLetter) {
        //
        // mShowing = true;
        // mDialogText.setVisibility(View.VISIBLE);
        //
        // }
        // mDialogText.setText(((Character) firstLetter).toString());
        // mHandler.removeCallbacks(mWindowRemover);
        // mHandler.postDelayed(mWindowRemover, 3000);
        // mPrevLetter = firstLetter;
        // }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub

    }

    protected AbsListView getListView() {
        return listView;
    }

    public void update(){
        runAsyncDataRetrieval();
    }

}
