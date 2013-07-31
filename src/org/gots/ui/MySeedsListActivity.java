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
import org.gots.allotment.sql.AllotmentDBHelper;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.adapter.MySeedsListAdapter;
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;

import android.app.ProgressDialog;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

public class MySeedsListActivity extends SherlockListFragment {
    private MySeedsListAdapter listAdapter;

    private BaseAllotmentInterface allotment;

    private GotsSeedManager seedManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent().getExtras() != null) {
            String allotmentRef = getActivity().getIntent().getExtras().getString("org.gots.allotment.reference");
            if (allotmentRef != null) {
                AllotmentDBHelper helper = new AllotmentDBHelper(getActivity());
                allotment = helper.getAllotmentByName(allotmentRef);
            }
        }
        seedManager = GotsSeedManager.getInstance();
        seedManager.initIfNew(getActivity());
    }

    @Override
    public ListAdapter getListAdapter() {
        return super.getListAdapter();
    }

    public void update() {
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        new AsyncTask<Void, Integer, List<BaseSeedInterface>>() {
            private ProgressDialog dialog;

            protected void onPreExecute() {
//                dialog = ProgressDialog.show(getActivity(), "", getResources().getString(R.string.gots_loading), true);
//                dialog.setCanceledOnTouchOutside(true);
                // dialog.show();
                super.onPreExecute();
            };

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {
              
                List<BaseSeedInterface> mySeeds = seedManager.getMyStock(GardenManager.getInstance().getCurrentGarden());

                return mySeeds;
            }

            protected void onPostExecute(List<BaseSeedInterface> mySeeds) {
                listAdapter = new MySeedsListAdapter(getActivity(), allotment, mySeeds);
                setListAdapter(listAdapter);
//                if (dialog.isShowing())
//                    dialog.dismiss();

                super.onPostExecute(mySeeds);
            };
        }.execute();
        super.onResume();
    }

    // @Override
    // protected void onResume() {
    // super.onResume();
    // listAdapter.notifyDataSetChanged();
    //
    // }
    //
    // @Override
    // public boolean onCreateOptionsMenu(Menu menu) {
    // // MenuInflater inflater = getMenuInflater();
    // // inflater.inflate(R.menu.menu_stock, menu);
    // return super.onCreateOptionsMenu(menu);
    // }
    //
    // @Override
    // public boolean onOptionsItemSelected(MenuItem item) {
    // switch (item.getItemId()) {
    //
    // case R.id.new_seed_barcode:
    // IntentIntegrator integrator = new IntentIntegrator(this);
    // integrator.initiateScan();
    // return true;
    // case R.id.help:
    // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
    // Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
    // startActivity(browserIntent);
    //
    // return true;
    // default:
    // return super.onOptionsItemSelected(item);
    // }
    //
    // }
}
