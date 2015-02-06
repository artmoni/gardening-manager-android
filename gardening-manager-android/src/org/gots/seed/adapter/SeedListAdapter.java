package org.gots.seed.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.gots.R;
import org.gots.action.adapter.comparator.ISeedSpecieComparator;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.view.SeedWidgetTile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

public abstract class SeedListAdapter extends BaseAdapter implements Filterable {

    HolderFilter holderFilter;

    protected List<BaseSeedInterface> vendorSeeds;

    protected List<BaseSeedInterface> vendorSeedsFilter;

    protected Context mContext;

    protected LayoutInflater inflater;

    public SeedListAdapter(Context context, List<BaseSeedInterface> vendorSeeds) {
        this.vendorSeeds = vendorSeeds;
        vendorSeedsFilter = new ArrayList<BaseSeedInterface>();
        vendorSeedsFilter.addAll(vendorSeeds);
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Collections.sort(vendorSeeds, new ISeedSpecieComparator(context));

    }

    public SeedListAdapter() {
        super();
    }

    public class Holder {
        public SeedWidgetTile seedWidgetTile;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_seed, null);
            holder = new Holder();
            holder.seedWidgetTile = (SeedWidgetTile) convertView.findViewById(R.id.idSeedWidgetLong);
            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();

        holder.seedWidgetTile.setSeed(getItem(position));
        holder.seedWidgetTile.setTag(holder);
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (holderFilter == null) {
            holderFilter = new HolderFilter();
        }
        return holderFilter;
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
    public void notifyDataSetInvalidated() {
        vendorSeedsFilter = vendorSeeds;
        super.notifyDataSetInvalidated();
    }

    class HolderFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<BaseSeedInterface> nHolderList = new ArrayList<BaseSeedInterface>();
            if (constraint == null || constraint.length() == 0) {
                results.values = vendorSeeds;
                results.count = vendorSeeds.size();
            } else if ("LIKE".equals(constraint)) {
                for (BaseSeedInterface seed : vendorSeeds) {
                    if (seed.getLikeStatus() != null && seed.getLikeStatus().getUserLikeStatus() > 0)
                        nHolderList.add(seed);
                }
                results.values = nHolderList;
                results.count = nHolderList.size();

            } else if ("THISMONTH".equals(constraint)) {
                for (BaseSeedInterface seed : vendorSeeds) {
                    int month = Calendar.getInstance().get(Calendar.MONTH);
                    if (seed.getDateSowingMin() >= month && seed.getDateSowingMax() <= month)
                        nHolderList.add(seed);
                }
                results.values = nHolderList;
                results.count = nHolderList.size();

            } else {
                for (BaseSeedInterface seed : vendorSeeds) {
                    if (SeedUtil.translateSpecie(mContext, seed).toUpperCase().startsWith(
                            constraint.toString().toUpperCase())
                            || seed.getVariety().toUpperCase().startsWith(constraint.toString().toUpperCase())
                            || constraint.toString().equals(seed.getBareCode()))
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

}