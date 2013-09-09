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
import org.gots.analytics.GotsAnalytics;
import org.gots.help.HelpUriBuilder;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.sql.GrowingSeedDBHelper;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ActionActivity extends AbstractActivity implements OnClickListener {

    private ListAllActionAdapter listActions;

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

        GrowingSeedDBHelper helper = new GrowingSeedDBHelper(this);

        if (getIntent().getExtras() != null)
            seedid = getIntent().getExtras().getInt("org.gots.seed.id");

        if (seedid > 0) {
            allSeeds.add(helper.getSeedById(seedid));
        } else
            allSeeds = helper.getGrowingSeeds();
        listActions = new ListAllActionAdapter(this, allSeeds, ListAllActionAdapter.STATUS_TODO);
        listAllotments = (ListView) findViewById(R.id.IdGardenActionsList);
        listAllotments.setAdapter(listActions);
        listAllotments.setDivider(null);
        listAllotments.setDividerHeight(0);

        if (!gotsPrefs.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        // GardenFactory gf = new GardenFactory(this);
        // gf.saveGarden(DashboardActivity.myGarden);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
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
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
            startActivity(browserIntent);

            // Intent i = new Intent(this, WebHelpActivity.class);
            // i.putExtra("org.gots.help.page", getClass().getSimpleName());
            // startActivity(i);

            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        listActions.notifyDataSetChanged();
        super.onResume();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        listActions.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
