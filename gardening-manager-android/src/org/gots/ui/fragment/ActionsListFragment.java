/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.gots.action.ActionOnSeed;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

public class ActionsListFragment extends AbstractListFragment implements ListView.OnScrollListener {

    Handler mHandler = new Handler();

    protected TextView mDialogText;

    protected boolean mShowing;

    private ArrayList<GrowingSeed> allSeeds = new ArrayList<GrowingSeed>();

    private ListAllActionAdapter listAllActionAdapter;

    int seedid = 0;

    private GotsGrowingSeedManager growingSeedManager;

    private GotsActionSeedProvider actionseedProvider;

    boolean force_sync = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listView = new ListView(getActivity());
        force_sync = true;
        return listView;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        seedid = bundle.getInt("org.gots.growingseed.id");
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());
        actionseedProvider = GotsActionSeedManager.getInstance().initIfNew(getActivity());
        super.onViewCreated(v, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // new AsyncTask<Integer, Void, ArrayList<GrowingSeed>>() {
        //
        // @Override
        // protected ArrayList<GrowingSeed> doInBackground(Integer... params) {
        // GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(
        // getActivity());
        //
        // int seedid = params[0].intValue();
        // if (seedid > 0) {
        // allSeeds.add(growingSeedManager.getGrowingSeedById(seedid));
        // } else
        // allSeeds = growingSeedManager.getGrowingSeeds();
        // GotsActionSeedProvider actionseedProvider = GotsActionSeedManager.getInstance().initIfNew(getActivity());
        //
        // List<ActionOnSeed> seedActions = new ArrayList<ActionOnSeed>();
        // for (GrowingSeed seed : allSeeds) {
        //
        // seedActions = actionseedProvider.getActionsDoneBySeed(seed, true);
        // seedActions.addAll(actionseedProvider.getActionsToDoBySeed(seed, true));
        //
        // }
        // listAllActionAdapter = new ListAllActionAdapter(getActivity(), seedActions,
        // ListAllActionAdapter.STATUS_DONE);
        // return allSeeds;
        // }
        //
        // protected void onPostExecute(ArrayList<GrowingSeed> allSeeds) {
        // listView.setAdapter(listAllActionAdapter);
        //
        // };
        // }.execute(seedid);

        listView.setOnScrollListener(ActionsListFragment.this);

    }

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

    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        allSeeds.clear();
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        if (seedid > 0) {
            allSeeds.add(growingSeedManager.getGrowingSeedById(seedid));
        } else
            allSeeds = growingSeedManager.getGrowingSeeds();

        List<ActionOnSeed> seedActions = new ArrayList<ActionOnSeed>();
        for (GrowingSeed seed : allSeeds) {

            seedActions = actionseedProvider.getActionsDoneBySeed(seed, force_sync);
            seedActions.addAll(actionseedProvider.getActionsToDoBySeed(seed, force_sync));

        }
        force_sync = false;
        return seedActions;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        listAllActionAdapter = new ListAllActionAdapter(getActivity(), (List<ActionOnSeed>) data,
                ListAllActionAdapter.STATUS_DONE);
        listView.setAdapter(listAllActionAdapter);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        Log.e(getTag(), "Error retrieving actions list");
        super.onNuxeoDataRetrieveFailed();
    }

    public void update() {
        force_sync = true;
        runAsyncDataRetrieval();
    }
}
