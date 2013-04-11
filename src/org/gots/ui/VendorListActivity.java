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
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.LocalSeedProvider;
import org.gots.seed.providers.nuxeo.NuxeoSeedProvider;
import org.gots.seed.providers.simple.SimpleSeedProvider;
import org.gots.seed.sql.VendorSeedDBHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();

//		seedProvider = new NuxeoSeedProvider(mContext);
//		new SeedUpdater().execute(seedProvider);
//		seedProvider = new LocalSeedProvider(mContext);
		seedProvider = new GotsSeedManager(mContext);
		listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, seedProvider.getAllSeeds());
		
		setListAdapter(listVendorSeedAdapter);
//		new SeedUpdater().execute(seedProvider);
		
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
//			new SeedUpdater().execute(seedProvider);
			listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, seedProvider.getAllSeeds());
			listVendorSeedAdapter.notifyDataSetChanged();
//			vendorSeeds = seedProvider.getAllSeeds();
//			listVendorSeedAdapter = new ListVendorSeedAdapter(getActivity(), vendorSeeds);
//			listVendorSeedAdapter.notifyDataSetChanged();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onResume() {
//		new SeedUpdater().execute(seedProvider);
		listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, seedProvider.getAllSeeds());
		setListAdapter(listVendorSeedAdapter);
		
		super.onResume();
	}

//	class SeedUpdater extends AsyncTask<GotsSeedProvider, Void, List<BaseSeedInterface>>{
//		private List<BaseSeedInterface> vendorSeeds;
//		private ProgressDialog dialog;
//
//		@Override
//		protected void onPreExecute() {
//			dialog = ProgressDialog.show(mContext, "", "Loading. Please wait...", true);
//			dialog.setCanceledOnTouchOutside(true);
//			dialog.show();
//			super.onPreExecute();
//		}
//		@Override
//		protected List<BaseSeedInterface> doInBackground(GotsSeedProvider... params) {
//			vendorSeeds = params[0].getAllSeeds();
//			return vendorSeeds;
//		}
//		@Override
//		protected void onPostExecute(List<BaseSeedInterface> result) {
//			Log.i("SeedUpdater",result.size()+" seeds updated");
//			listVendorSeedAdapter = new ListVendorSeedAdapter(mContext, result);
//			
//			setListAdapter(listVendorSeedAdapter);
//			dialog.dismiss();
//			super.onPostExecute(result);
//		}
//		@Override
//		protected void onCancelled() {
// 
//			super.onCancelled();
//		}
//	}
	
}
