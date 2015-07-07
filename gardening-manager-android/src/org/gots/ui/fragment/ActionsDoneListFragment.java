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
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class ActionsDoneListFragment extends AbstractListFragment {

    public static final String ORG_GOTS_GROWINGSEED_ID = "org.gots.growingseed.id";

    Handler mHandler = new Handler();

    protected TextView mDialogText;

    protected boolean mShowing;

    private ListAllActionAdapter listAllActionAdapter;

    int seedid = -1;

    boolean force_sync = false;

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        if (bundle != null)
            seedid = bundle.getInt(ORG_GOTS_GROWINGSEED_ID);
        super.onViewCreated(v, savedInstanceState);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        if (seedid == -1)
            return null;

        GrowingSeed seed = growingSeedManager.getGrowingSeedById(seedid);

        List<ActionOnSeed> seedActions = new ArrayList<ActionOnSeed>();
        seedActions = actionseedProvider.getActionsDoneBySeed(seed, force_sync);
        seedActions.addAll(actionseedProvider.getActionsToDoBySeed(seed, force_sync));

        force_sync = false;
        return seedActions;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        listAllActionAdapter = new ListAllActionAdapter(getActivity(), (List<ActionOnSeed>) data,
                ListAllActionAdapter.STATUS_DONE);
        getListView().setAdapter(listAllActionAdapter);
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
