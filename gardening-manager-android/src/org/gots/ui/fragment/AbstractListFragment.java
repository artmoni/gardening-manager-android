package org.gots.ui.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import org.gots.R;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.garden.GotsGardenManager;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.nuxeo.android.fragments.BaseListFragment;


public abstract class AbstractListFragment extends BaseListFragment {
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
//        View view = inflater.inflate(R.layout.swipe_listview, container, false);
        listView = new ListView(getActivity());
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0)
//                    swipeRefreshLayout.setEnabled(true);
//                else
//                    swipeRefreshLayout.setEnabled(false);
            }
        });
        return listView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
//        swipeRefreshLayout.setOnRefreshListener(this);

    }

    protected AbsListView getListView() {
        return listView;
    }

    public void update() {
        runAsyncDataRetrieval();
    }



    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

}
