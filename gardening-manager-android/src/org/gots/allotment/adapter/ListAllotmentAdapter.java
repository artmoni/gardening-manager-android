/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.allotment.adapter;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gots.R;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GrowingSeed;
import org.gots.seed.adapter.ListGrowingSeedAdapter;
import org.gots.seed.view.GrowingSeedWidget;
import org.gots.ui.ExpandableHeightGridView;

import java.util.List;

public class ListAllotmentAdapter extends BaseAdapter {
    protected static final String TAG = "ListAllotmentAdapter";

    FragmentActivity mContext;
    OnGrowingSeedClickListener clickListener;
    private ListGrowingSeedAdapter listGrowingSeedAdapter;
    private List<BaseAllotmentInterface> myAllotments;

    public ListAllotmentAdapter(FragmentActivity mContext, List<BaseAllotmentInterface> allotments) {
        this.mContext = mContext;
        myAllotments = allotments;
    }

    public void setAllotments(List<BaseAllotmentInterface> allotments) {
        myAllotments = allotments;
        notifyDataSetChanged();
    }

    public void setOnGrowingSeedClickListener(OnGrowingSeedClickListener listener) {
        this.clickListener = listener;
    }

    ;

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

            holder.seedGridView = (ExpandableHeightGridView) ll.findViewById(R.id.IdGrowingSeedList);
            holder.titlebar = (LinearLayout) ll.findViewById(R.id.idAllotmentTitlebar);
            holder.allotmentName = (TextView) ll.findViewById(R.id.textAllotmentName);
            holder.menu = (LinearLayout) ll.findViewById(R.id.idAllotmentMenu);

            holder.allotment = getItem(position);
            ll.setTag(holder);
            ll.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);

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
        holder.seedGridView.setExpanded(true);
//        int layoutsize = holder.seedGridView.getChildAt(0).getWidth();
//        if (width <= 480)
//            layoutsize = 50;
//        int nbcolumn = width / layoutsize;
//        if (nbcolumn < 1)
//            nbcolumn = 1;
//        holder.seedGridView.setNumColumns(nbcolumn);
//
//        if (listGrowingSeedAdapter.getCount() > 0) {
//
//            holder.seedGridView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
//                    (holder.seedGridView.getCount() / nbcolumn + 1) * layoutsize + layoutsize));
//        } else
//            holder.seedGridView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, layoutsize));

        holder.seedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int positionSeed, long id) {
                if (view instanceof GrowingSeedWidget)
                    clickListener.onGrowingSeedClick(view,
                            ((ListGrowingSeedAdapter) ((GridView) parent).getAdapter()).getItem(positionSeed));
                else
                    clickListener.onAllotmentClick(view, getItem(position));
            }
        });
        holder.seedGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int positionSeed, long id) {
                if (view instanceof GrowingSeedWidget)
                    clickListener.onGrowingSeedLongClick(view,
                            ((ListGrowingSeedAdapter) ((GridView) parent).getAdapter()).getItem(positionSeed));
                return true;
            }
        });

        holder.allotmentName.setText(getItem(position).getName());

        holder.menu.setTag(holder);
        holder.menu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                clickListener.onAllotmentMenuClick(v, getItem(position));
            }
        });

        return ll;
    }

    public interface OnGrowingSeedClickListener {
        public void onGrowingSeedClick(View view, GrowingSeed seedInterface);

        public void onGrowingSeedLongClick(View view, GrowingSeed seedInterface);

        public void onAllotmentMenuClick(View view, BaseAllotmentInterface allotmentInterface);

        public void onAllotmentClick(View v, BaseAllotmentInterface item);

    }

    public class Holder {
        public ExpandableHeightGridView seedGridView;

        public TextView allotmentName;

        public LinearLayout titlebar;

        public BaseAllotmentInterface allotment;

        public LinearLayout menu;
    }

}
