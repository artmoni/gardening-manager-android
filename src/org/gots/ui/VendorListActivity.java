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

import org.gots.R;
import org.gots.garden.GardenManager;
import org.gots.help.HelpUriBuilder;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.sql.VendorSeedDBHelper;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class VendorListActivity extends ListActivity {

	public Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		VendorSeedDBHelper myBank = new VendorSeedDBHelper(this);
		ArrayList<BaseSeedInterface> vendorSeeds;
		vendorSeeds = myBank.getVendorSeeds();

		setListAdapter(new ListVendorSeedAdapter(this, vendorSeeds));
		mContext = this;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.menu_catalogue, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Intent i;
		switch (item.getItemId()) {
		case R.id.new_seed:
			i = new Intent(this, NewSeedActivity.class);
			startActivity(i);
			return true;

		case R.id.refresh_seed:
			new RefreshTask().execute(new Object());
			return true;
		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
			startActivity(browserIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class RefreshTask extends AsyncTask<Object, Integer, Long> {
		@Override
		protected Long doInBackground(Object... params) {

			GardenManager garden = new GardenManager(mContext);
			garden.refreshData();
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
