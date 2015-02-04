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
import org.gots.action.ActionOnSeed;
import org.gots.action.bean.BuyingAction;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.action.bean.SowingAction;
import org.gots.ads.GotsAdvertisement;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.provider.SeedsContentProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.ui.AllotmentListFragment.OnAllotmentSelected;
import org.gots.ui.VendorListFragment.OnSeedSelected;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.GetChars;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class HutActivity extends TabActivity implements OnSeedSelected, OnAllotmentSelected {

    // private ListVendorSeedAdapter lvsea;
    ListView listSeeds;

    ArrayList<GrowingSeed> allSeeds = new ArrayList<GrowingSeed>();

    private ViewPager mViewPager;

    private int currentAllotment = -1;

    TabsAdapter mTabsAdapter;

    private static final short FRAGMENT_ID_CATALOG = 0;

    String currentFilter = "";

    boolean clearFilter = true;

    private Fragment listAllotmentfragment;

    private BaseSeedInterface currentSeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent().getExtras() != null)
            currentAllotment = getIntent().getExtras().getInt("org.gots.allotment.reference");

        // GardenManager gm =GardenManager.getInstance();
        setContentView(R.layout.hut);
        final ActionBar actionBar = getSupportActionBar();

        actionBar.setCustomView(R.layout.actionbar_catalog);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // displaySpinnerFilter();
        displaySearchBox();
        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        buildMyTabHost();
    }

    private void displaySearchBox() {
        final EditText filter = (EditText) findViewById(R.id.edittextSearchFilter);
        filter.setText(currentFilter);

        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                findViewById(R.id.clearSearchFilter).setBackground(getResources().getDrawable(R.drawable.ic_search));
                clearFilter = false;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        filter.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(filter);
                    EditText filter = (EditText) findViewById(R.id.edittextSearchFilter);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });
        ImageButton search = (ImageButton) findViewById(R.id.clearSearchFilter);
        search.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performSearch(filter);
                EditText filter = (EditText) findViewById(R.id.edittextSearchFilter);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);

                // EditText filter = (EditText) findViewById(R.id.edittextSearchFilter);
                // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                // imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
            }

        });

    }

    @Override
    protected void onPostResume() {
        // final EditText filter = (EditText) findViewById(R.id.edittextSearchFilter);
        // InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
        // imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        super.onPostResume();
    }

    // protected void displaySpinnerFilter() {
    // Spinner searchFilter = (Spinner) findViewById(R.id.idSpinnerSearch);
    // List<String> list = new ArrayList<String>();
    // list.add(getResources().getString(R.string.hut_menu_filter));
    // list.add(getResources().getString(R.string.hut_menu_vendorseeds));
    // list.add(getResources().getString(R.string.hut_menu_myseeds));
    // list.add(getResources().getString(R.string.hut_menu_favorites));
    // list.add(getResources().getString(R.string.hut_menu_thismonth));
    // ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
    // searchFilter.setAdapter(dataAdapter);
    // searchFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
    // @Override
    // public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    // if (position == 0) {
    //
    // }
    // Bundle args = new Bundle();
    // switch (position) {
    // case 1:
    // mTabsAdapter.addTab(
    // getSupportActionBar().newTab().setTag("event_list").setText(
    // getString(R.string.hut_menu_vendorseeds)), VendorListActivity.class, null);
    // break;
    // case 2:
    // mTabsAdapter.addTab(
    // getSupportActionBar().newTab().setTag("event_list").setText(
    // getString(R.string.hut_menu_myseeds)), MySeedsListActivity.class, null);
    // break;
    // case 3:
    // args.putBoolean(VendorListActivity.FILTER_FAVORITES, true);
    // mTabsAdapter.addTab(
    // getSupportActionBar().newTab().setTag("event_list").setText(
    // getString(R.string.hut_menu_favorites)), VendorListActivity.class, args);
    // break;
    // case 4:
    // args.putBoolean(VendorListActivity.FILTER_THISMONTH, true);
    // mTabsAdapter.addTab(
    // getSupportActionBar().newTab().setTag("event_list").setText(
    // getString(R.string.hut_menu_thismonth)), VendorListActivity.class, args);
    // break;
    //
    // default:
    // break;
    // }
    // }
    //
    // @Override
    // public void onNothingSelected(AdapterView<?> parent) {
    // // TODO Auto-generated method stub
    //
    // }
    // });
    // }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            Log.i("Scan result", scanResult.toString());

            new AsyncTask<Void, Void, BaseSeedInterface>() {
                @Override
                protected BaseSeedInterface doInBackground(Void... params) {
                    BaseSeedInterface scanSeed = seedManager.getSeedByBarCode(scanResult.getContents());
                    if (scanSeed != null) {
                        seedManager.addToStock(scanSeed, getCurrentGarden());
                    }

                    return scanSeed;
                }

                protected void onPostExecute(BaseSeedInterface scanSeed) {
                    if (scanSeed != null) {
                        // seedProv�ider.addToStock(scanSeed, gardenProvider.getCurrentGarden());
                        // updateVendorSeeds();
                        // listVendorSeedAdapter.notifyDataSetChanged();
                        currentFilter = scanSeed.getBareCode();
                        Toast.makeText(getApplicationContext(), scanSeed.getSpecie() + " Added to stock",
                                Toast.LENGTH_LONG).show();

                        // Bundle args = new Bundle();
                        // args.putString(VendorListActivity.FILTER_DATA, scanSeed.getBareCode());
                        // args.putBoolean(VendorListActivity.FILTER_BARCODE, true);

                        // ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(
                        // "android:switcher:" + R.id.pager + ":" + 1);
                        // if (fragment == null
                        // || (fragment.getArguments() != null && !fragment.getArguments().getBoolean(
                        // VendorListActivity.FILTER_BARCODE))) {
                        // mTabsAdapter.addTab(getSupportActionBar().newTab().setTag("result").setText("Result"),
                        // VendorListActivity.class, args);
                        // mTabsAdapter.setCurrentItem(mTabsAdapter.getCount() - 1);
                        // }else
                        // {
                        // }

                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HutActivity.this);
                        alertDialogBuilder.setTitle(getResources().getString(R.string.seed_menu_add_barcode));
                        alertDialogBuilder.setMessage(
                                getResources().getString(R.string.seed_description_barcode_noresult)).setCancelable(
                                false).setPositiveButton(getResources().getString(R.string.seed_action_add_catalogue),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        // MainActivity.this.finish();
                                        Intent i = new Intent(HutActivity.this, NewSeedActivity.class);
                                        i.putExtra("org.gots.seed.barcode", scanResult.getContents());
                                        startActivity(i);
                                    }
                                }).setNegativeButton(getResources().getString(R.string.button_cancel),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, just close
                                        // the dialog box and do nothing
                                        dialog.cancel();
                                    }
                                });

                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                };
            }.execute();

        }

    }

    private void buildMyTabHost() {
        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_hut_name);

        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // tabHost = getTabHost(); // The activity TabHost

        // mViewPager = (ViewPager) findViewById(R.id.pager);
        // mTabsAdapter = new TabsAdapter(this, mViewPager);
        bar.removeAllTabs();
        // // ********************** Tab description **********************
        Bundle args;
        args = new Bundle();
        // mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_vendorseeds)),
        // VendorListFragment.class, args); // FRAGMENT_ID_CATALOG=0 -> see above
        addTab(new VendorListFragment(), getString(R.string.hut_menu_vendorseeds));
        args = new Bundle();
        if (currentAllotment != -1) {
            args.putBoolean(VendorListFragment.IS_SELECTABLE, true);
        }
        // args.putBoolean(VendorListFragment.FILTER_PARROT, true);
        if (gotsPrefs.getParrotToken() != null)
            // mTabsAdapter.addTab(
            // bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_vendorseeds_plant)),
            // VendorListFragment.class, args);
            addTab(new VendorListFragment(VendorListFragment.FILTER_PARROT), getString(R.string.hut_menu_vendorseeds));

        // mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText("species"),
        // FamilyListActivity.class, args);

        // mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_myseeds)),
        // MySeedsListActivity.class, null);
        // args = new Bundle();
        // args.putBoolean(VendorListFragment.FILTER_STOCK, true);
        // mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_myseeds)),
        // VendorListFragment.class, args);
        addTab(new VendorListFragment(VendorListFragment.FILTER_STOCK), getString(R.string.hut_menu_myseeds));

        if (gotsPrefs.isConnectedToServer()) {
            // args = new Bundle();
            // args.putBoolean(VendorListFragment.FILTER_FAVORITES, true);
            // mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_favorites)),
            // VendorListFragment.class, args);
            addTab(new VendorListFragment(VendorListFragment.FILTER_FAVORITES), getString(R.string.hut_menu_favorites));
        }
        // args = new Bundle();
        // args.putBoolean(VendorListFragment.FILTER_THISMONTH, true);
        // mTabsAdapter.addTab(bar.newTab().setTag("event_list").setText(getString(R.string.hut_menu_thismonth)),
        // VendorListFragment.class, args);
        addTab(new VendorListFragment(VendorListFragment.FILTER_THISMONTH), getString(R.string.hut_menu_thismonth));
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_hut, menu);
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
        case android.R.id.home:
            finish();
            return true;
        case R.id.new_seed_barcode:
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
            // tracker.trackEvent("Catalog", "menu", "scanBarCode", 0);
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

    protected void performSearch(final EditText filter) {
        if (clearFilter) {
            currentFilter = "";
            filter.setText(currentFilter);
            clearFilter = false;
            findViewById(R.id.clearSearchFilter).setBackground(getResources().getDrawable(R.drawable.ic_search));
        } else {
            currentFilter = filter.getText().toString();
            clearFilter = true;
            findViewById(R.id.clearSearchFilter).setBackground(
                    getResources().getDrawable(R.drawable.ic_menu_close_clear_cancel));
        }

        Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + R.id.pager + ":" + getSelectedTab());
        if (fragment.getArguments() != null && fragment.getArguments().getBoolean(VendorListFragment.FILTER_PARROT)) {
            Intent filterIntent = new Intent(VendorListFragment.BROADCAST_FILTER);
            filterIntent.putExtra(VendorListFragment.FILTER_VALUE, currentFilter);
            sendBroadcast(filterIntent);
        } else if (fragment instanceof VendorListFragment) {
            if (false) {
                Filterable fragFilter = (Filterable) ((VendorListFragment) fragment).getListAdapter();
                fragFilter.getFilter().filter(currentFilter.toString());
            } else {
                Fragment searchFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(
                        "android:switcher:" + R.id.pager + ":" + (FRAGMENT_ID_CATALOG));
                if (searchFragment instanceof VendorListFragment)
                    ((VendorListFragment) searchFragment).setFilterValue(currentFilter);
                // searchFragment.getArguments().clear();
                // searchFragment.getArguments().putString(VendorListFragment.FILTER_VALUE, currentFilter);
                // searchFragment.onResume();
                // mTabsAdapter.setCurrentItem(FRAGMENT_ID_CATALOG);
            }
        }
    }

    // static final class TabInfo {
    // private final Class<?> clss;
    //
    // private final Bundle args;
    //
    // TabInfo(Class<?> _class, Bundle _args) {
    // clss = _class;
    // args = _args;
    // }
    // }

    // /**
    // * This is a helper class that implements the management of tabs and all
    // * details of connecting a ViewPager with associated TabHost. It relies on a
    // * trick. Normally a tab host has a simple API for supplying a View or
    // * Intent that each tab will show. This is not sufficient for switching
    // * between pages. So instead we make the content part of the tab host 0dp
    // * high (it is not shown) and the TabsAdapter supplies its own dummy view to
    // * show as the tab content. It listens to changes in tabs, and takes care of
    // * switch to the correct paged in the ViewPager whenever the selected tab
    // * changes.
    // */
    // public class TabsAdapter extends TabActivity implements ActionBar.TabListener,
    // ViewPager.OnPageChangeListener {
    // private final Context mContext;
    //
    // private final ActionBar mActionBar;
    //
    // private final ViewPager mViewPager;
    //
    // private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();
    //
    // SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    //
    // public TabsAdapter(ActionBarActivity activity, ViewPager pager) {
    // super(activity.getSupportFragmentManager());
    // mContext = activity;
    // mActionBar = activity.getSupportActionBar();
    // mViewPager = pager;
    // mViewPager.setAdapter(this);
    // mViewPager.setOnPageChangeListener(this);
    // }
    //
    // public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
    // TabInfo info = new TabInfo(clss, args);
    // tab.setTag(info);
    // tab.setTabListener(this);
    // mTabs.add(info);
    // mActionBar.addTab(tab);
    // notifyDataSetChanged();
    // }
    //
    // public void removeTablisp(int index) {
    // mTabs.remove(index);
    // mActionBar.removeTab(mActionBar.getTabAt(index));
    //
    // notifyDataSetChanged();
    // }
    //
    // @Override
    // public int getCount() {
    // return mTabs.size();
    // }
    //
    // @Override
    // public Fragment getItem(int position) {
    // TabInfo info = mTabs.get(position);
    //
    // Fragment fragment = Fragment.instantiate(mContext, info.clss.getName(), info.args);
    // if (info.args != null)
    // fragment.setArguments(info.args);
    // registeredFragments.put(position, fragment);
    //
    // return fragment;
    // }
    //
    // @Override
    // public void destroyItem(ViewGroup container, int position, Object object) {
    // registeredFragments.remove(position);
    // super.destroyItem(container, position, object);
    // }
    //
    // public Fragment getRegisteredFragment(int position) {
    // return registeredFragments.get(position);
    // }
    //
    // public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    // }
    //
    // public void onPageSelected(int position) {
    // mActionBar.setSelectedNavigationItem(position);
    //
    // // Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag(
    // // "android:switcher:" + R.id.pager + ":" + position);
    // // if (fragment != null && fragment.getAdapter() != null)
    // // ((BaseAdapter) fragment.getAdapter()).notifyDataSetChanged();
    //
    // }
    //
    // public void onPageScrollStateChanged(int state) {
    // }
    //
    // public void onTabSelected(Tab tab, FragmentTransaction ft) {
    // Object tag = tab.getTag();
    // for (int i = 0; i < mTabs.size(); i++) {
    // if (mTabs.get(i) == tag) {
    // mViewPager.setCurrentItem(i);
    //
    // }
    // }
    // }
    //
    // public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    // }
    //
    // public void onTabReselected(Tab tab, FragmentTransaction ft) {
    // }
    //
    // public int getCurrentItem() {
    // return mViewPager.getCurrentItem();
    // }
    //
    // public void setCurrentItem(int itemId) {
    // // mViewPager.setCurrentItem(itemId);
    // mActionBar.setSelectedNavigationItem(itemId);
    // }
    // }

    @Override
    protected String requireRefreshSyncAuthority() {
        return SeedsContentProvider.AUTHORITY;
    }

    @Override
    public void onSeedClick(BaseSeedInterface seed) {
        Intent i = new Intent(getApplicationContext(), TabSeedActivity.class);
        i.putExtra("org.gots.seed.vendorid", seed.getSeedId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onSeedLongClick(VendorListFragment fragment, BaseSeedInterface seed) {
        // listAllotmentfragment = new AllotmentListFragment();
        //
        // FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
        // transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        // transactionTutorial.addToBackStack(null);
        // transactionTutorial.replace(R.id.contentFragment, listAllotmentfragment).commit();
        // findViewById(R.id.contentFragment).setVisibility(View.VISIBLE);
        // currentSeed = seed;
        startSupportActionMode(new MyCallBack(seed));
    }

    @Override
    public void onAllotmentClick(BaseAllotmentInterface allotmentInterface) {

        if (listAllotmentfragment != null && listAllotmentfragment.isAdded()) {
            FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
            transactionTutorial.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
            transactionTutorial.addToBackStack(null);
            transactionTutorial.remove(listAllotmentfragment).commit();
            findViewById(R.id.contentFragment).setVisibility(View.GONE);

        }
        if (currentSeed != null) {
            SowingAction action = new SowingAction(getApplicationContext());
            action.execute(allotmentInterface, (GrowingSeed) currentSeed);
        }
    }

    @Override
    public void onAllotmentLongClick(BaseAllotmentInterface allotmentInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGrowingSeedClick(View v, GrowingSeed growingSeedInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGrowingSeedLongClick(View v, GrowingSeed growingSeedInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * CallBACK on long press
     */

    private final class MyCallBack implements ActionMode.Callback {

        private BaseSeedInterface currentSeed;

        private MyCallBack(BaseSeedInterface seedInterface) {
            currentSeed = seedInterface;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (currentSeed.getNbSachet() == 0)
                menu.findItem(R.id.action_stock_reduce).setVisible(false);
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_hut_contextual, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            ActionOnSeed actionDone = null;
            switch (item.getItemId()) {
            case R.id.action_seed_detail:
                Intent i = new Intent(getApplicationContext(), TabSeedActivity.class);
                i.putExtra("org.gots.seed.vendorid", currentSeed.getSeedId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                break;
            case R.id.action_stock_add:
                actionDone = new BuyingAction(getApplicationContext());
                break;
            case R.id.action_stock_reduce:
                actionDone = new ReduceQuantityAction(getApplicationContext());
                break;
            // case R.id.action_sow:
            // Intent intent = new Intent(mContext, GardenActivity.class);
            // intent.putExtra(GardenActivity.SELECT_ALLOTMENT, true);
            // intent.putExtra(GardenActivity.VENDOR_SEED_ID, currentSeed.getSeedId());
            // mContext.startActivity(intent);
            //
            // break;
            default:
                break;
            }

            if (actionDone == null) {
                Log.w(TAG, "onActionItemClicked - unknown selected action");
                return false;
            }

            new AsyncTask<ActionOnSeed, Integer, Void>() {
                ActionOnSeed action;

                @Override
                protected Void doInBackground(ActionOnSeed... params) {
                    action = params[0];
                    action.execute((GrowingSeed) currentSeed);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    Toast.makeText(
                            getApplicationContext(),
                            SeedUtil.translateAction(getApplicationContext(), action) + " - "
                                    + SeedUtil.translateSpecie(getApplicationContext(), currentSeed), Toast.LENGTH_LONG).show();

                    if (getCurrentFragment() instanceof VendorListFragment)
                        ((VendorListFragment) getCurrentFragment()).update();
                    super.onPostExecute(result);
                }
            }.execute(actionDone);

            mode.finish();
            return true;
        }
    }

    @Override
    protected ViewPager getViewPager() {
        return (ViewPager) findViewById(R.id.pager);
    }
}
