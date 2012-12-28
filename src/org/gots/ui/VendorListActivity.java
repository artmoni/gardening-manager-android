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
import java.util.Random;

import org.gots.R;
import org.gots.allotment.sql.AllotmentDBHelper;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.garden.GardenManager;
import org.gots.help.HelpUriBuilder;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.sql.VendorSeedDBHelper;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class VendorListActivity extends SherlockListFragment {

	public Context mContext;
	private ListVendorSeedAdapter listVendorSeedAdapter;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();

		VendorSeedDBHelper myBank = new VendorSeedDBHelper(getActivity());
		ArrayList<BaseSeedInterface> vendorSeeds;
		vendorSeeds = myBank.getVendorSeeds();	
		
		

		listVendorSeedAdapter = new ListVendorSeedAdapter(getActivity(), vendorSeeds);
		setListAdapter(listVendorSeedAdapter);
		
		if (vendorSeeds.size()==0)
			new RefreshTask().execute(new Object());
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
			new RefreshTask().execute(new Object());
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

public void update (){
	listVendorSeedAdapter.notifyDataSetChanged();
}
	

	private class RefreshTask extends AsyncTask<Object, Integer, Long> {
		@Override
		protected Long doInBackground(Object... params) {

			GardenManager garden = new GardenManager(mContext);
			garden.update(); 
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
			ArrayList<BaseSeedInterface> vendorSeeds;
			vendorSeeds = myBank.getVendorSeeds();

			setListAdapter(new ListVendorSeedAdapter(mContext, vendorSeeds));
			super.onPostExecute(result);
		}
	}

}
