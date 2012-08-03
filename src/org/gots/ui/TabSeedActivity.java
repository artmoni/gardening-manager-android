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

import java.util.Locale;

import org.gots.R;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.GrowingSeedDBHelper;
import org.gots.seed.sql.VendorSeedDBHelper;
import org.gots.seed.view.SeedWidgetLong;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class TabSeedActivity extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seed_tab);

		TabHost tabHost = getTabHost(); // The activity TabHost
		TabHost.TabSpec spec; // Resusable TabSpec for each tab
		Intent intent; // Reusable Intent for each tab
		GrowingSeedInterface mSeed = null;

		// ********************** **********************
		if (getIntent().getExtras() == null) {
			Log.e("SeedActivity", "You must provide a org.gots.seed.id as an Extra Int");
			finish();
			return;
		}
		if (getIntent().getExtras().getInt("org.gots.seed.id") != 0) {
			int seedId = getIntent().getExtras().getInt("org.gots.seed.id");
			GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);
			mSeed = helper.getSeedById(seedId);
		} else if (getIntent().getExtras().getInt("org.gots.seed.vendorid") != 0) {
			int seedId = getIntent().getExtras().getInt("org.gots.seed.vendorid");
			VendorSeedDBHelper helper = new VendorSeedDBHelper(this);
			mSeed = (GrowingSeedInterface) helper.getSeedById(seedId);
		}

		SeedWidgetLong seedImage = (SeedWidgetLong) findViewById(R.id.IdSeedWidgetLong);
		seedImage.setSeed(mSeed);

		// ********************** Tab description **********************
		View detailtitle = LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) detailtitle.findViewById(R.id.tabsText);
		tv.setText(getResources().getString(R.string.seed_description_tabmenu_detail).toUpperCase());
		TabSpec descriptionspec = tabHost.newTabSpec("detail").setIndicator(detailtitle);
		intent = new Intent().setClass(this, SeedActivity.class);
		intent.putExtra("org.gots.seed.id", mSeed.getId());
		descriptionspec.setContent(intent);
		tabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		tabHost.addTab(descriptionspec);

		// ********************** Tab actions **********************
		if (mSeed.getGrowingSeedId() > 0) {
			View actionstitle = LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
			tv = (TextView) actionstitle.findViewById(R.id.tabsText);
			tv.setText(getResources().getString(R.string.seed_description_tabmenu_actions).toUpperCase());
			TabSpec actionsspec = tabHost.newTabSpec("actions").setIndicator(actionstitle);
			intent = new Intent().setClass(this, ListActionActivity.class);
			intent.putExtra("org.gots.seed.id", mSeed.getGrowingSeedId());
			actionsspec.setContent(intent);
			tabHost.addTab(actionsspec);
		}

		// ********************** Tab Wikipedia**********************
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {

			View wikipediatitle = LayoutInflater.from(this).inflate(R.layout.tabs_bg, null);
			tv = (TextView) wikipediatitle.findViewById(R.id.tabsText);
			tv.setText(getResources().getString(R.string.seed_description_tabmenu_wikipedia).toUpperCase());

			TabSpec wikipediaspec = tabHost.newTabSpec("wikipedia").setIndicator(wikipediatitle);
			intent = new Intent().setClass(this, WebViewActivity.class);
			String urlDescription;
			// urlDescription = mSeed.getUrlDescription();
			// if (urlDescription == null) {
			urlDescription = "http://" + Locale.getDefault().getLanguage() + ".wikipedia.org/wiki/" + mSeed.getSpecie();
			Toast.makeText(this, urlDescription, 1000);
			// }
			intent.putExtra("org.gots.seed.url", urlDescription);
			wikipediaspec.setContent(intent);
			tabHost.addTab(wikipediaspec);

		}

	}
}
