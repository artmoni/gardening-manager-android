/**
 * ****************************************************************************
 * All rights reserved. This program and the accompanying materials
 * Copyright (c) 2012 sfleury.
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.seed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.gots.R;
import org.gots.seed.GrowingSeed;
import org.gots.seed.view.GrowingSeedWidget;

import java.util.List;

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
        if (position < getCount() - 1)
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
            if (position < getCount() - 1) {
                GrowingSeed growingSeed = getItem(position);

                GrowingSeedWidget growingSeedWidget = new GrowingSeedWidget(mContext);
                growingSeedWidget.setSeed(growingSeed);
                growingSeedWidget.setTag(growingSeed);
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    growingSeedWidget.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));
                } else {
                    growingSeedWidget.setBackground(mContext.getResources().getDrawable(R.drawable.family_unknown));
                }
                convertView = growingSeedWidget;
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
