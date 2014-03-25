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
import org.gots.ads.GotsAdvertisement;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;
import org.gots.seed.provider.parrot.ParrotSeedProvider;
import org.gots.inapp.GotsBillingDialog;
import org.gots.seed.GrowingSeedInterface;
import org.gots.ui.fragment.AbstractFragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class HutActivity extends AbstractFragmentActivity implements ActionBar.TabListener {

    protected static final String TAG = "HutActivity";

    // private ListVendorSeedAdapter lvsea;
    ListView listSeeds;

    ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();

    private ViewPager mViewPager;

    private int currentAllotment = -1;

    private TabsAdapter mTabsAdapter;

    protected CharSequence currentFilter = "";

    private EditText searchEditText;

    private ImageView searchButton;

    private int SWITCH_BUTTON_SEARCH = 1;

    private int SWITCH_BUTTON_CLEAR = 0;

    private int SWITCH_BUTTON = SWITCH_BUTTON_SEARCH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null)
            currentAllotment = getIntent().getExtras().getInt("org.gots.allotment.reference");

        // GardenManager gm =GardenManager.getInstance();
        setContentView(R.layout.hut);

        if (!gotsPref.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

        searchEditText = (EditText) findViewById(R.id.edittextSearchFilter);
        searchButton = (ImageView) findViewById(R.id.clearSearchFilter);

        buildSearchBox();

    }

    private void buildSearchBox() {

        searchEditText.setText(currentFilter);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchSeed();
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                SWITCH_BUTTON = SWITCH_BUTTON_SEARCH;
                switchSearchButton();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // listVendorSeedAdapter.getFilter().filter(s.toString());
                currentFilter = s;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                searchSeed();
            }

        });
    }

    protected void searchSeed() {
        Intent seedIntent = new Intent(BroadCastMessages.SEED_DISPLAYLIST);
        if (SWITCH_BUTTON == SWITCH_BUTTON_SEARCH) {
            Bundle extras = new Bundle();
            extras.putString(BroadCastMessages.SEED_DISPLAYLIST_FILTER, currentFilter.toString());
            seedIntent.putExtras(extras);
            SWITCH_BUTTON = SWITCH_BUTTON_CLEAR;
            switchSearchButton();
        } else {
            SWITCH_BUTTON = SWITCH_BUTTON_SEARCH;
            searchEditText.setText("");
            switchSearchButton();
        }
        sendBroadcast(seedIntent);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
    }

    private void switchSearchButton() {
        if (SWITCH_BUTTON == SWITCH_BUTTON_SEARCH)
            searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        else
            searchButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_close_clear_cancel));
    }

    private void buildMyTabHost() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_hut_name);

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // tabHost = getTabHost(); // The activity TabHost

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mTabsAdapter = new TabsAdapter(this, mViewPager);
        bar.removeAllTabs();
        // // ********************** Tab description **********************
        Bundle nuxeoArgs = new Bundle();
        nuxeoArgs.putString(VendorListActivity.PROVIDER, NuxeoSeedProvider.class.getName());
        mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_vendorseeds_veget)),
                VendorListActivity.class, nuxeoArgs);
        
        Bundle parrotArgs = new Bundle();
        parrotArgs.putString(VendorListActivity.PROVIDER, ParrotSeedProvider.class.getName());
        mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_vendorseeds_plant)),
                VendorListActivity.class, parrotArgs);

        mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_myseeds)),
                MySeedsListActivity.class, null);
        // an allotment is selected
        if (currentAllotment >= 0)
            bar.setSelectedNavigationItem(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildMyTabHost();

    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // mViewPager.getAdapter().notifyDataSetChanged();
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
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
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
            if (info.args != null)
                bundle.putAll(info.args);
            Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
            fragment.setArguments(bundle);
            return fragment;
        }

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);

            
            SherlockListFragment fragment = (SherlockListFragment) getSupportFragmentManager().findFragmentByTag(
                    "android:switcher:" + R.id.pager + ":" + position);
            if (fragment != null && fragment.getListAdapter() != null)
                ((BaseAdapter) fragment.getListAdapter()).notifyDataSetChanged();

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
