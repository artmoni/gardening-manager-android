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

import org.gots.R;
import org.gots.analytics.GotsAnalytics;
import org.gots.garden.GardenInterface;
import org.gots.garden.adapter.ProfileAdapter;
import org.gots.help.HelpUriBuilder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ProfileActivity extends SherlockActivity {

	private ProfileAdapter profileAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.profile);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.dashboard_profile_name);

		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		final ListView profileList = (ListView) findViewById(R.id.IdGardenProfileList);
		profileAdapter = new ProfileAdapter(this);
		profileList.setAdapter(profileAdapter);
		profileList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				arg0.setSelected(false);
				arg1.setSelected(true);
				arg0.invalidate();

				GardenInterface selectedGarden = (GardenInterface) profileList.getItemAtPosition(arg2);

				Toast.makeText(arg1.getContext(), "selected num " + arg2 + " / garden " + selectedGarden.getName(),
						Toast.LENGTH_LONG).show();
			}
		});

	}

	// private void buildGardenList() {
	// GardenDBHelper helper = new GardenDBHelper(this);
	//
	// String[] referenceList = helper.getGardens();
	//
	// if (referenceList == null)
	// return;
	//
	// gardenSelector = (Spinner) findViewById(R.id.idGardenSelector);
	//
	// final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	// R.layout.spinner_item_text, referenceList);
	// gardenSelector.setAdapter(adapter);
	//
	// gardenSelector.setSelection((int)
	// gardenManager.getcurrentGarden().getId() - 1);
	//
	// gardenSelector.setOnItemSelectedListener(new
	// AdapterView.OnItemSelectedListener() {
	//
	// @Override
	// public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
	// long arg3) {
	//
	// gardenManager.setCurrentGarden(position + 1);
	// // buildWeatherList();
	//
	// GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
	// tracker.trackEvent("Garden", "Select",
	// gardenManager.getcurrentGarden().getLocality(), position + 1);
	//
	// weatherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_weather));
	// weatherState.setImageDrawable(getResources().getDrawable(R.drawable.weather_updating));
	// startService(weatherIntent);
	// registerReceiver(weatherBroadcastReceiver, new
	// IntentFilter(WeatherUpdateService.BROADCAST_ACTION));
	//
	// }
	//
	// @Override
	// public void onNothingSelected(AdapterView<?> arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	// }

	@Override
	protected void onResume() {
		super.onResume();
		profileAdapter.notifyDataSetChanged();
		// buildGardenList();
		// weatherState.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_weather));
		// weatherState.setImageDrawable(getResources().getDrawable(R.drawable.weather_updating));
		// startService(weatherIntent);
		// registerReceiver(weatherBroadcastReceiver, new
		// IntentFilter(WeatherUpdateService.BROADCAST_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		// unregisterReceiver(weatherBroadcastReceiver);
		// stopService(weatherIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_profile, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;

		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass()
					.getSimpleName())));
			startActivity(browserIntent);
			return true;

		case R.id.new_gaden:
			Intent i = new Intent(this, ProfileCreationActivity.class);
			startActivity(i);
			return true;
		case R.id.edit_garden:
			Intent intent = new Intent(this, ProfileCreationActivity.class);
			intent.putExtra("option", ProfileCreationActivity.OPTION_EDIT);
			startActivity(intent);

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}
}
