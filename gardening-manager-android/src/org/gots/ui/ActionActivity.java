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
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.ads.GotsAdvertisement;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeedInterface;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class ActionActivity extends AbstractActivity {

    ListView listAllotments;

    ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_actions_name);

        setContentView(R.layout.actions);
        int seedid = 0;

        if (getIntent().getExtras() != null)
            seedid = getIntent().getExtras().getInt("org.gots.seed.id");

        new AsyncTask<Integer, Void, ArrayList<GrowingSeedInterface>>() {
            private ProgressDialog dialog;
            private ArrayList<GrowingSeedInterface> allSeeds = new ArrayList<GrowingSeedInterface>();

            private ListAllActionAdapter listActions;
            protected void onPreExecute() {
                dialog = ProgressDialog.show(ActionActivity.this, "", getResources().getString(R.string.gots_loading),
                        true);
                dialog.setCanceledOnTouchOutside(true);
            };
            @Override
            protected ArrayList<GrowingSeedInterface> doInBackground(Integer... params) {
                GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(
                        getApplicationContext());

                int seedid = params[0].intValue();
                if (seedid > 0) {
                    allSeeds.add(growingSeedManager.getGrowingSeedById(seedid));
                } else {
                    // allSeeds = growingSeedManager.getGrowingSeeds();
                    for (BaseAllotmentInterface allotment : allotmentManager.getMyAllotments())
                        allSeeds.addAll(growingSeedManager.getGrowingSeedsByAllotment(allotment));
                }
                listActions = new ListAllActionAdapter(getApplicationContext(), allSeeds,
                        ListAllActionAdapter.STATUS_TODO);
                return allSeeds;
            }

            protected void onPostExecute(ArrayList<GrowingSeedInterface> allSeeds) {

                listAllotments = (ListView) findViewById(R.id.IdGardenActionsList);
                listAllotments.setAdapter(listActions);
                listAllotments.setDivider(null);
                listAllotments.setDividerHeight(0);
                try {
                    dialog.dismiss();
                    dialog = null;
                } catch (Exception e) {
                    // nothing
                }
            };
        }.execute(seedid);

        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
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

    @Override
    protected void onResume() {
        // listActions.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // listActions.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}