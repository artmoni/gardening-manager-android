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

import org.gots.R;
import org.gots.action.view.ActionWidget;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;
import org.gots.seed.view.SeedWidgetLong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class CopyOfMySeedsListAdapter extends BaseAdapter implements OnClickListener {
	private Context mContext;
	private ArrayList<BaseSeedInterface> mySeeds = new ArrayList<BaseSeedInterface>();
	private BaseAllotmentInterface allotment;
	private SeedWidgetLong seedWidget;
	private ActionWidget actionWidget;

	// final private static int nbActionToDisplay = 5;

	public CopyOfMySeedsListAdapter(Context mContext, BaseAllotmentInterface allotment) {
		this.mContext = mContext;
		// this.mySeeds.addAll(mySeeds);
		this.allotment = allotment;
		VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
		mySeeds = myBank.getMySeeds();
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
//		mySeeds.get(position).getId()
	}

	// public static class ViewHolder {
	// private TextView seedSpecie;
	// private SeedWidget seedWidget;
	// private TextView seedVariety;
	// private ActionWidget actionWidget;
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// final ViewHolder holder;
		 
		 LinearLayout ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_seed, parent, false);
		BaseSeedInterface currentSeed = getItem(5);
		
		seedWidget = (SeedWidgetLong) ll.findViewById(R.id.idSeedWidgetLong);
		seedWidget.setSeed(currentSeed);
	    
		
		
		/*
		LinearLayout ll;
		if (convertView == null){
			ll= new LinearLayout(mContext);
//			li = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_seed, parent, false);
            LayoutInflater vi;
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            vi = (LayoutInflater)mContext.getSystemService(inflater);
            vi.inflate(R.layout.list_seed, ll, true);
            
            seedWidget = (SeedWidgetLong) ll.findViewById(R.id.idSeedWidgetLong);
            seedWidget.setSeed(currentSeed);
            
		}else
			ll = (LinearLayout) convertView;
	
		*/
		return ll;
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
