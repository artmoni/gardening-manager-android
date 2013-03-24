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
package org.gots.garden.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.gots.R;
import org.gots.ads.GotsAdvertisement;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.service.WeatherUpdateService;
import org.gots.weather.view.WeatherView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileAdapter extends BaseAdapter {

	private Context mContext;
	private int nbAds = 0;
	private int frequencyAds = 4;
	private List<GardenInterface> myGardens = new ArrayList<GardenInterface>();
	private LayoutInflater inflater;
	private ImageView weatherState;
	private Intent weatherIntent;
	private WeatherManager weatherManager;
	private GardenManager gardenManager;
	private GardenInterface selectedGarden;
	private View currentView;

	public ProfileAdapter(Context context, List<GardenInterface> myGardens) {
		mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		weatherManager = new WeatherManager(mContext);
		gardenManager = new GardenManager(mContext);

		if (!GotsPreferences.getInstance(mContext).isPremium())
			nbAds = myGardens.size() / frequencyAds + 1;
		this.myGardens = myGardens;
		selectedGarden = gardenManager.getcurrentGarden();
		// myGardens = weatherManager.get
	}

	@Override
	public int getCount() {
		return myGardens.size() + nbAds;
	}

	@Override
	public GardenInterface getItem(int position) {
		if (position % frequencyAds > 0 && !GotsPreferences.getInstance(mContext).isPremium())
			position = position - (position / frequencyAds + 1);
		return myGardens.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		if (position % frequencyAds == 0 && !GotsPreferences.getInstance(mContext).isPremium()) {
			GotsAdvertisement ads = new GotsAdvertisement(mContext);
			convertView = ads.getAdsLayout();
			return convertView;
		} else {
			View vi = convertView;

			// if (convertView == null)
			vi = inflater.inflate(R.layout.list_garden, null);

			TextView gardenName = (TextView) vi.findViewById(R.id.idGardenName);
			weatherState = (ImageView) vi.findViewById(R.id.idWeatherConnected);
			ImageView imageProfile = (ImageView) vi.findViewById(R.id.imageProfile);
			weatherHistory = (LinearLayout) vi.findViewById(R.id.layoutWeatherHistory);

			final GardenInterface currentGarden = getItem(position);

			if (selectedGarden != null && currentGarden != null && selectedGarden.getId() == currentGarden.getId()) {
				vi.setSelected(true);
				imageProfile.setVisibility(View.VISIBLE);
				weatherState.setVisibility(View.VISIBLE);
				weatherHistory.setVisibility(View.VISIBLE);

			} else {
				vi.setSelected(false);
				// vi.getBackground().setAlpha(200);
				imageProfile.setVisibility(View.GONE);
				weatherState.setVisibility(View.GONE);
				weatherHistory.setVisibility(View.GONE);

			}

			if (currentGarden.getName() != null)
				gardenName.setText(currentGarden.getName());
			else
				gardenName.setText(currentGarden.getAddress().getLocality());

			weatherIntent = new Intent(mContext, WeatherUpdateService.class);
			weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));

			weatherState.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
					weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_updating));
					mContext.startService(weatherIntent);
					mContext.registerReceiver(weatherBroadcastReceiver, new IntentFilter(
							WeatherUpdateService.BROADCAST_ACTION));
					// currentView = (ImageView) v;

				}
			});

			// *************** WEATHER HISTORY
			if (vi.isSelected()) {
				final HorizontalScrollView scrollView = (HorizontalScrollView) vi
						.findViewById(R.id.scrollWeatherHistory);
				scrollView.post(new Runnable() {
					@Override
					public void run() {
						scrollView.scrollTo(scrollView.getWidth(), scrollView.getHeight());
					}
				});
				if (weatherHistory.getChildCount() > 0)
					weatherHistory.removeAllViews();

				for (int i = -10; i <= 0; i++) {
					WeatherConditionInterface condition;
					try {
						condition = weatherManager.getCondition(i);
					} catch (Exception e) {
						Calendar weatherday = new GregorianCalendar();
						weatherday.setTime(Calendar.getInstance().getTime());
						weatherday.add(Calendar.DAY_OF_YEAR, i);

						condition = new WeatherCondition();
						condition.setDate(weatherday.getTime());
					}

					WeatherView view = new WeatherView(mContext);
					view.setWeather(condition);
					view.setPadding(2, 0, 2, 0);
					weatherHistory.addView(view);

				}
			}

			vi.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					gardenManager.setCurrentGarden(currentGarden);
					selectedGarden = getItem(position);
					notifyDataSetChanged();

					weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
					weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_updating));
					mContext.startService(weatherIntent);
					mContext.registerReceiver(weatherBroadcastReceiver, new IntentFilter(
							WeatherUpdateService.BROADCAST_ACTION));
					currentView = v;

					GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
					tracker.trackEvent("Garden", "Select", gardenManager.getcurrentGarden().getLocality(), position + 1);
				}
			});

			return vi;

		}
	}

	private BroadcastReceiver weatherBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(intent);

		}
	};
	private ViewGroup weatherHistory;

	private void updateUI(Intent intent) {
		boolean isError = intent.getBooleanExtra("error", true);
		ImageView weatherConnected = (ImageView) currentView.findViewById(R.id.idWeatherConnected);

		TextView txtError = (TextView) currentView.findViewById(R.id.idTextAlert);
		if (isError) {
			txtError.setVisibility(View.VISIBLE);
			weatherConnected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_disconnected));
			weatherConnected.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_state_critical));

		} else {
			txtError.setVisibility(View.GONE);
			weatherConnected.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_connected));
			weatherConnected.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_state_ok));
		}
		// buildWeatherList();
	}

	@Override
	public void notifyDataSetChanged() {
		// myGardens = gardenManager.getMyGardens();
		super.notifyDataSetChanged();
	}
}
