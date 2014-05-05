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

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.action.bean.SowingAction;
import org.gots.action.view.ActionWidget;
import org.gots.allotment.AllotmentManager;
import org.gots.allotment.view.QuickAllotmentActionBuilder;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.adapter.ListGrowingSeedAdapter;
import org.gots.ui.HutActivity;
import org.gots.ui.MyMainGarden;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAllotmentAdapter extends BaseAdapter implements OnClickListener {
    Context mContext;

    private ListGrowingSeedAdapter listGrowingSeedAdapter;

    // private GridView listSeeds;

    private List<BaseAllotmentInterface> myAllotments;

    private boolean isSelectable;

    private int currentSeedId;

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    public ListAllotmentAdapter(Context mContext, List<BaseAllotmentInterface> allotments, Bundle bundle) {
        this.mContext = mContext;
        myAllotments = allotments;
        if (bundle != null) {
            isSelectable = bundle.getBoolean(MyMainGarden.SELECT_ALLOTMENT);
            currentSeedId = bundle.getInt(MyMainGarden.VENDOR_SEED_ID);
        }
    }

    public void setAllotments(List<BaseAllotmentInterface> allotments) {
        myAllotments = allotments;
        notifyDataSetChanged();
    };

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
        public GridView listSeeds;

        public LinearLayout menu;
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

            holder.listSeeds = (GridView) ll.findViewById(R.id.IdGrowingSeedList);
            holder.menu = (LinearLayout) ll.findViewById(R.id.idMenuAllotment);
            ll.setTag(holder);
            ll.setDescendantFocusability(LinearLayout.FOCUS_BLOCK_DESCENDANTS);
        } else
            holder = (Holder) ll.getTag();

        //
        // new AsyncTask<LinearLayout, Integer, List<GrowingSeedInterface>>() {
        // Holder holder;
        //
        // @Override
        // protected List<GrowingSeedInterface> doInBackground(LinearLayout... params) {
        // holder = (Holder) params[0].getTag();
        // return growingSeedManager.getGrowingSeedsByAllotment(myAllotments.get(position));
        //
        // }
        //
        // protected void onPostExecute(List<GrowingSeedInterface> mySeeds) {
        //
        // };
        // }.execute(ll);
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
        int layoutsize = 200;
        int nbcolumn = (width - 200) / layoutsize;
        if (nbcolumn < 1)
            nbcolumn = 1;
        holder.listSeeds.setNumColumns(nbcolumn);

        listGrowingSeedAdapter = new ListGrowingSeedAdapter(mContext, getItem(position).getSeeds());
        holder.listSeeds.setAdapter(listGrowingSeedAdapter);

        // holder.listSeeds.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
        // layoutsize));
        // if (holder.listSeeds.getCount() % nbcolumn == 0)
        holder.listSeeds.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                (holder.listSeeds.getCount() / nbcolumn + 1) * layoutsize));
        // else
        // holder.listSeeds.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
        // ((holder.listSeeds.getCount() / nbcolumn) + 1) * layoutsize));

        holder.menu.removeAllViews();

        SowingAction sow = new SowingAction(mContext);
        ActionWidget widget = new ActionWidget(mContext, sow);
        widget.setTag(position);
        if (isSelectable) {
            Animation myFadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.tween);

            widget.startAnimation(myFadeInAnimation);
            widget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Integer, GrowingSeedInterface>() {
                        @Override
                        protected GrowingSeedInterface doInBackground(Void... params) {

                            GotsGrowingSeedManager provider = GotsGrowingSeedManager.getInstance().initIfNew(mContext);
                            GotsSeedManager seedManager = GotsSeedManager.getInstance().initIfNew(mContext);
                            // NuxeoGrowingSeedProvider provider = new NuxeoGrowingSeedProvider(mContext);
                            GrowingSeedInterface growingSeed = (GrowingSeedInterface) seedManager.getSeedById(currentSeedId);
                            growingSeed.setDateSowing(Calendar.getInstance().getTime());

                            return provider.insertSeed(growingSeed, getItem(position));
                        }

                        @Override
                        protected void onPostExecute(GrowingSeedInterface seed) {
                            // notifyDataSetChanged();
                            Toast.makeText(mContext, "Sowing" + " " + SeedUtil.translateSpecie(mContext, seed),
                                    Toast.LENGTH_LONG).show();
                            mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
                            ((Activity) mContext).finish();
                        }
                    }.execute();
                }
            });
        } else {
            widget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    AllotmentManager.getInstance().setCurrentAllotment(getItem(Integer.valueOf(v.getTag().toString())));

                    Intent i = new Intent(mContext, HutActivity.class);
                    i.putExtra("org.gots.allotment.reference", getItem(Integer.valueOf(v.getTag().toString())).getId());
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                }
            });
        }

        widget.setPadding(4, 4, 4, 8);
        holder.menu.addView(widget);

        return ll;
    }

    @Override
    public void onClick(View v) {
        QuickAllotmentActionBuilder actionsBuilder = new QuickAllotmentActionBuilder(v);
        actionsBuilder.show();

    }
}
