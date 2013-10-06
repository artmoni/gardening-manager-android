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

import javax.xml.ws.Holder;

import org.gots.R;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.comparator.ISeedSpecieComparator;
import org.gots.action.bean.BuyingAction;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.ads.GotsAdvertisement;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.view.SeedWidgetLong;
import org.gots.ui.NewSeedActivity;

import com.google.ads.ac;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;

public class ListVendorSeedAdapter extends BaseAdapter implements Filterable {

    private BuyingAction buying;

    private LayoutInflater inflater;

    private Context mContext;

    private List<BaseSeedInterface> vendorSeeds;

    private List<BaseSeedInterface> vendorSeedsFilter;

    // private int nbAds = 0;
    // private int frequencyAds = 4;
    private View adsView;

    private HolderFilter holderFilter;

    public ListVendorSeedAdapter(Context context, List<BaseSeedInterface> vendorSeeds) {
        // super(context);
        this.vendorSeeds = vendorSeeds;
        vendorSeedsFilter = new ArrayList<BaseSeedInterface>();
        vendorSeedsFilter.addAll(vendorSeeds);
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Collections.sort(vendorSeeds, new ISeedSpecieComparator(context));

        GotsAdvertisement ads = new GotsAdvertisement(mContext);
        adsView = ads.getAdsLayout();

    }

    public class Holder {
        public SeedWidgetLong seedWidgetLong;

        public ActionWidget actionWidget;

        public LinearLayout actionBox;
        // public Holder( SeedWidgetLong seedWidgetLong, ActionWidget actionWidget) {
        // this.seedWidgetLong=seedWidgetLong;
        // this.actionWidget=actionWidget;
        // }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        final BaseSeedInterface currentSeed = getItem(position);
        Holder holder;

        if (vi == null) {
            vi = inflater.inflate(R.layout.list_seed, null);
            holder = new Holder();
            holder.actionWidget = (ActionWidget) vi.findViewById(R.id.IdSeedAction);
            holder.seedWidgetLong = (SeedWidgetLong) vi.findViewById(R.id.idSeedWidgetLong);
            holder.actionBox = (LinearLayout) convertView.findViewById(R.id.IdSeedAction);
            vi.setTag(holder);
        } else
            holder = (Holder) vi.getTag();

        holder.seedWidgetLong.setSeed(currentSeed);
        holder.seedWidgetLong.setTag(holder.actionBox);
        holder.seedWidgetLong.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                View actionsView = (View) v.getTag();
                if (actionsView == null)
                    return;
                if (actionsView.getVisibility() == View.VISIBLE)
                    actionsView.setVisibility(View.GONE);
                else
                    actionsView.setVisibility(View.VISIBLE);

            }
        });
        buying = new BuyingAction(mContext);
        buying.setState(ActionState.NORMAL);
        holder.actionWidget.setAction(buying);
        holder.actionWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        SeedActionInterface action = buying;
                        action.execute((GrowingSeedInterface) currentSeed);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // notifyDataSetChanged();
                        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));

                        super.onPostExecute(result);
                    }
                }.execute();

                // Toast.makeText(getContext(),
                // action.getName() + " " + currentSeed.getSpecie() + " " +
                // currentSeed.getVariety(), 30).show();
            }
        });
        // actionWidget.setOnActionItemClickListener(new
        // ActionWidget.OnActionItemClickListener() {
        //
        // @Override
        // public void onItemClick(ActionWidget source, BaseActionInterface
        // baseActionInterface) {
        // SeedActionInterface action = (SeedActionInterface)
        // baseActionInterface;
        // action.execute((GrowingSeedInterface) currentSeed);
        // Toast.makeText(getContext(),
        // action.getName() + " " + currentSeed.getSpecie() + " " +
        // currentSeed.getVariety(), 30).show();
        // notifyDataSetChanged();
        // }
        // });

        Calendar sowTime = Calendar.getInstance();
        if (sowTime.get(Calendar.MONTH) > currentSeed.getDateSowingMin())
            sowTime.set(Calendar.YEAR, sowTime.get(Calendar.YEAR) + 1);
        sowTime.set(Calendar.MONTH, currentSeed.getDateSowingMin());

        Calendar harvestTime = new GregorianCalendar();
        harvestTime.setTime(sowTime.getTime());
        harvestTime.add(Calendar.DAY_OF_MONTH, currentSeed.getDurationMin());

        holder.seedWidgetLong.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(mContext, NewSeedActivity.class);
                i.putExtra("org.gots.seedid", currentSeed.getSeedId());
                mContext.startActivity(i);
                return false;
            }
        });

        return vi;

    }

    @Override
    public void notifyDataSetChanged() {
        // VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
        // vendorSeeds = myBank.getVendorSeeds();

        super.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return vendorSeedsFilter.size();
    }

    @Override
    public BaseSeedInterface getItem(int position) {

        return vendorSeedsFilter.get(position);
    }

    @Override
    public long getItemId(int position) {

        return vendorSeedsFilter.get(position).getSeedId();
    }

    public void setSeeds(List<BaseSeedInterface> vendorSeeds) {
        this.vendorSeeds = vendorSeeds;
        vendorSeedsFilter = new ArrayList<BaseSeedInterface>();
        vendorSeedsFilter.addAll(vendorSeeds);
    }

    @Override
    public Filter getFilter() {
        if (holderFilter == null) {
            holderFilter = new HolderFilter();
        }
        return holderFilter;
    }

    private class HolderFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = vendorSeeds;
                results.count = vendorSeeds.size();
            } else {
                List<BaseSeedInterface> nHolderList = new ArrayList<BaseSeedInterface>();
                for (BaseSeedInterface seed : vendorSeeds) {
                    if (SeedUtil.translateSpecie(mContext, seed).toUpperCase().startsWith(
                            constraint.toString().toUpperCase()))
                        nHolderList.add(seed);
                }
                results.values = nHolderList;
                results.count = nHolderList.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                vendorSeedsFilter = (ArrayList<BaseSeedInterface>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    @Override
    public void notifyDataSetInvalidated() {
        vendorSeedsFilter = vendorSeeds;
        super.notifyDataSetInvalidated();
    }
}
