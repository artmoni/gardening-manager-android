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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HutActivity extends SherlockFragmentActivity implements ActionBar.TabListener {

	// private ListVendorSeedAdapter lvsea;
	ListView listSeeds;
	ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();
	// TabHost tabHost;
	private Context mContext;
	private ListView listView;
	private ViewPager mViewPager;

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
		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.dashboard_hut_name);

		bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// tabHost = getTabHost(); // The activity TabHost

		mViewPager = (ViewPager) findViewById(R.id.pager);
		TabsAdapter mTabsAdapter = new TabsAdapter(this, mViewPager);

		// // ********************** Tab description **********************
		mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_myseeds)),
				MySeedsListActivity.class, null);

		VendorSeedDBHelper myBank = new VendorSeedDBHelper(this);
		ArrayList<BaseSeedInterface> mySeeds = myBank.getMySeeds();
		
		mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_vendorseeds)),
				VendorListActivity.class, null);

		
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
		mViewPager.getAdapter().notifyDataSetChanged();

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
		inflater.inflate(R.menu.menu_hut, menu);
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

	
		case android.R.id.home:
			finish();
			return true;
		
		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass()
					.getSimpleName())));
			startActivity(browserIntent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	

	static final class TabInfo {
		private final Class<?> clss;
		private final Bundle args;

		TabInfo(Class<?> _class, Bundle _args) {
			clss = _class;
			args = _args;
		}
	}

	/**
	 * This is a helper class that implements the management of tabs and all
	 * details of connecting a ViewPager with associated TabHost. It relies on a
	 * trick. Normally a tab host has a simple API for supplying a View or
	 * Intent that each tab will show. This is not sufficient for switching
	 * between pages. So instead we make the content part of the tab host 0dp
	 * high (it is not shown) and the TabsAdapter supplies its own dummy view to
	 * show as the tab content. It listens to changes in tabs, and takes care of
	 * switch to the correct paged in the ViewPager whenever the selected tab
	 * changes.
	 */
	public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
			ViewPager.OnPageChangeListener {
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ViewPager mViewPager;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
			super(activity.getSupportFragmentManager());
			mContext = activity;
			mActionBar = activity.getSupportActionBar();
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionBar.addTab(tab);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mTabs.size();
		}

		@Override
		public Fragment getItem(int position) {
			TabInfo info = mTabs.get(position);
			Bundle bundle = new Bundle();

			Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
			fragment.setArguments(bundle);
			return fragment;
		}

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
		}

		public void onPageSelected(int position) {
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onPageScrollStateChanged(int state) {
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}
	}
}
