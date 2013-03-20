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
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.nuxeo.NuxeoSeedProvider;
import org.gots.seed.sql.VendorSeedDBHelper;

import android.content.Context;
import android.content.Intent;
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
	private ListVendorSeedAdapter listVendorSeedAdapter;
	// GardenManager manager;
	private List<BaseSeedInterface> vendorSeeds;
	private GotsSeedProvider seedProvider;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();

		seedProvider = new NuxeoSeedProvider(mContext);
		vendorSeeds = seedProvider.getAllSeeds();

		// manager = new GardenManager(mContext);

		listVendorSeedAdapter = new ListVendorSeedAdapter(getActivity(), vendorSeeds);
		setListAdapter(listVendorSeedAdapter);

		// if (vendorSeeds.size() == 0)
		// manager.update();
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
			vendorSeeds = seedProvider.getAllSeeds();
			listVendorSeedAdapter = new ListVendorSeedAdapter(getActivity(), vendorSeeds);
			listVendorSeedAdapter.notifyDataSetChanged();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void update() {
		listVendorSeedAdapter.notifyDataSetChanged();
	}

}
