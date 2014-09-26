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
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.view.QuickSeedActionBuilder;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.TabSeedActivity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListGrowingSeedAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener {
    Context mContext;

    List<GrowingSeedInterface> mySeeds;

    // String currentAllotmentReference;

    public ListGrowingSeedAdapter(Context mContext, List<GrowingSeedInterface> seeds) {
        this.mContext = mContext;
        mySeeds = seeds;

    }

    @Override
    public int getCount() {
        return mySeeds.size();
    }

    @Override
    public GrowingSeedInterface getItem(int position) {
        return mySeeds.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        SeedWidget seedWidget = (SeedWidget) convertView;
        if (convertView == null) {
            GrowingSeedInterface currentSeed = (GrowingSeedInterface) getItem(position);

            seedWidget = new SeedWidget(mContext);
            seedWidget.setSeed(currentSeed);
            seedWidget.setOnClickListener(this);
            seedWidget.setOnLongClickListener(this);
            seedWidget.setTag(currentSeed);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                seedWidget.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));
            } else {
                seedWidget.setBackground(mContext.getResources().getDrawable(R.drawable.family_unknown));
            }

            // seedWidget.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.parcelle_dark_bg));

        } else {

        }

        return seedWidget;
    }

    @Override
    public void notifyDataSetChanged() {
        // GrowingSeedDBHelper helper = new GrowingSeedDBHelper(mContext);
        // mySeeds = helper.getSeedsByAllotment(currentAllotmentReference);

        // parentAdapter.notifyDataSetChanged();
        super.notifyDataSetChanged();
    }

    static class SeedViewHolder {
        TextView seedName;

        TextView seedHarvestPeriod;

        TextView seedSowingPeriod;
    }

    @Override
    public void onClick(View v) {
        final Intent i = new Intent(mContext, TabSeedActivity.class);
        i.putExtra("org.gots.seed.id", ((GrowingSeedInterface) v.getTag()).getGrowingSeedId());
        i.putExtra("org.gots.seed.url", ((GrowingSeedInterface) v.getTag()).getUrlDescription());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }

    @Override
    public boolean onLongClick(View v) {
        QuickSeedActionBuilder actionBuilder = new QuickSeedActionBuilder(mContext, (SeedWidget) v);
        actionBuilder.show();
        return false;
    }

}
