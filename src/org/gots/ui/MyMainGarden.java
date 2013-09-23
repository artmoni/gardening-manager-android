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
import java.util.Random;

import org.gots.R;
import org.gots.ads.GotsAdvertisement;
import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.analytics.GotsAnalytics;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.help.HelpUriBuilder;
import org.gots.weather.view.WeatherWidget;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MyMainGarden extends AbstractActivity {

    private ListAllotmentAdapter lsa;

    ListView listAllotments;

    WeatherWidget weatherWidget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_allotments_name);

        // GardenManager gm =GardenManager.getInstance();

        setContentView(R.layout.garden);
        listAllotments = (ListView) findViewById(R.id.IdGardenAllotmentsList);
        lsa = new ListAllotmentAdapter(getApplicationContext(), new ArrayList<BaseAllotmentInterface>());
        listAllotments.setAdapter(lsa);
        listAllotments.setDivider(null);
        listAllotments.setDividerHeight(0);

        // listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.help_hut_2));
        if (!gotsPrefs.isPremium()) {
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
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.menu_garden, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.new_allotment:

            // AllotmentDBHelper helper = new AllotmentDBHelper(this);
            // helper.insertAllotment(newAllotment);
            new AsyncTask<Void, Integer, BaseAllotmentInterface>() {
                @Override
                protected BaseAllotmentInterface doInBackground(Void... params) {
                    BaseAllotmentInterface newAllotment = new Allotment();
                    newAllotment.setName("" + new Random().nextInt());
                    return allotmentManager.createAllotment(newAllotment);
                }

                @Override
                protected void onPostExecute(BaseAllotmentInterface result) {
                    onResume();
                    super.onPostExecute(result);
                }
            }.execute();

            listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_simple));
            return true;
        case R.id.help:
            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
            startActivity(browserIntent);

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private ProgressDialog dialog;

    @Override
    protected void onResume() {
        new AsyncTask<Void, Integer, List<BaseAllotmentInterface>>() {

            protected void onPreExecute() {
                dialog = ProgressDialog.show(MyMainGarden.this, "", getResources().getString(R.string.gots_loading),
                        true);
                dialog.setCanceledOnTouchOutside(true);
            };

            @Override
            protected List<BaseAllotmentInterface> doInBackground(Void... params) {
                return allotmentManager.getMyAllotments();
            }

            @Override
            protected void onPostExecute(List<BaseAllotmentInterface> result) {
                lsa.setAllotments(result);
                try {
                    dialog.dismiss();
                    dialog = null;
                } catch (Exception e) {
                    // nothing
                }
                // if (listAllotments.getCount() == 0) {
                // final String classname = getClass().getSimpleName();
                // new AlertDialog.Builder(getApplicationContext()).setIcon(R.drawable.help).setTitle(
                // R.string.menu_help_firstlaunch).setPositiveButton(R.string.button_ok,
                // new DialogInterface.OnClickListener() {
                // public void onClick(DialogInterface dialog, int whichButton) {
                //
                // Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                // Uri.parse(HelpUriBuilder.getUri(classname)));
                // startActivity(browserIntent);
                // }
                // }).setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                // public void onClick(DialogInterface dialog, int whichButton) {
                //
                // /* User clicked Cancel so do some stuff */
                // }
                // }).show();
                // // Intent intent = new Intent(this, MyMainGardenFirstTime.class);
                // // startActivity(intent);
                // }
                super.onPostExecute(result);
            }
        }.execute();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
