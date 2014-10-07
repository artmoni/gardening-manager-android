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
import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.BuyingAction;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.allotment.view.QuickAllotmentActionBuilder;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.provider.parrot.ParrotSeedProvider;
import org.gots.ui.fragment.AbstractListFragment;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class VendorListActivity extends AbstractListFragment {

    protected static final String FILTER_FAVORITES = "filter.favorites";

    protected static final String FILTER_THISMONTH = "filter.thismonth";

    protected static final String FILTER_BARCODE = "filter.barcode";

    protected static final String FILTER_VALUE = "filter.data";

    protected static final String BROADCAST_FILTER = "broadcast_filter";

    public static final String FILTER_PARROT = "filter.parrot";

    protected static final String FILTER_STOCK = "filter.stock";

    protected static final String IS_SELECTABLE = "seed.selectable";

    public static final String TAG = "VendorListActivity";

    public Context mContext;

    public SeedListAdapter listVendorSeedAdapter;

    protected CharSequence currentFilter = "";

    private ProgressDialog dialog;

    private Bundle args;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setHasOptionsMenu(true);
        // super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity();
        args = getArguments();

        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BROADCAST_FILTER));
        // mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.PROGRESS_FINISHED));
        listVendorSeedAdapter = new VendorSeedListAdapter(mContext, new ArrayList<BaseSeedInterface>());
        View view = inflater.inflate(R.layout.list_seed_grid, container, false);
        gridViewCatalog = (GridView) view.findViewById(R.id.seedgridview);
        gridViewCatalog.setAdapter(listVendorSeedAdapter);

        gridViewCatalog.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (args != null && args.getBoolean(IS_SELECTABLE)) {

                    getActivity().finish();
                } else {
//                    view.setSelected(!view.isSelected());
                    ((ActionBarActivity) getActivity()).startSupportActionMode(new MyCallBack(position));
                }
            }
        });
        // setListAdapter(listVendorSeedAdapter);

        return view;
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BROADCAST_FILTER.equals(intent.getAction())) {
                currentFilter = intent.getExtras().getString(FILTER_VALUE);
            }
            runAsyncDataRetrieval();
        }
    };

    private GridView gridViewCatalog;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        List<BaseSeedInterface> catalogue = new ArrayList<BaseSeedInterface>();
        if (args == null) {
            catalogue = seedProvider.getVendorSeeds(false);
            if (catalogue.size() == 0)
                catalogue = seedProvider.getVendorSeeds(true);
        } else if (args.getBoolean(FILTER_STOCK))
            catalogue = seedProvider.getMyStock(gardenManager.getCurrentGarden());

        else if (args.getBoolean(FILTER_FAVORITES))
            // listVendorSeedAdapter.getFilter().filter("LIKE");
            catalogue = seedProvider.getMyFavorites();
        else if (args.getBoolean(FILTER_BARCODE)) {
            // listVendorSeedAdapter.getFilter().filter("LIKE");
            catalogue.add(seedProvider.getSeedByBarCode(args.getString(FILTER_VALUE)));
        } else if (args.getBoolean(FILTER_THISMONTH))
            // listVendorSeedAdapter.getFilter().filter("THISMONTH");
            catalogue = seedProvider.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        else if (args.getBoolean(FILTER_PARROT)) {
            ParrotSeedProvider parrotProvider = new ParrotSeedProvider(mContext);
            if ("".equals(currentFilter))
                catalogue = parrotProvider.getVendorSeeds(true);
            else
                catalogue = parrotProvider.getVendorSeedsByName(currentFilter.toString());

        }
        return catalogue;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseSeedInterface> vendorSeeds = (List<BaseSeedInterface>) data;
        listVendorSeedAdapter.setSeeds(vendorSeeds);
        // listVendorSeedAdapter.getFilter().filter(currentFilter);
        // if (!"".equals(currentFilter) && currentFilter != null)
        // displaySearchBox();
        listVendorSeedAdapter.notifyDataSetChanged();

        // if (progressBar != null)
        //
        // progressBar.stopAnimatingBackground();
        // setActionRefresh(false);

        super.onNuxeoDataRetrieved(data);
    }


    @Override
    public void onPause() {
        try {
            dialog.dismiss();
            dialog = null;
        } catch (Exception e) {
            // nothing
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (seedBroadcastReceiver != null)
            mContext.unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();
    }

    private final class MyCallBack implements ActionMode.Callback {
        private final int position;

        private BaseSeedInterface currentSeed;

        private MyCallBack(int position) {
            this.position = position;
            currentSeed = listVendorSeedAdapter.getItem(position);
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

            SeedActionInterface actionDone = null;
            switch (item.getItemId()) {
            case R.id.action_seed_detail:
                Intent i = new Intent(mContext, TabSeedActivity.class);
                i.putExtra("org.gots.seed.vendorid", currentSeed.getSeedId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
                break;
            case R.id.action_stock_add:
                actionDone = new BuyingAction(mContext);
                break;
            case R.id.action_stock_reduce:
                actionDone = new ReduceQuantityAction(mContext);
                break;
            case R.id.action_sow:
                Intent intent = new Intent(mContext, MyMainGarden.class);
                intent.putExtra(MyMainGarden.SELECT_ALLOTMENT, true);
                intent.putExtra(MyMainGarden.VENDOR_SEED_ID, currentSeed.getSeedId());
                mContext.startActivity(intent);

                break;
            default:
                break;
            }

            if (actionDone == null) {
                Log.w(TAG, "onActionItemClicked - unknown selected action");
                return false;
            }

            new AsyncTask<SeedActionInterface, Integer, Void>() {
                SeedActionInterface action;

                @Override
                protected Void doInBackground(SeedActionInterface... params) {
                    action = params[0];
                    action.execute((GrowingSeedInterface) currentSeed);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    Toast.makeText(
                            mContext,
                            SeedUtil.translateAction(mContext, action) + " - "
                                    + SeedUtil.translateSpecie(mContext, currentSeed), Toast.LENGTH_LONG).show();
                    mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
                    super.onPostExecute(result);
                }
            }.execute(actionDone);

            mode.finish();
            return true;
        }
    }
}
