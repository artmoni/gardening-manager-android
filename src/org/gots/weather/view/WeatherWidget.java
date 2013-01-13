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
package org.gots.weather.view;

import org.gots.weather.WeatherManager;
import org.gots.weather.adapter.WeatherWidgetAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;
import android.widget.TextView;

public class WeatherWidget extends GridView {

	// today

	private int mType = WeatherView.FULL;
	private WeatherWidgetAdapter weatherWidgetAdapter;
	private int nbDays = 2;
	private WeatherManager wm;

	public WeatherWidget(Context context, int type) {
		super(context);
		mType = type;
		initView();
	}

	public WeatherWidget(Context context, AttributeSet attr) {
		super(context, attr);
		mType = WeatherView.FULL;
		initView();

	}

	private void initView() {
		wm = new WeatherManager(getContext());
		if (wm.isConnected()) {
			weatherWidgetAdapter = new WeatherWidgetAdapter(getContext(), mType, wm.getConditionSet(nbDays));
			setAdapter(weatherWidgetAdapter);
		} else {
			TextView error = new TextView(getContext());
			error.setText("Error");
			addView(error);
		}
		setNumColumns(getAdapter().getCount());

	}

	// public void setNbPastDays(int nbPastDays) {
	// this.nbPastDays = nbPastDays;
	// // displayWeather();
	// }

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
	}

	public void update() {
		// wm.update();
		weatherWidgetAdapter.setConditions(wm.getConditionSet(nbDays));
		Log.i("update", weatherWidgetAdapter.getCount() + "<->" + getChildCount());

		weatherWidgetAdapter.notifyDataSetChanged();
		setAdapter(weatherWidgetAdapter);
		invalidateViews();
	}

}
