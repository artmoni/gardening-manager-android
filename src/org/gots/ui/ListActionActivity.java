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
package org.gots.ui;

import java.util.ArrayList;

import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

public class ListActionActivity extends SherlockListFragment implements ListView.OnScrollListener {

    // private String[] mStrings;

    protected final class WindowRemover implements Runnable {
        public void run() {
            removeWindow();
        }

        protected void removeWindow() {
            if (mShowing) {
                mShowing = false;
                mDialogText.setVisibility(View.INVISIBLE);
            }
        }

    }

    private WindowRemover mWindowRemover = new WindowRemover();

    Handler mHandler = new Handler();

    private WindowManager mWindowManager;

    protected TextView mDialogText;

    protected boolean mShowing;

    private boolean mReady;

    private char mPrevLetter = Character.MIN_VALUE;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int seedid = 0;

        Bundle bundle = this.getArguments();
        seedid = bundle.getInt("org.gots.growingseed.id");

        // ActionSeedDBHelper helper = new ActionSeedDBHelper(this);
        // ArrayList<BaseActionInterface> actions = helper.getActionsToDo();
        // Arrays.sort(mStrings);

        // *******************************************
        mWindowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);

        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        new AsyncTask<Integer, Void, ArrayList<GrowingSeedInterface>>() {
            private ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
            private ListAllActionAdapter listAllActionAdapter;

            @Override
            protected ArrayList<GrowingSeedInterface> doInBackground(Integer... params) {
                GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(
                        getActivity());

                int seedid = params[0].intValue();
                if (seedid > 0) {
                    allSeeds.add(growingSeedManager.getGrowingSeedById(seedid));
                } else
                    allSeeds = growingSeedManager.getGrowingSeeds();
                listAllActionAdapter = new ListAllActionAdapter(getActivity(), allSeeds, ListAllActionAdapter.STATUS_DONE);
                return allSeeds;
            }

            protected void onPostExecute(ArrayList<GrowingSeedInterface> allSeeds) {
                setListAdapter(listAllActionAdapter);

            };
        }.execute(seedid);

        getListView().setOnScrollListener(ListActionActivity.this);

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

}
