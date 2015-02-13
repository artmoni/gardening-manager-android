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
import org.gots.action.ActionOnSeed;
import org.gots.action.bean.BuyingAction;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.action.bean.SowingAction;
import org.gots.ads.GotsAdvertisement;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.provider.nuxeo.NuxeoGardenProvider;
import org.gots.provider.SeedsContentProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;
import org.gots.ui.AllotmentListFragment.OnAllotmentSelected;
import org.gots.ui.VendorListFragment.OnSeedSelected;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
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
        ActionBar actionBar = getSupportActionBar();

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
                if (Build.VERSION.SDK_INT >= 16) {
                    findViewById(R.id.clearSearchFilter).setBackground(getResources().getDrawable(R.drawable.ic_search));
                } else {
                    findViewById(R.id.clearSearchFilter).setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_search));
                }
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
            }

        });

    }

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
                        currentFilter = scanSeed.getBareCode();
                        Toast.makeText(getApplicationContext(), scanSeed.getSpecie() + " Added to stock",
                                Toast.LENGTH_LONG).show();

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

        bar.removeAllTabs();
        // // ********************** Tab description **********************
        Bundle args;
        args = new Bundle();
        addTab(new VendorListFragment(), getString(R.string.hut_menu_vendorseeds));
        args = new Bundle();
        if (currentAllotment != -1) {
            args.putBoolean(VendorListFragment.IS_SELECTABLE, true);
        }
        if (gotsPrefs.getParrotToken() != null)
            addTab(new VendorListFragment(VendorListFragment.FILTER_PARROT), getString(R.string.hut_menu_vendorseeds));

        addTab(new VendorListFragment(VendorListFragment.FILTER_STOCK), getString(R.string.hut_menu_myseeds));

        if (gotsPrefs.isConnectedToServer()) {
            addTab(new VendorListFragment(VendorListFragment.FILTER_FAVORITES), getString(R.string.hut_menu_favorites));
        }
        addTab(new VendorListFragment(VendorListFragment.FILTER_THISMONTH), getString(R.string.hut_menu_thismonth));
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

    @SuppressWarnings("deprecation")
    protected void performSearch(final EditText filter) {
        new AsyncTask<Void, Void, List<BaseSeedInterface>>() {
            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {
                NuxeoSeedProvider nuxeoSeedProvider = new NuxeoSeedProvider(getApplicationContext());
                return nuxeoSeedProvider.getVendorSeedsByName(filter.getText().toString(), true);
            }

            protected void onPostExecute(List<BaseSeedInterface> result) {
                if (result != null) {
                    if (clearFilter) {
                        currentFilter = "";
                        filter.setText(currentFilter);
                        clearFilter = false;
                        if (Build.VERSION.SDK_INT >= 16) {
                            findViewById(R.id.clearSearchFilter).setBackground(
                                    getResources().getDrawable(R.drawable.ic_search));
                        } else {
                            findViewById(R.id.clearSearchFilter).setBackgroundDrawable(
                                    getResources().getDrawable(R.drawable.ic_search));
                        }
                    } else {
                        currentFilter = filter.getText().toString();
                        clearFilter = true;
                        clearFilter = false;
                        if (Build.VERSION.SDK_INT >= 16) {
                            findViewById(R.id.clearSearchFilter).setBackground(
                                    getResources().getDrawable(R.drawable.ic_menu_close_clear_cancel));
                        } else {
                            findViewById(R.id.clearSearchFilter).setBackgroundDrawable(
                                    getResources().getDrawable(R.drawable.ic_menu_close_clear_cancel));
                        }
                        
                    }

                    Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.pager + ":" + getSelectedTab());
                    if (fragment.getArguments() != null
                            && fragment.getArguments().getBoolean(VendorListFragment.FILTER_PARROT)) {
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
                        }
                    }
                }
            };
        }.execute();

    }

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
