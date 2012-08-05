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
package org.gots.seed.adapter;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.List;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.comparator.ISeedSpecieComparator;
import org.gots.action.bean.BuyingAction;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.view.SeedWidgetLong;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

public class ListVendorSeedAdapter extends ArrayAdapter<BaseSeedInterface> {

	private BuyingAction buying;
	private LayoutInflater inflater;

	public ListVendorSeedAdapter(Context context, List<BaseSeedInterface> vendorSeeds) {
		super(context, 0, vendorSeeds);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Collections.sort(vendorSeeds, new ISeedSpecieComparator(context));
	}

	


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		final BaseSeedInterface currentSeed = getItem(position);
		SeedWidgetLong seedWidgetLong;
		ActionWidget actionWidget;

		if (vi == null)
			vi = inflater.inflate(R.layout.list_seed, null);

		seedWidgetLong = (SeedWidgetLong) vi.findViewById(R.id.idSeedWidgetLong);
		actionWidget = (ActionWidget) vi.findViewById(R.id.IdSeedAction);

		seedWidgetLong.setSeed(currentSeed);

		buying = new BuyingAction(getContext());
		buying.setState(ActionState.NORMAL);
		actionWidget.setAction(buying);
		actionWidget.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SeedActionInterface action = (SeedActionInterface) buying;
				action.execute((GrowingSeedInterface) currentSeed);
				// Toast.makeText(getContext(),
				// action.getName() + " " + currentSeed.getSpecie() + " " +
				// currentSeed.getVariety(), 30).show();
				notifyDataSetChanged();
			}
		});
		// actionWidget.setOnActionItemClickListener(new
		// ActionWidget.OnActionItemClickListener() {
		//
		// @Override
		// public void onItemClick(ActionWidget source, BaseActionInterface
		// baseActionInterface) {
		// SeedActionInterface action = (SeedActionInterface)
		// baseActionInterface;
		// action.execute((GrowingSeedInterface) currentSeed);
		// Toast.makeText(getContext(),
		// action.getName() + " " + currentSeed.getSpecie() + " " +
		// currentSeed.getVariety(), 30).show();
		// notifyDataSetChanged();
		// }
		// });

		Calendar sowTime = Calendar.getInstance();
		if (sowTime.get(Calendar.MONTH) > currentSeed.getDateSowingMin())
			sowTime.set(Calendar.YEAR, sowTime.get(Calendar.YEAR) + 1);
		sowTime.set(Calendar.MONTH, currentSeed.getDateSowingMin());

		Calendar harvestTime = new GregorianCalendar();
		harvestTime.setTime(sowTime.getTime());
		harvestTime.add(Calendar.DAY_OF_MONTH, currentSeed.getDurationMin());

		return vi;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

}
