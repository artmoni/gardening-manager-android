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
import java.util.Calendar;
import java.util.List;

import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.provider.parrot.ParrotSeedProvider;
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

public class VendorListActivity extends AbstractListFragment {

    protected static final String FILTER_FAVORITES = "filter.favorites";

    protected static final String FILTER_THISMONTH = "filter.thismonth";

    protected static final String FILTER_BARCODE = "filter.barcode";

    protected static final String FILTER_VALUE = "filter.data";

    public static final String FILTER_PARROT = "filter.parrot";

    public Context mContext;

    public SeedListAdapter listVendorSeedAdapter;

    protected CharSequence currentFilter = "";

    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        mContext = getActivity();
        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
//        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.PROGRESS_FINISHED));
        listVendorSeedAdapter = new VendorSeedListAdapter(mContext, new ArrayList<BaseSeedInterface>());
        setListAdapter(listVendorSeedAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

  

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            
            updateVendorSeeds();
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        updateVendorSeeds();
        super.onResume();
    }

    protected synchronized void updateVendorSeeds() {
        new AsyncTask<Void, Integer, List<BaseSeedInterface>>() {

            protected void onPreExecute() {
                // dialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.gots_loading),
                // true);
                // dialog.setCanceledOnTouchOutside(true);
//                setActionRefresh(true);
                mContext.sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
                super.onPreExecute();
            };

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {

                Bundle args = getArguments();
                List<BaseSeedInterface> catalogue = new ArrayList<BaseSeedInterface>();
                if (args == null) {
                    catalogue = seedProvider.getVendorSeeds(false);
                    if (catalogue.size() == 0)
                        catalogue = seedProvider.getVendorSeeds(true);
                } else if (args.getBoolean(FILTER_FAVORITES))
                    // listVendorSeedAdapter.getFilter().filter("LIKE");
                    catalogue = seedProvider.getMyFavorites();
                else if (args.getBoolean(FILTER_BARCODE)) {
                    // listVendorSeedAdapter.getFilter().filter("LIKE");
                    catalogue.add(seedProvider.getSeedByBarCode(args.getString(FILTER_VALUE)));
                } else if (args.getBoolean(FILTER_THISMONTH))
                    // listVendorSeedAdapter.getFilter().filter("THISMONTH");
                catalogue = seedProvider.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
                else if (args.getBoolean(FILTER_PARROT)) {
                    ParrotSeedProvider parrotProvider = new ParrotSeedProvider(mContext);
                    catalogue = parrotProvider.getVendorSeeds(true);

                }

                return catalogue;
            }

            protected void onPostExecute(List<BaseSeedInterface> vendorSeeds) {

                listVendorSeedAdapter.setSeeds(vendorSeeds);
                listVendorSeedAdapter.getFilter().filter(currentFilter);
                // if (!"".equals(currentFilter) && currentFilter != null)
                // displaySearchBox();
                listVendorSeedAdapter.notifyDataSetChanged();
                // if (progressBar != null)
                //
                // progressBar.stopAnimatingBackground();
//                setActionRefresh(false);

                mContext.sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
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

    @Override
    public void onDestroy() {
        if (seedBroadcastReceiver != null)
            mContext.unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();
    }

}
