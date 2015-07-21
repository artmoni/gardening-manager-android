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
import org.gots.provider.SeedsContentProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.ui.fragment.AllotmentListFragment.OnAllotmentSelected;
import org.gots.ui.fragment.CatalogueFragment;
import org.gots.ui.fragment.CatalogueFragment.OnSeedSelected;
import org.gots.ui.fragment.FavoriteCatalogueFragment;
import org.gots.ui.fragment.MonthlySeedListFragment;
import org.gots.ui.fragment.ParrotCatalogueFragment;
import org.gots.ui.fragment.StockVendorListFragment;
import org.gots.ui.fragment.VendorCatalogueFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
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

    private ImageView actionBarSearchView;

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

        actionBarSearchView = (ImageView) findViewById(R.id.clearSearchFilter);

        // displaySpinnerFilter();
        displaySearchBox();
        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return seedManager.getVendorSeeds(false, 0, 1);
    }

    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        FloatingItem floatingItem = new FloatingItem();
        floatingItem.setTitle(getResources().getString(R.string.seed_action_add_catalogue));
        floatingItem.setRessourceId(R.drawable.bt_add_seed);
        floatingItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewSeedActivity.class);
                startActivity(i);
            }
        });
        floatingItems.add(floatingItem);

        FloatingItem recognitionItem = new FloatingItem();
        recognitionItem.setTitle(getResources().getString(R.string.plant_recognition));
        recognitionItem.setRessourceId(R.drawable.ic_flower_search);
        recognitionItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecognitionActivity.class);
                startActivity(i);
            }
        });
        floatingItems.add(recognitionItem);
        return floatingItems;
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
                actionBarSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
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
                    filterByName(filter);
                    return true;
                }
                return false;
            }
        });
        actionBarSearchView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                filterByName(filter);
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
        addTab(new VendorCatalogueFragment(), getString(R.string.hut_menu_vendorseeds));
        args = new Bundle();
        if (currentAllotment != -1) {
            args.putBoolean(CatalogueFragment.IS_SELECTABLE, true);
        }
        if (gotsPrefs.getParrotToken() != null)
            addTab(new ParrotCatalogueFragment(), "Parrot");

        addTab(new StockVendorListFragment(), getString(R.string.hut_menu_myseeds));

        if (gotsPrefs.isConnectedToServer()) {
            addTab(new FavoriteCatalogueFragment(), getString(R.string.hut_menu_favorites));
        }
        addTab(new MonthlySeedListFragment(), getString(R.string.hut_menu_thismonth));
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
        case R.id.new_seed_barcode:
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("deprecation")
    protected void performSearch(final EditText filter) {
        // new AsyncTask<Void, Void, List<BaseSeedInterface>>() {
        // @Override
        // protected List<BaseSeedInterface> doInBackground(Void... params) {
        // NuxeoSeedProvider nuxeoSeedProvider = new NuxeoSeedProvider(getApplicationContext());
        // return nuxeoSeedProvider.getVendorSeedsByName(filter.getText().toString(), true);
        // }
        //
        // protected void onPostExecute(List<BaseSeedInterface> result) {
        //
        // };
        // }.execute();
        if (clearFilter) {
            currentFilter = "";
            filter.setText(currentFilter);
            clearFilter = false;
            actionBarSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
        } else {
            currentFilter = filter.getText().toString();
            clearFilter = true;
            actionBarSearchView.setImageDrawable(getResources().getDrawable(R.drawable.ic_menu_close_clear_cancel));
        }

        CatalogueFragment fragment = (CatalogueFragment) getSupportFragmentManager().findFragmentByTag(
                "android:switcher:" + R.id.pager + ":" + getSelectedTab());
        // if (fragment.getArguments() != null
        // && fragment.getArguments().getBoolean(CatalogueFragment.FILTER_PARROT)) {
        // Intent filterIntent = new Intent(CatalogueFragment.BROADCAST_FILTER);
        // filterIntent.putExtra(CatalogueFragment.FILTER_VALUE, currentFilter);
        // sendBroadcast(filterIntent);
        // } else
        if (fragment instanceof CatalogueFragment) {
            // Fragment searchFragment = (Fragment) getSupportFragmentManager().findFragmentByTag(
            // "android:switcher:" + R.id.pager + ":" + (FRAGMENT_ID_CATALOG));
            // if (searchFragment instanceof CatalogueFragment)
            fragment.setFilterValue(currentFilter);
        }
    }

    @Override
    protected String requireRefreshSyncAuthority() {
        return SeedsContentProvider.AUTHORITY;
    }

    @Override
    public void onPlantCatalogueClick(BaseSeedInterface seed) {
        Intent i = new Intent(getApplicationContext(), TabSeedActivity.class);
        i.putExtra(TabSeedActivity.GOTS_VENDORSEED_ID, seed.getSeedId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onPlantCatalogueLongClick(CatalogueFragment fragment, BaseSeedInterface seed) {
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

                    if (getCurrentFragment() instanceof CatalogueFragment)
                        ((CatalogueFragment) getCurrentFragment()).update();
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

    protected void filterByName(final EditText filter) {
        performSearch(filter);
        // EditText filter = (EditText) findViewById(R.id.edittextSearchFilter);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
    }
}
