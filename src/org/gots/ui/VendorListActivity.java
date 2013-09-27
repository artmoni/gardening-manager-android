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

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.service.SeedUpdateService;
import org.gots.ui.fragment.AbstractListFragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class VendorListActivity extends AbstractListFragment {

    public Context mContext;

    public ListVendorSeedAdapter listVendorSeedAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mContext = getActivity();
        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, new ArrayList<BaseSeedInterface>());
        setListAdapter(listVendorSeedAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_catalogue, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

        case R.id.refresh_seed:
            Intent seedIntent = new Intent(mContext, SeedUpdateService.class);
            mContext.startService(seedIntent);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // updateUI(intent);
            
           
            updateVendorSeeds();

        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateVendorSeeds();
    }

    private ProgressDialog dialog;

    protected void updateVendorSeeds() {
        new AsyncTask<Void, Integer, List<BaseSeedInterface>>() {

            protected void onPreExecute() {
                try {
                    dialog.dismiss();
                    dialog = null;
                } catch (Exception e) {
                    // nothing
                }
                dialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.gots_loading),
                        true);
                dialog.setCanceledOnTouchOutside(true);
                // dialog.show();
                // if (vendorSeeds.size() < 1)
                // mContext.startService(seedIntent);
                super.onPreExecute();
            };

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {
                return seedProvider.getVendorSeeds(false);
            }

            protected void onPostExecute(List<BaseSeedInterface> vendorSeeds) {
                try {
                    dialog.dismiss();
                    dialog = null;
                } catch (Exception e) {
                    // nothing
                }
                listVendorSeedAdapter.setSeeds(vendorSeeds);
                listVendorSeedAdapter.notifyDataSetChanged();

                super.onPostExecute(vendorSeeds);
            };
        }.execute();
    }

    @Override
    public void onPause() {
        try {
            dialog.dismiss();
            dialog = null;
        } catch (Exception e) {
            // nothing
        }
        super.onPause();
    }

    // protected void updateUI(Intent intent) {
    // // boolean isnewseed = intent.getBooleanExtra(SeedUpdateService.ISNEWSEED, false);
    // // if (isnewseed) {
    // // seedProvider = new GotsSeedManager(mContext);
    // // listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, seedProvider.getVendorSeeds());
    // //
    // // setListAdapter(listVendorSeedAdapter);
    // // listVendorSeedAdapter.notifyDataSetChanged();
    // // }
    // onResume();
    // }

    @Override
    public void onDestroy() {
        mContext.unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();
    }
}
