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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.GardeningActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.comparator.ISeedSpecieComparator;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.action.bean.SowingAction;
import org.gots.action.sql.ActionDBHelper;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.ads.GotsAdvertisement;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.VendorSeedDBHelper;
import org.gots.seed.view.SeedWidgetLong;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MySeedsListAdapter extends BaseAdapter implements OnClickListener {
	private Context mContext;
	private ArrayList<BaseSeedInterface> mySeeds = new ArrayList<BaseSeedInterface>();
	private BaseAllotmentInterface allotment;
	private LayoutInflater inflater;
	private int nbAds = 0;
	private int frequencyAds = 4;

	// private SeedWidgetLong seedWidget;
	// private ActionWidget actionWidget;

	// final private static int nbActionToDisplay = 5;

	public MySeedsListAdapter(Context context, BaseAllotmentInterface allotment, ArrayList<BaseSeedInterface> seeds) {
		this.mContext = context;
		// this.mySeeds.addAll(mySeeds);
		this.allotment = allotment;
		this.mySeeds = seeds;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (!GotsPreferences.isPremium())
			nbAds = mySeeds.size() / frequencyAds + 1;
		Collections.sort(mySeeds, new ISeedSpecieComparator(context));

	}

	@Override
	public int getCount() {
		return mySeeds.size() + nbAds;
	}

	@Override
	public BaseSeedInterface getItem(int position) {
		if (position % frequencyAds > 0 && !GotsPreferences.isPremium())
			position = position - (position / frequencyAds + 1);
		return mySeeds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position % frequencyAds == 0 && !GotsPreferences.isPremium()) {
			GotsAdvertisement ads = new GotsAdvertisement(mContext);
			convertView = ads.getAdsLayout();
			return convertView;
		} else {
			View vi = convertView;
			final BaseSeedInterface currentSeed = getItem(position);
			SeedWidgetLong seedWidgetLong;
			ActionWidget actionWidget;

			// if (vi == null)
			vi = inflater.inflate(R.layout.list_seed, null);

			seedWidgetLong = (SeedWidgetLong) vi.findViewById(R.id.idSeedWidgetLong);
			actionWidget = (ActionWidget) vi.findViewById(R.id.IdSeedAction);

			seedWidgetLong.setSeed(currentSeed);

			BaseActionInterface action = null;
			if (allotment != null) {
				// action = new SowingAction(mContext);
				ActionDBHelper helper = new ActionDBHelper(mContext);
				action = (SowingAction) helper.getActionByName("sow");

				if (Calendar.getInstance().get(Calendar.MONTH) >= currentSeed.getDateSowingMin()
						&& Calendar.getInstance().get(Calendar.MONTH) <= currentSeed.getDateSowingMax())
					action.setState(ActionState.NORMAL);
				else if (Calendar.getInstance().get(Calendar.MONTH) + 1 >= currentSeed.getDateSowingMin())
					action.setState(ActionState.WARNING);
				else
					action.setState(ActionState.UNDEFINED);

			} else {
				action = new ReduceQuantityAction(mContext);
				action.setState(ActionState.NORMAL);
			}

			actionWidget.setAction(action);
			final BaseActionInterface baseActionInterface = action;
			actionWidget.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {

					if (allotment != null) {
						GardeningActionInterface action = (GardeningActionInterface) baseActionInterface;
						action.execute(allotment, (GrowingSeedInterface) currentSeed);
						((Activity) mContext).finish();
					} else {
						SeedActionInterface action = (SeedActionInterface) baseActionInterface;
						action.execute((GrowingSeedInterface) currentSeed);
					}
					notifyDataSetChanged();
				}
			});

			try {

				Calendar sowTime = Calendar.getInstance();
				if (sowTime.get(Calendar.MONTH) > currentSeed.getDateSowingMin())
					sowTime.set(Calendar.YEAR, sowTime.get(Calendar.YEAR) + 1);
				sowTime.set(Calendar.MONTH, currentSeed.getDateSowingMin());

				Calendar harvestTime = new GregorianCalendar();
				harvestTime.setTime(sowTime.getTime());
				harvestTime.add(Calendar.DAY_OF_MONTH, currentSeed.getDurationMin());

			} catch (Exception e) {
				// holder.seedSowingDate.setText("--");
			}

			return vi;
		}
	}

	@Override
	public void notifyDataSetChanged() {
		VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
		mySeeds = myBank.getMySeeds();
		super.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {

	}
}
