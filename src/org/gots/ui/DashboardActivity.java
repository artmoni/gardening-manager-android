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
import org.gots.ads.GotsAdvertisement;
import org.gots.analytics.GotsAnalytics;
import org.gots.preferences.GotsPreferences;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class DashboardActivity extends Activity implements OnClickListener {
	// public static GardenInterface myGarden = new Garden();
	// GoogleAnalyticsTracker tracker;
	GotsAdvertisement adView;
	private WeatherWidget weatherWidget;
	private WeatherWidget weatherWidget2;
	private LinearLayout handle;
	private LinearLayout weatherWidgetLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();

		setContentView(R.layout.dashboard);

		// attach event handler to dash buttons
		findViewById(R.id.dashboard_button_hut).setOnClickListener(this);
		findViewById(R.id.dashboard_button_allotment).setOnClickListener(this);
		findViewById(R.id.dashboard_button_action).setOnClickListener(this);
		findViewById(R.id.dashboard_button_profile).setOnClickListener(this);

		handle = (LinearLayout) findViewById(R.id.handle);
		weatherWidget2 = new WeatherWidget(this, WeatherView.IMAGE);
		handle.addView(weatherWidget2);

		weatherWidgetLayout = (LinearLayout) findViewById(R.id.WeatherWidget);
		weatherWidget = new WeatherWidget(this, WeatherView.TEXT);
		weatherWidgetLayout.addView(weatherWidget);

		// ADMOB
		LinearLayout layout = (LinearLayout) findViewById(R.id.bannerAd);
		if (!GotsPreferences.DEVELOPPEMENT) {
			adView = new GotsAdvertisement(this);
			layout.addView(adView.getAdsLayout());

		} else
			layout.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_dashboard_top));

		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());
		GoogleAnalyticsTracker.getInstance().dispatch();

	}

	@Override
	public void onClick(View v) {

		Intent i = null;
		switch (v.getId()) {
		case R.id.dashboard_button_hut:

			
				i = new Intent(v.getContext(), HutActivity.class);
			break;
		case R.id.dashboard_button_allotment:
			
				i = new Intent(v.getContext(), MyMainGarden.class);
			break;
		case R.id.dashboard_button_action:
			
				i = new Intent(v.getContext(), ActionActivity.class);

			break;
		case R.id.dashboard_button_profile:
			
				i = new Intent(v.getContext(), org.gots.ui.ProfileActivity.class);
			break;
		default:
			break;
		}
		if (i != null) {
			startActivity(i);
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
	}

	@Override
	protected void onResume() {
		Log.i("DASHBOARD", "OnResume()");
		GoogleAnalyticsTracker.getInstance().dispatch();
		weatherWidget2.update();

		weatherWidget.update();

		super.onResume();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}
}
