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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

	public ProfileAdapter(Context context) {
		mContext = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		weatherManager = new WeatherManager(mContext);
		gardenManager = new GardenManager(mContext);
		myGardens = gardenManager.getMyGardens();
		if (!GotsPreferences.isPremium())
			nbAds = myGardens.size() / frequencyAds + 1;

		// myGardens = weatherManager.get
	}

	@Override
	public int getCount() {
		return myGardens.size() + nbAds;
	}

	@Override
	public GardenInterface getItem(int position) {
		if (position % frequencyAds > 0 && !GotsPreferences.isPremium())
			position = position - (position / frequencyAds + 1);
		return myGardens.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position % frequencyAds == 0 && !GotsPreferences.isPremium()) {
			GotsAdvertisement ads = new GotsAdvertisement(mContext);
			convertView = ads.getAdsLayout();
			return convertView;
		} else {
			View vi = convertView;
			final GardenInterface currentGarden = getItem(position);
			vi = inflater.inflate(R.layout.list_garden, null);

			// ImageView imageProfile = (ImageView)
			// vi.findViewById(R.id.imageProfile);
			// imageProfile.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// vi.setSelected(true);
			// }
			// });
			TextView gardenName = (TextView) vi.findViewById(R.id.idGardenName);
			gardenName.setText(currentGarden.getName());

			weatherIntent = new Intent(mContext, WeatherUpdateService.class);
			weatherState = (ImageView) vi.findViewById(R.id.idWeatherConnected);
			weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
			// weatherState.setOnClickListener(new View.OnClickListener() {
			//
			// @Override
			// public void onClick(View v) {
			// weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
			// weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_updating));
			// mContext.startService(weatherIntent);
			// mContext.registerReceiver(weatherBroadcastReceiver, new
			// IntentFilter(
			// WeatherUpdateService.BROADCAST_ACTION));
			//
			// }
			// });

			// *************** WEATHER HISTORY
			weatherHistory = (LinearLayout) vi.findViewById(R.id.layoutWeatherHistory);
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
			} else
				weatherHistory.setVisibility(View.GONE);
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
		// boolean isError = intent.getBooleanExtra("error", true);
		//
		// TextView txtError = (TextView) findViewById(R.id.idTextAlert);
		// if (isError) {
		// txtError.setVisibility(View.VISIBLE);
		// weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_disconnected));
		// weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_state_critical));
		//
		// } else {
		// txtError.setVisibility(View.GONE);
		// weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_connected));
		// weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_state_ok));
		// }
		// buildWeatherList();
	}

	@Override
	public void notifyDataSetChanged() {
		myGardens = gardenManager.getMyGardens();
		super.notifyDataSetChanged();
	}
}
