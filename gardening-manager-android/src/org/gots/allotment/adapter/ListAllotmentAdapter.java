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
import org.gots.allotment.view.QuickAllotmentActionBuilder;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.seed.adapter.ListGrowingSeedAdapter;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.GardenActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar.LayoutParams;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAllotmentAdapter extends BaseAdapter {
    protected static final String TAG = "ListAllotmentAdapter";

    FragmentActivity mContext;

    private ListGrowingSeedAdapter listGrowingSeedAdapter;

    // private GridView listSeeds;

    private List<BaseAllotmentInterface> myAllotments;

    private boolean isSelectable;

    private int currentSeedId;

    OnGrowingSeedClickListener clickListener;

    public interface OnGrowingSeedClickListener {
        public void onGrowingSeedClick(View view, GrowingSeed seedInterface);

        public void onGrowingSeedLongClick(View view, GrowingSeed seedInterface);

        public void onAllotmentMenuClick(View view, BaseAllotmentInterface allotmentInterface);

    }

    public ListAllotmentAdapter(FragmentActivity mContext, List<BaseAllotmentInterface> allotments, Bundle bundle) {
        this.mContext = mContext;
        myAllotments = allotments;
        if (bundle != null) {
            isSelectable = bundle.getBoolean(GardenActivity.SELECT_ALLOTMENT);
            currentSeedId = bundle.getInt(GardenActivity.VENDOR_SEED_ID);
        }
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
        int layoutsize = 150;
        if (width <= 480)
            layoutsize = 50;
        int nbcolumn = (width - 200) / layoutsize;
        if (nbcolumn < 1)
            nbcolumn = 1;
        holder.seedGridView.setNumColumns(nbcolumn);

        listGrowingSeedAdapter = new ListGrowingSeedAdapter(mContext, getItem(position).getSeeds());
        holder.seedGridView.setAdapter(listGrowingSeedAdapter);
        holder.seedGridView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, layoutsize));
        if (listGrowingSeedAdapter.getCount() > 0) {
            holder.seedGridView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    (holder.seedGridView.getCount() / nbcolumn + 1) * layoutsize + layoutsize));
            // holder.listSeeds.setLayoutParams(new
            // LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
            // LayoutParams.WRAP_CONTENT));
        }
        holder.seedGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickListener.onGrowingSeedClick(view,
                        ((ListGrowingSeedAdapter) ((GridView) parent).getAdapter()).getItem(position));

            }
        });
        holder.seedGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
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
        // SowingAction sow = new SowingAction(mContext);
        // ActionWidget widget = new ActionWidget(mContext, sow);
        // widget.setTag(position);

        if (isSelectable) {
            holder.menu.setBackgroundResource(R.anim.rotate_alerte);
            // Animation myFadeInAnimation =
            // AnimationUtils.loadAnimation(mContext, R.anim.rotate_alerte);
            // menu.startAnimation(myFadeInAnimation);
            AnimationDrawable frameAnimation = (AnimationDrawable) holder.menu.getBackground();
            frameAnimation.start();
            holder.menu.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (clickListener != null) {
                        clickListener.onAllotmentMenuClick(v, getItem(position));
                    }
                }
            });
        }

        // widget.setPadding(4, 4, 4, 8);
        // holder.menu.addView(widget);

        // // SowingAction sow = new SowingAction(mContext);
        // ImageView widgetSensor = new ImageView(mContext);
        // widgetSensor.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_sensor));
        // widgetSensor.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));
        // widgetSensor.setTag(position);
        // widgetSensor.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        //
        // GotsPurchaseItem purchaseItem = new GotsPurchaseItem(mContext);
        //
        // // if (!purchaseItem.getFeatureParrot() ? true :
        // purchaseItem.isPremium()) {
        // if (!purchaseItem.getFeatureParrot() || purchaseItem.isPremium()) {
        // FragmentManager fm = mContext.getSupportFragmentManager();
        // GotsBillingDialog editNameDialog = new
        // GotsBillingDialog(GotsPurchaseItem.SKU_FEATURE_PARROT);
        // editNameDialog.setStyle(DialogFragment.STYLE_NORMAL,
        // R.style.CustomDialog);
        // editNameDialog.show(fm, "fragment_edit_name");
        // } else {
        // Intent sensorIntent = new Intent(mContext, SensorActivity.class);
        // mContext.startActivity(sensorIntent);
        // }// new AsyncTask<Void, Void, List<ParrotLocation>>() {
        // // private LocationListAdapter sensorListAdapter;
        // //
        // // List<ParrotSampleFertilizer> samplesFertilizer = null;
        // //
        // // List<ParrotSampleTemperature> samplesTemp = null;
        // //
        // // @Override
        // // protected List<ParrotLocation> doInBackground(Void... params) {
        // // ParrotSensorProvider sensorProvider = new
        // ParrotSensorProvider(mContext);
        // // List<ParrotLocation> locations = sensorProvider.getLocations();
        // // sensorProvider.getStatus();
        // // samplesFertilizer =
        // sensorProvider.getSamples(locations.get(0).getLocation_identifier());
        // // samplesTemp =
        // sensorProvider.getSamples2(locations.get(0).getLocation_identifier());
        // //
        // // return locations;
        // // }
        // //
        // // protected void onPostExecute(List<ParrotLocation> result) {
        // // // sensorListAdapter = new SensorListAdapter(mContext, result);
        // // sensorListAdapter = new LocationListAdapter(mContext, result);
        // // // new AlertDialog.Builder(mContext).setAdapter(sensorListAdapter,
        // // // new DialogInterface.OnClickListener() {
        // // //
        // // // @Override
        // // // public void onClick(DialogInterface dialog, int which) {
        // // // Toast.makeText(mContext,
        // sensorListAdapter.getItem(which).getSensor_serial(),
        // // // Toast.LENGTH_SHORT).show();
        // // // ;
        // // // }
        // // // }).show();
        // //
        // // Intent sensorIntent = new Intent(mContext, SensorActivity.class);
        // // mContext.startActivity(sensorIntent);
        // //
        // // if (samplesFertilizer != null) {
        // // WebView webView = new WebView(mContext);
        // // String chd = new String();
        // // for (ParrotSampleFertilizer fertilizer : samplesFertilizer) {
        // // chd = chd.concat(String.valueOf(fertilizer.getFertilizer_level() *
        // 100));
        // // chd = chd.concat(",");
        // // }
        // // chd = chd.substring(0, chd.length() - 1);
        // // String url =
        // "http://chart.apis.google.com/chart?cht=ls&chs=250x100&chd=t:" + chd;
        // // webView.loadUrl(url);
        // // Log.d(ListAllotmentAdapter.class.getName(), url);
        // // AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        // // alert.setView(webView);
        // // alert.show();
        // // }
        // // if (samplesTemp != null) {
        // // WebView webView = new WebView(mContext);
        // // String chd = new String();
        // // int i = 0;
        // // for (ParrotSampleTemperature sampleTemp : samplesTemp) {
        // // chd =
        // chd.concat(String.valueOf(sampleTemp.getAir_temperature_celsius()));
        // // chd = chd.concat(",");
        // // if (i++ >= 50)
        // // break;
        // // }
        // // chd = chd.substring(0, chd.length() - 1);
        // // String url =
        // "http://chart.apis.google.com/chart?cht=ls&chs=250x100&chd=t:" + chd;
        // // webView.loadUrl(url);
        // // Log.d(ListAllotmentAdapter.class.getName(), url);
        // // AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        // // alert.setView(webView);
        // // alert.show();
        // // }
        // // };
        // // }.execute();
        // }
        // });
        //
        // widgetSensor.setPadding(4, 4, 4, 8);
        // holder.menu.addView(widgetSensor);
        return ll;
    }

}
