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
package org.gots.allotment.adapter;

import java.util.List;

import org.gots.R;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GrowingSeed;
import org.gots.seed.adapter.ListGrowingSeedAdapter;
import org.gots.seed.view.SeedWidget;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAllotmentAdapter extends BaseAdapter {
    protected static final String TAG = "ListAllotmentAdapter";

    FragmentActivity mContext;

    private ListGrowingSeedAdapter listGrowingSeedAdapter;

    // private GridView listSeeds;

    private List<BaseAllotmentInterface> myAllotments;

    // private boolean isSelectable;
    //
    // private int currentSeedId;

    OnGrowingSeedClickListener clickListener;

    public interface OnGrowingSeedClickListener {
        public void onGrowingSeedClick(View view, GrowingSeed seedInterface);

        public void onGrowingSeedLongClick(View view, GrowingSeed seedInterface);

        public void onAllotmentMenuClick(View view, BaseAllotmentInterface allotmentInterface);

        public void onAllotmentClick(View v, BaseAllotmentInterface item);

    }

    public ListAllotmentAdapter(FragmentActivity mContext, List<BaseAllotmentInterface> allotments) {
        this.mContext = mContext;
        myAllotments = allotments;
    }

    public void setAllotments(List<BaseAllotmentInterface> allotments) {
        myAllotments = allotments;
        notifyDataSetChanged();
    };

    public void setOnGrowingSeedClickListener(OnGrowingSeedClickListener listener) {
        this.clickListener = listener;
    }

    @Override
    public int getCount() {
        return myAllotments.size();
    }

    @Override
    public BaseAllotmentInterface getItem(int position) {
        return myAllotments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myAllotments.get(position).getId();
    }

    public class Holder {
        public GridView seedGridView;

        public TextView allotmentName;

        public LinearLayout titlebar;

        public BaseAllotmentInterface allotment;

        public LinearLayout menu;


        // public LinearLayout allotmentBox;
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout ll = (LinearLayout) convertView;
        Holder holder;
        if (ll == null) {
            holder = new Holder();
            ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_allotments, parent, false);
            if (GotsPreferences.DEBUG) {
                TextView textView = new TextView(mContext);
                textView.setText("(" + getItem(position).getId() + ")" + getItem(position).getUUID());
                ll.addView(textView);
            }

            holder.seedGridView = (GridView) ll.findViewById(R.id.IdGrowingSeedList);
            holder.titlebar = (LinearLayout) ll.findViewById(R.id.idAllotmentTitlebar);
            holder.allotmentName = (TextView) ll.findViewById(R.id.textAllotmentName);
            holder.menu = (LinearLayout) ll.findViewById(R.id.idAllotmentMenu);
            // holder.allotmentBox = (LinearLayout) ll.findViewById(R.id.idAllotmentBoxLayout);

            holder.allotment = getItem(position);
            ll.setTag(holder);
            ll.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);
            // ll.setOnClickListener(this);

        } else
            holder = (Holder) ll.getTag();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int width;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }

        listGrowingSeedAdapter = new ListGrowingSeedAdapter(mContext, getItem(position).getSeeds());
        holder.seedGridView.setAdapter(listGrowingSeedAdapter);

        int layoutsize = 100;
        if (width <= 480)
            layoutsize = 50;
        int nbcolumn = width / layoutsize;
        if (nbcolumn < 1)
            nbcolumn = 1;
        holder.seedGridView.setNumColumns(nbcolumn);

        if (listGrowingSeedAdapter.getCount() > 0) {
            
            holder.seedGridView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    (holder.seedGridView.getCount() / nbcolumn + 1) * layoutsize + layoutsize));
        }else
            holder.seedGridView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, layoutsize));
            
        // holder.allotmentBox.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // clickListener.onAllotmentClick(v, getItem(position));
        // }
        // });
        holder.seedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof SeedWidget)
                    clickListener.onGrowingSeedClick(view,
                            ((ListGrowingSeedAdapter) ((GridView) parent).getAdapter()).getItem(position));
                else
                    clickListener.onAllotmentClick(view, getItem(position));
            }
        });
        holder.seedGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof SeedWidget)
                    clickListener.onGrowingSeedLongClick(view,
                            ((ListGrowingSeedAdapter) ((GridView) parent).getAdapter()).getItem(position));
                return true;
            }
        });
        // else
        // holder.listSeeds.setLayoutParams(new
        // LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
        // ((holder.listSeeds.getCount() / nbcolumn) + 1) * layoutsize));

        holder.allotmentName.setText(getItem(position).getName());

        // holder.titlebar.removeAllViews();

        holder.menu.setTag(holder);
        holder.menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListener.onAllotmentMenuClick(v, getItem(position));
            }
        });


        return ll;
    }

}
