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
import java.util.List;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.GardeningActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.comparator.ISeedSpecieComparator;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.action.sql.ActionDBHelper;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;
import org.gots.seed.view.SeedWidgetLong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MySeedsListAdapter extends BaseAdapter implements OnClickListener {
	private Context mContext;
	private List<BaseSeedInterface> mySeeds = new ArrayList<BaseSeedInterface>();
	private BaseAllotmentInterface allotment;
	private LayoutInflater inflater;

	// private SeedWidgetLong seedWidget;
	// private ActionWidget actionWidget;

	// final private static int nbActionToDisplay = 5;

	public MySeedsListAdapter(Context context, BaseAllotmentInterface allotment, List<BaseSeedInterface> seeds) {
		this.mContext = context;
		// this.mySeeds.addAll(mySeeds);
		this.allotment = allotment;
		this.mySeeds = seeds;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		Collections.sort(mySeeds, new ISeedSpecieComparator(context));

	}

	@Override
	public int getCount() {
		return mySeeds.size();
	}

	@Override
	public BaseSeedInterface getItem(int position) {

		return mySeeds.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
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

		BaseActionInterface action = null;
		if (allotment != null) {
			// action = new SowingAction(mContext);
			ActionDBHelper helper = new ActionDBHelper(mContext);
			action = helper.getActionByName("sow");

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
			    new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        if (allotment != null) {
                            GardeningActionInterface action = (GardeningActionInterface) baseActionInterface;
                            action.execute(allotment, (GrowingSeedInterface) currentSeed);
                            ((Activity) mContext).finish();
                        } else {
                            SeedActionInterface action = (SeedActionInterface) baseActionInterface;
                            action.execute((GrowingSeedInterface) currentSeed);
                        }
                        return null;
                    }
                    @Override
                    protected void onPostExecute(Void result) {
//                        notifyDataSetChanged();
                        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
                        super.onPostExecute(result);
                    }
                }.execute();
				
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

	@Override
	public void notifyDataSetChanged() {
		
		super.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {

	}
}
