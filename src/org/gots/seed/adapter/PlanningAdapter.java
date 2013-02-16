package org.gots.seed.adapter;

import android.widget.BaseAdapter;

public abstract class PlanningAdapter extends BaseAdapter {

	private String[] month = { "J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D" };

	public PlanningAdapter() {
		super();
	}

	@Override
	public int getCount() {

		return month.length;
	}

	@Override
	public String getItem(int position) {
		return month[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	
}