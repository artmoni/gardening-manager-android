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

import java.util.List;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.service.SeedBroadcastReceiver;
import org.gots.seed.service.SeedUpdateService;
import org.gots.weather.service.WeatherUpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class VendorListActivity extends SherlockListFragment {

    public Context mContext;

    // GardenManager manager;
    private GotsSeedProvider seedProvider;

    private ListVendorSeedAdapter listVendorSeedAdapter;

    private Intent seedIntent;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        seedProvider = new GotsSeedManager(mContext, new LocalSeedProvider(mContext));
        seedIntent = new Intent(mContext, SeedUpdateService.class);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_catalogue, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {

        case R.id.refresh_seed:
            mContext.startService(seedIntent);

            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI(intent);
        }
    };

    @Override
    public void onResume() {

        List<BaseSeedInterface> vendorSeeds = seedProvider.getVendorSeeds();
        listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, vendorSeeds);
        if (vendorSeeds.size() < 1)
            mContext.startService(seedIntent);

        setListAdapter(listVendorSeedAdapter);
        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        super.onResume();
    }

    @Override
    public void onPause() {
        mContext.unregisterReceiver(seedBroadcastReceiver);
        mContext.stopService(seedIntent);

        super.onPause();
    }

    protected void updateUI(Intent intent) {
        boolean isnewseed = intent.getBooleanExtra(SeedUpdateService.ISNEWSEED, false);
        // if (isnewseed) {
        seedProvider = new GotsSeedManager(mContext);
        listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, seedProvider.getVendorSeeds());

        setListAdapter(listVendorSeedAdapter);
        // listVendorSeedAdapter.notifyDataSetChanged();
        // }
    }

}
