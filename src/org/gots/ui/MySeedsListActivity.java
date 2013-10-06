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
import java.util.List;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.MySeedsListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.fragment.AbstractListFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListAdapter;

public class MySeedsListActivity extends AbstractListFragment {
    protected static final String TAG = "MySeedsListActivity";

    public MySeedsListAdapter listAdapter;

    public BaseAllotmentInterface allotment;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent().getExtras() != null) {
            allotment = allotmentManager.getCurrentAllotment();
        }

        getActivity().registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
//        listAdapter = new MySeedsListAdapter(getActivity(), allotment, new ArrayList<BaseSeedInterface>());
        listAdapter = new MySeedsListAdapter(getActivity(), allotment, new ArrayList<BaseSeedInterface>());
        
        setListAdapter(listAdapter);
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            onResume();
        }
    };

    @Override
    public ListAdapter getListAdapter() {
        return super.getListAdapter();
    }

    public void update() {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        new AsyncTask<Void, Integer, List<BaseSeedInterface>>() {

            protected void onPreExecute() {
                // dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.gots_loading),
                // true);
                // dialog.setCanceledOnTouchOutside(true);
                // dialog.show();
                super.onPreExecute();
            };

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {

                List<BaseSeedInterface> mySeeds = new ArrayList<BaseSeedInterface>();

                mySeeds = seedProvider.getMyStock(gardenManager.getCurrentGarden());

                return mySeeds;
            }

            protected void onPostExecute(List<BaseSeedInterface> mySeeds) {
                listAdapter.setSeeds(mySeeds);
                listAdapter.notifyDataSetChanged();
                // if (dialog.isShowing())
                // dialog.dismiss();

                super.onPostExecute(mySeeds);
            };
        }.execute();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();
    }
    // @Override
    // protected void onResume() {
    // super.onResume();
    // listAdapter.notifyDataSetChanged();
    //
    // }
    //
    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // MenuInflater inflater = getMenuInflater();
    // // inflater.inflate(R.menu.menu_stock, menu);
    // return super.onCreateOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId()) {
    //
    // case R.id.new_seed_barcode:
    // IntentIntegrator integrator = new IntentIntegrator(this);
    // integrator.initiateScan();
    // return true;
    // case R.id.help:
    // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
    // Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
    // startActivity(browserIntent);
    //
    // return true;
    // default:
    // return super.onOptionsItemSelected(item);
    // }
    //
    // }
}
