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
import org.gots.analytics.GotsAnalytics;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.VendorSeedDBHelper;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HutActivity extends TabActivity implements OnTabChangeListener {

	// private ListVendorSeedAdapter lvsea;
	ListView listSeeds;
	ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
	TabHost tabHost;
	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		// GardenManager gm =GardenManager.getInstance();
		mContext = this;
		setContentView(R.layout.hut);
		buildMyTabHost();

	}

	private void buildMyTabHost() {
		tabHost = getTabHost(); // The activity TabHost

		// ********************** Tab myseeds**********************
		View myseedstitle = LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) myseedstitle.findViewById(R.id.tabsText);
		tv.setText(getResources().getString(R.string.hut_menu_myseeds).toUpperCase());
		TabSpec myseedsspec = tabHost.newTabSpec("detail").setIndicator(myseedstitle);

		VendorSeedDBHelper myBank = new VendorSeedDBHelper(this);
		ArrayList<BaseSeedInterface> mySeeds = myBank.getMySeeds();
		Intent intent;

		intent = new Intent().setClass(this, MySeedsListActivity.class);
		myseedsspec.setContent(intent);
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);
		tabHost.addTab(myseedsspec);

		// ********************** Tab commercial seed**********************
		View vendorseedstitle = LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
		tv = (TextView) vendorseedstitle.findViewById(R.id.tabsText);
		tv.setText(getResources().getString(R.string.hut_menu_vendorseeds).toUpperCase());
		TabSpec vendorspec = tabHost.newTabSpec("vendorseeds").setIndicator(vendorseedstitle);
		intent = new Intent().setClass(this, VendorListActivity.class);
		vendorspec.setContent(intent);
		tabHost.addTab(vendorspec);

		if (mySeeds.size() == 0)
			getTabHost().setCurrentTabByTag("vendorseeds");

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
	public void onTabChanged(String tabId) {

	}

	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}
}
