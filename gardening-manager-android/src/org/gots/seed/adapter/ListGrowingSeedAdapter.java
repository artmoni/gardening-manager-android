/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * Copyright (c) 2012 sfleury.
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed.adapter;

import java.util.List;

import org.gots.R;
import org.gots.seed.GrowingSeed;
import org.gots.seed.view.SeedWidget;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListGrowingSeedAdapter extends BaseAdapter {
    Context mContext;

    List<GrowingSeed> mySeeds;

    // String currentAllotmentReference;

    public ListGrowingSeedAdapter(Context mContext, List<GrowingSeed> seeds) {
        this.mContext = mContext;
        mySeeds = seeds;

    }

    @Override
    public int getCount() {
        return mySeeds.size() + 1;
    }

    @Override
    public GrowingSeed getItem(int position) {
        if (position < getCount()-1)
            return mySeeds.get(position);
        else
            return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (position < getCount()-1) {
                GrowingSeed currentSeed = (GrowingSeed) getItem(position);

                SeedWidget widget = new SeedWidget(mContext);
                widget.setSeed(currentSeed);
                widget.setTag(currentSeed);
//                widget.setLayoutParams(new LinearLayout.LayoutParams(80, 100));
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    widget.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));
                } else {
                    widget.setBackground(mContext.getResources().getDrawable(R.drawable.family_unknown));
                }
                convertView = widget;
            } else {
                View buttonPlus = LayoutInflater.from(mContext).inflate(R.layout.button_add, parent, false);
                convertView = buttonPlus;
            }
        }
        return convertView;

    }

    static class SeedViewHolder {
        TextView seedName;

        TextView seedHarvestPeriod;

        TextView seedSowingPeriod;
    }

}
