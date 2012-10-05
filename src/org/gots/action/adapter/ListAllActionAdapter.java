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
package org.gots.action.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.GrowingSeedDBHelper;
import org.gots.seed.view.SeedWidget;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAllActionAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<BaseActionInterface> actions = new ArrayList<BaseActionInterface>();
	// private ArrayList<GrowingSeedInterface> seeds = new
	// ArrayList<GrowingSeedInterface>();
	// private ArrayList<WeatherConditionInterface> weathers = new
	// ArrayList<WeatherConditionInterface>();

	private int current_status = STATUS_DONE;
	public static final int STATUS_TODO = 0;
	public static final int STATUS_DONE = 1;

	public ListAllActionAdapter(Context context, ArrayList<GrowingSeedInterface> allSeeds, int status) {
		this.mContext = context;
		current_status = status;
		for (Iterator<GrowingSeedInterface> iterator = allSeeds.iterator(); iterator.hasNext();) {
			GrowingSeedInterface seed = iterator.next();
			ActionSeedDBHelper helper = new ActionSeedDBHelper(context);
			ArrayList<BaseActionInterface> seedActions;

			if (current_status == STATUS_TODO) {
				seedActions = helper.getActionsToDoBySeed(seed);

			} else {
				seedActions = helper.getActionsDoneBySeed(seed);
			}

			if (seedActions.size() > 0) {
				for (Iterator<BaseActionInterface> iterator2 = seedActions.iterator(); iterator2.hasNext();) {
					// this.seeds.add(seed);
					// ********** GET ACTIONS for seed
//					if (SeedActionInterface.class.isInstance(iterator2)) {
						BaseActionInterface baseActionInterface = iterator2.next();
						this.actions.add(baseActionInterface);
//					}

				}
			}
		}
		Collections.sort(actions, new IStatusUpdateComparator());
		// Collections.sort(seeds, new IStatusUpdateComparator());

	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return actions.size();
	}

	@Override
	public BaseActionInterface getItem(int position) {
		return actions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout ll = (LinearLayout) convertView;
		if (convertView == null) {
			// ll = new LinearLayout(mContext);
			ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_action, parent, false);
		}

		GrowingSeedDBHelper helper = new GrowingSeedDBHelper(mContext);
		final GrowingSeedInterface seed = helper.getSeedById(actions.get(position).getGrowingSeedId());
		if (seed != null && BaseActionInterface.class.isInstance(actions.get(position))) {
			ActionWidget actionTODO = (ActionWidget) ll.findViewById(R.id.idActionView);
			actions.get(position).setState(ActionState.NORMAL);
			actionTODO.setAction(actions.get(position));

			SeedWidget seedView = (SeedWidget) ll.findViewById(R.id.idSeedView);
			seedView.setSeed(seed);

			// WeatherView weatherView = (WeatherView)
			// ll.findViewById(R.id.idWeatherView);
			// weatherView.setWeather(weathers.get(position));

			TextView tv = (TextView) ll.findViewById(R.id.IdSeedActionStatus);
			TextView tv2 = (TextView) ll.findViewById(R.id.IdSeedActionDate);

			SimpleDateFormat dateFormat = new SimpleDateFormat(" dd/MM/yyyy", Locale.FRANCE);

			if (current_status == STATUS_TODO) {
				tv.setText(mContext.getResources().getString(R.string.seed_action_todo));

				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(seed.getDateSowing());
				rightNow.add(Calendar.DAY_OF_YEAR, actions.get(position).getDuration());
				tv2.setText(dateFormat.format(rightNow.getTime()));
				actionTODO.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((SeedActionInterface) actions.get(position)).execute(seed);
						actions.remove(position);
						// seeds.remove(position);
						notifyDataSetChanged();
					}
				});

			} else {
				tv.setText(mContext.getResources().getString(R.string.seed_action_done));

				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(seed.getDateSowing());
				rightNow.add(Calendar.DAY_OF_YEAR, actions.get(position).getDuration());
				tv2.setText(dateFormat.format(actions.get(position).getDateActionDone()));
			}
		}
		return ll;
	}

	class IStatusUpdateComparator implements Comparator<BaseActionInterface> {
		@Override
		public int compare(BaseActionInterface obj1, BaseActionInterface obj2) {
			int result = 0;
			if (obj1.getDuration() >= 0 && obj2.getDuration() >= 0) {
				Log.i("Compare", obj1.getDuration() + " | " + obj2.getDuration());
				result = obj1.getDuration() < obj2.getDuration() ? -1 : 0;
			}

			return result;
		}
	}

}
