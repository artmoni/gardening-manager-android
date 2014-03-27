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

import org.gots.IntentIntegratorSupportV4;
import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.service.SeedUpdateService;
import org.gots.ui.fragment.AbstractListFragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class VendorListActivity extends AbstractListFragment {

    public Context mContext;

    public SeedListAdapter listVendorSeedAdapter;

    protected CharSequence currentFilter = "";

    private ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mContext = getActivity();
        mContext.registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        listVendorSeedAdapter = new VendorSeedListAdapter(mContext, new ArrayList<BaseSeedInterface>());
        setListAdapter(listVendorSeedAdapter);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_catalogue, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.idSeedFilter:
            displaySearchBox();
            return true;
        case R.id.refresh_seed:
            Intent seedIntent = new Intent(mContext, SeedUpdateService.class);
            mContext.startService(seedIntent);
            return true;
        case R.id.new_seed_barcode:
            IntentIntegratorSupportV4 integrator = new IntentIntegratorSupportV4(this);
            integrator.initiateScan();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        final IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            Log.i("Scan result", scanResult.toString());

            new AsyncTask<Void, Void, BaseSeedInterface>() {
                @Override
                protected BaseSeedInterface doInBackground(Void... params) {
                    BaseSeedInterface scanSeed = seedProvider.getSeedByBarCode(scanResult.getContents());

                    return scanSeed;
                }

                protected void onPostExecute(BaseSeedInterface scanSeed) {
                    if (scanSeed != null) {
                        // seedProvider.addToStock(scanSeed, gardenProvider.getCurrentGarden());
                        // updateVendorSeeds();
                        // listVendorSeedAdapter.notifyDataSetChanged();
                        currentFilter = scanSeed.getBareCode();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setTitle(getResources().getString(R.string.seed_menu_add_barcode));
                        alertDialogBuilder.setMessage(
                                getResources().getString(R.string.seed_description_barcode_noresult)).setCancelable(
                                false).setPositiveButton(getResources().getString(R.string.seed_action_add_catalogue),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // if this button is clicked, close
                                        // current activity
                                        // MainActivity.this.finish();
                                        Intent i = new Intent(mContext, NewSeedActivity.class);
                                        i.putExtra("org.gots.seed.barcode", scanResult.getContents());
                                        mContext.startActivity(i);
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
        // buildMyTabHost();

    }

    private void displaySearchBox() {

        getActivity().findViewById(R.id.linearlayoutSearchBox).setVisibility(View.VISIBLE);
        EditText filter = (EditText) getActivity().findViewById(R.id.edittextSearchFilter);
        filter.setText(currentFilter);

        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listVendorSeedAdapter.getFilter().filter(s.toString());
            }
        });
        ImageButton clear = (ImageButton) getActivity().findViewById(R.id.clearSearchFilter);
        clear.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentFilter = "";
                closeSearchBox();
            }

        });
    }

    private void closeSearchBox() {
        EditText filter = (EditText) getActivity().findViewById(R.id.edittextSearchFilter);
        filter.setText("");
        getActivity().findViewById(R.id.linearlayoutSearchBox).setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(filter.getWindowToken(), 0);
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateVendorSeeds();
        }
    };

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        updateVendorSeeds();
        super.onResume();
    }

    protected synchronized void updateVendorSeeds() {
        new AsyncTask<Void, Integer, List<BaseSeedInterface>>() {

            protected void onPreExecute() {
                dialog = ProgressDialog.show(mContext, "", mContext.getResources().getString(R.string.gots_loading),
                        true);
                dialog.setCanceledOnTouchOutside(true);
                super.onPreExecute();
            };

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {
                List<BaseSeedInterface> catalogue = seedProvider.getVendorSeeds(false);
                if (catalogue.size() == 0)
                    catalogue = seedProvider.getVendorSeeds(true);
                return catalogue;
            }

            protected void onPostExecute(List<BaseSeedInterface> vendorSeeds) {
                try {
                    dialog.dismiss();
                    dialog = null;
                } catch (Exception e) {
                    // nothing
                }
                listVendorSeedAdapter.setSeeds(vendorSeeds);
                listVendorSeedAdapter.getFilter().filter(currentFilter);
                if (!"".equals(currentFilter) && currentFilter != null)
                    displaySearchBox();
                listVendorSeedAdapter.notifyDataSetChanged();

                super.onPostExecute(vendorSeeds);
            };
        }.execute();
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
        mContext.unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();
    }
}
