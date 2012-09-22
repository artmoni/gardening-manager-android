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

import java.util.Random;

import org.gots.R;
import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.allotment.sql.AllotmentDBHelper;
import org.gots.analytics.GotsAnalytics;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.help.HelpUriBuilder;
import org.gots.weather.view.WeatherWidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class MyMainGarden extends Activity {

	private ListAllotmentAdapter lsa;
	ListView listAllotments;
	WeatherWidget weatherWidget;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		// GardenManager gm =GardenManager.getInstance();

		setContentView(R.layout.garden);

		lsa = new ListAllotmentAdapter(this);
		listAllotments = (ListView) findViewById(R.id.IdGardenAllotmentsList);
		listAllotments.setAdapter(lsa);
		listAllotments.setDivider(null);
		listAllotments.setDividerHeight(0);

		if (listAllotments.getCount() == 0) {
			final String classname =  getClass().getSimpleName();
			new AlertDialog.Builder(this)
            .setIcon(R.drawable.help)
            .setTitle(R.string.menu_help_firstlaunch)
            .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                	Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri( classname)));
        			startActivity(browserIntent);                }
            })
            .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                    /* User clicked Cancel so do some stuff */
                }
            })
            .show();
//			Intent intent = new Intent(this, MyMainGardenFirstTime.class);
//			startActivity(intent);
		}

		// listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.help_hut_2));

		View dashboardButton = (View) findViewById(R.id.btReturn);
		dashboardButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_garden, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.new_allotment:
			BaseAllotmentInterface newAllotment = new Allotment();
			newAllotment.setName("" + new Random().nextInt());

			AllotmentDBHelper helper = new AllotmentDBHelper(this);
			helper.insertAllotment(newAllotment);
			lsa.notifyDataSetChanged();

			if (listAllotments.getCount() == 0)
				listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.help_hut));
			else
				listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_simple));
			return true;
		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass().getSimpleName())));
			startActivity(browserIntent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		lsa.notifyDataSetChanged();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}
}
