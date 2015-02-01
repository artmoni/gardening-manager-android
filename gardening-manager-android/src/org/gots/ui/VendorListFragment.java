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
import org.gots.action.ActionOnSeed;
import org.gots.action.bean.BuyingAction;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.view.OnProfileEventListener;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.provider.parrot.ParrotSeedProvider;
import org.gots.ui.fragment.AbstractListFragment;

import android.app.Activity;
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
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class VendorListFragment extends AbstractListFragment implements OnScrollListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    protected static final String FILTER_FAVORITES = "filter.favorites";

    protected static final String FILTER_THISMONTH = "filter.thismonth";

    protected static final String FILTER_BARCODE = "filter.barcode";

    protected static final String FILTER_VALUE = "filter.data";

    public static final String FILTER_PARROT = "filter.parrot";

    protected static final String FILTER_STOCK = "filter.stock";

    protected static final String BROADCAST_FILTER = "broadcast_filter";

    protected static final String IS_SELECTABLE = "seed.selectable";

    public static final String TAG = "VendorListActivity";

    public Context mContext;

    public SeedListAdapter listVendorSeedAdapter;

    protected CharSequence currentFilter = "";

    private Bundle args;

    private GridView gridViewCatalog;

    private int pageSize = 25;

    private int page = 0;

    private int paddingPage = 10;

    private OnSeedSelected mCallback;

    public interface OnSeedSelected {
        public abstract void onSeedClick(BaseSeedInterface seed);

        public abstract void onSeedLongClick(BaseSeedInterface seed);

    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnSeedSelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnSeedSelected");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        args = getArguments();

        listVendorSeedAdapter = new VendorSeedListAdapter(mContext, new ArrayList<BaseSeedInterface>());
        View view = inflater.inflate(R.layout.list_seed_grid, container, false);
        gridViewCatalog = (GridView) view.findViewById(R.id.seedgridview);
        gridViewCatalog.setAdapter(listVendorSeedAdapter);
        gridViewCatalog.setOnItemClickListener(this);
        gridViewCatalog.setOnItemLongClickListener(this);
        gridViewCatalog.setOnScrollListener(this);

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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BROADCAST_FILTER));
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
        if (args == null || args.size() == 0) {
            catalogue = seedProvider.getVendorSeeds(false, page, pageSize);
            if (catalogue.size() == 0)
                catalogue = seedProvider.getVendorSeeds(true, page, pageSize);
        } else if (args.getBoolean(FILTER_STOCK))
            catalogue = seedProvider.getMyStock(gardenManager.getCurrentGarden());

        else if (args.getBoolean(FILTER_FAVORITES))
            catalogue = seedProvider.getMyFavorites();
        else if (args.getBoolean(FILTER_BARCODE)) {
            catalogue.add(seedProvider.getSeedByBarCode(args.getString(FILTER_VALUE)));
        } else if (args.getBoolean(FILTER_THISMONTH))
            catalogue = seedProvider.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        else if (args.getBoolean(FILTER_PARROT)) {
            ParrotSeedProvider parrotProvider = new ParrotSeedProvider(mContext);
            if ("".equals(currentFilter))
                catalogue.addAll(parrotProvider.getVendorSeeds(true, page, pageSize));
            else
                catalogue = parrotProvider.getVendorSeedsByName(currentFilter.toString());

        } else if (args.getString(FILTER_VALUE) != null) {
            catalogue = seedProvider.getVendorSeedsByName(args.getString(FILTER_VALUE));

        }

        return catalogue;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseSeedInterface> vendorSeeds = (List<BaseSeedInterface>) data;
        listVendorSeedAdapter.setSeeds(vendorSeeds);
        listVendorSeedAdapter.notifyDataSetChanged();
        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onPause() {
        if (seedBroadcastReceiver != null && isAdded())
            mContext.unregisterReceiver(seedBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * CallBACK on long press
     */

    private final class MyCallBack implements ActionMode.Callback {

        private BaseSeedInterface currentSeed;

        private MyCallBack(int position) {
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
            gridViewCatalog.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_hut_contextual, menu);
            gridViewCatalog.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            ActionOnSeed actionDone = null;
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
//            case R.id.action_sow:
//                Intent intent = new Intent(mContext, GardenActivity.class);
//                intent.putExtra(GardenActivity.SELECT_ALLOTMENT, true);
//                intent.putExtra(GardenActivity.VENDOR_SEED_ID, currentSeed.getSeedId());
//                mContext.startActivity(intent);
//
//                break;
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

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (firstVisibleItem + visibleItemCount >= totalItemCount && firstVisibleItem != 0) {
            if (isReady()) {
                additems();
            }
        }
    }

    private void additems() {
        page = page + paddingPage;
        pageSize = pageSize + paddingPage;
        Log.d(TAG, "page=" + page + " - pageSize=" + pageSize);
        runAsyncDataRetrieval();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    SeedListAdapter getListAdapter() {
        return listVendorSeedAdapter;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ((ActionBarActivity) getActivity()).startSupportActionMode(new MyCallBack(position));
        gridViewCatalog.setSelection(position);;
        gridViewCatalog.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> list, View container, int position, long id) {
        mCallback.onSeedClick(listVendorSeedAdapter.getItem(position));
    }
}
