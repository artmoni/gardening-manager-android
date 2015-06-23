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
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.provider.parrot.ParrotSeedProvider;
import org.gots.ui.fragment.AbstractListFragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.GridView;

public abstract class CatalogueFragment extends AbstractListFragment implements OnScrollListener,
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    // protected static final String FILTER_FAVORITES = "filter.favorites";

    // protected static final String FILTER_THISMONTH = "filter.thismonth";

    // protected static final String FILTER_BARCODE = "filter.barcode";

//    protected static final String FILTER_VALUE = "filter.data";

    // public static final String FILTER_PARROT = "filter.parrot";

    // protected static final String FILTER_STOCK = "filter.stock";

    protected static final String BROADCAST_FILTER = "broadcast_filter";

    protected static final String IS_SELECTABLE = "seed.selectable";

    public static final String TAG = CatalogueFragment.class.getSimpleName();

    public Context mContext;

    public SeedListAdapter listVendorSeedAdapter;

    private String filterValue;

    private boolean force = false;

    private GridView gridViewCatalog;

    private int pageSize = 25;

    private int page = 0;

    private int paddingPage = 10;

    private OnSeedSelected mCallback;

    public interface OnSeedSelected {
        public abstract void onPlantCatalogueClick(BaseSeedInterface seed);

        public abstract void onPlantCatalogueLongClick(CatalogueFragment vendorListFragment, BaseSeedInterface seed);

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
        // args = getArguments();

        listVendorSeedAdapter = new VendorSeedListAdapter(mContext, new ArrayList<BaseSeedInterface>());
        View view = inflater.inflate(R.layout.list_seed_grid, container, false);
        gridViewCatalog = (GridView) view.findViewById(R.id.seedgridview);
        gridViewCatalog.setAdapter(listVendorSeedAdapter);
        gridViewCatalog.setOnItemClickListener(this);
        gridViewCatalog.setOnItemLongClickListener(this);
        gridViewCatalog.setOnScrollListener(this);

        return view;
    }

//    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
////            if (BROADCAST_FILTER.equals(intent.getAction())) {
////                filterValue = intent.getExtras().getString(FILTER_VALUE);
////            }
//            if (isReady())
//                runAsyncDataRetrieval();
//        }
//    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        runAsyncDataRetrieval();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
//        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BROADCAST_FILTER));
//        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        super.onResume();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));

        super.onNuxeoDataRetrievalStarted();
    }

    protected abstract List<BaseSeedInterface> onRetrieveNuxeoData(String filterValue, int page, int pageSize,
            boolean force);

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        List<BaseSeedInterface> catalogue = new ArrayList<BaseSeedInterface>();
        catalogue = onRetrieveNuxeoData(filterValue, page, pageSize, force);

        // if (filter.equals(FILTER_BARCODE)) {
        // catalogue.add(seedProvider.getSeedByBarCode(filterValue));
        // }
        force = false;
        return catalogue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
        runAsyncDataRetrieval();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseSeedInterface> vendorSeeds = (List<BaseSeedInterface>) data;
        listVendorSeedAdapter.setSeeds(vendorSeeds);
        listVendorSeedAdapter.notifyDataSetChanged();
        if (getActivity() != null)
            getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        if (getActivity() != null)
            getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
        super.onNuxeoDataRetrieveFailed();
    }

    @Override
    public void onPause() {
//        if (seedBroadcastReceiver != null && isAdded())
//            mContext.unregisterReceiver(seedBroadcastReceiver);
        super.onPause();
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
        force = true;
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
        gridViewCatalog.setSelection(position);
        gridViewCatalog.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
        mCallback.onPlantCatalogueLongClick(this, listVendorSeedAdapter.getItem(position));
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> list, View container, int position, long id) {
        mCallback.onPlantCatalogueClick(listVendorSeedAdapter.getItem(position));
    }

    public void update() {
        runAsyncDataRetrieval();
    }
}
