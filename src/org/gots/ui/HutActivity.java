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
import org.gots.analytics.GotsAnalytics;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.garden.GardenManager;
import org.gots.help.HelpUriBuilder;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.adapter.MySeedsListAdapter;
import org.gots.seed.sql.VendorSeedDBHelper;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HutActivity extends SherlockActivity implements ActionBar.TabListener {

	// private ListVendorSeedAdapter lvsea;
	ListView listSeeds;
	ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
	// TabHost tabHost;
	private Context mContext;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(false);

		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		// GardenManager gm =GardenManager.getInstance();
		mContext = this;
		setContentView(R.layout.hut);
		buildMyTabHost();

		
		

	}

	private void buildMyTabHost() {
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// tabHost = getTabHost(); // The activity TabHost

		// ********************** Tab myseeds**********************
		ActionBar.Tab tab = getSupportActionBar().newTab();
		tab.setText(getResources().getString(R.string.hut_menu_myseeds).toUpperCase());
		tab.setTabListener(this);
		getSupportActionBar().addTab(tab);

		// View myseedstitle =
		// LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
		// TextView tv = (TextView) myseedstitle.findViewById(R.id.tabsText);
		// tv.setText(getResources().getString(R.string.hut_menu_myseeds).toUpperCase());
		// TabSpec myseedsspec =
		// tabHost.newTabSpec("detail").setIndicator(myseedstitle);

		VendorSeedDBHelper myBank = new VendorSeedDBHelper(this);
		ArrayList<BaseSeedInterface> mySeeds = myBank.getMySeeds();
		Intent intent;

		intent = new Intent().setClass(this, MySeedsListActivity.class);
		// myseedsspec.setContent(intent);
		// tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		// tabHost.addTab(myseedsspec);

		// ********************** Tab commercial seed**********************
		tab = getSupportActionBar().newTab();
		tab.setText(getResources().getString(R.string.hut_menu_vendorseeds).toUpperCase());
		tab.setTabListener(this);
		getSupportActionBar().addTab(tab);

		// View vendorseedstitle =
		// LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
		// tv = (TextView) vendorseedstitle.findViewById(R.id.tabsText);
		// tv.setText(getResources().getString(R.string.hut_menu_vendorseeds).toUpperCase());
		// TabSpec vendorspec =
		// tabHost.newTabSpec("vendorseeds").setIndicator(vendorseedstitle);
		// intent = new Intent().setClass(this, VendorListActivity.class);
		// vendorspec.setContent(intent);
		// tabHost.addTab(vendorspec);
		//
		// if (mySeeds.size() == 0) {
		// getTabHost().setCurrentTabByTag("vendorseeds");
		//
		// final String classname = getClass().getSimpleName();
		// new
		// AlertDialog.Builder(this).setIcon(R.drawable.help).setTitle(R.string.menu_help_firstlaunch)
		// .setPositiveButton(R.string.button_ok, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		//
		// Intent browserIntent = new Intent(Intent.ACTION_VIEW,
		// Uri.parse(HelpUriBuilder
		// .getUri(classname)));
		// startActivity(browserIntent);
		// }
		// }).setNegativeButton(R.string.button_cancel, new
		// DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int whichButton) {
		//
		// /* User clicked Cancel so do some stuff */
		// }
		// }).show();
		// }
		LinearLayout dashboardButton = (LinearLayout) findViewById(R.id.btReturn);
		dashboardButton.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// GardenFactory gf = new GardenFactory(this);
		// gf.saveGarden(DashboardActivity.myGarden);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
		if (scanResult != null && scanResult.getContents() != "") {
			Log.i("Scan result", scanResult.toString());
			VendorSeedDBHelper helper = new VendorSeedDBHelper(this);
			BaseSeedInterface scanSeed = helper.getSeedByBarCode(scanResult.getContents());
			if (scanSeed != null) {
				scanSeed.setNbSachet(scanSeed.getNbSachet() + 1);
				helper.updateSeed(scanSeed);
				buildMyTabHost();
			}
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		if (tab.getPosition()==0){
			BaseAllotmentInterface allotment = null;
			if (getIntent().getExtras() != null) {
				String allotmentRef = getIntent().getExtras().getString("org.gots.allotment.reference");
				if (allotmentRef != null) {
					AllotmentDBHelper helper = new AllotmentDBHelper(this);
					allotment = helper.getAllotmentByName(allotmentRef);
				}
			}
			VendorSeedDBHelper myBank = new VendorSeedDBHelper(this);
			ArrayList<BaseSeedInterface> mySeeds = myBank.getMySeeds();

			listView = (ListView) findViewById(R.id.listSeed);
			MySeedsListAdapter listAdapter = new MySeedsListAdapter(this, allotment, mySeeds);
			listView.setAdapter(listAdapter);
		}
		else if (tab.getPosition()==1){
		VendorSeedDBHelper myBank = new VendorSeedDBHelper(this);
		ArrayList<BaseSeedInterface> vendorSeeds;
		vendorSeeds = myBank.getVendorSeeds();
		listView = (ListView) findViewById(R.id.listSeed);
		listView.setAdapter(new ListVendorSeedAdapter(this, vendorSeeds));
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_catalogue, menu);
		return true;
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

		case android.R.id.home:
			finish();
			return true;
		case R.id.new_allotment:
			BaseAllotmentInterface newAllotment = new Allotment();
			newAllotment.setName("" + new Random().nextInt());

			AllotmentDBHelper helper = new AllotmentDBHelper(this);
			helper.insertAllotment(newAllotment);
			// lsa.notifyDataSetChanged();

			// if (listAllotments.getCount() == 0)
			// listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.help_hut));
			// else
			// listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_simple));
			// return true;
		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass()
					.getSimpleName())));
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

			listView.setAdapter(new ListVendorSeedAdapter(mContext, vendorSeeds));
			super.onPostExecute(result);
		}
	}
}
