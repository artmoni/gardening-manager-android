package org.gots.seed.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gots.R;
import org.gots.action.adapter.comparator.ISeedSpecieComparator;
import org.gots.action.view.ActionWidget;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.view.SeedWidgetLong;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;

public abstract class SeedListAdapter extends BaseAdapter implements Filterable {

    HolderFilter holderFilter;

    protected List<BaseSeedInterface> vendorSeeds;

    protected List<BaseSeedInterface> vendorSeedsFilter;

    protected Context mContext;

    protected LayoutInflater inflater;

    private View adsView;

    public SeedListAdapter(Context context, List<BaseSeedInterface> vendorSeeds) {
        // super(context);
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
        public SeedWidgetLong seedWidgetLong;

        public ActionWidget actionWidget;

        public LinearLayout actionBox;

        public ImageView imageSelectedState;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_seed, null);
            holder = new Holder();
            holder.actionWidget = (ActionWidget) convertView.findViewById(R.id.IdSeedAction);
            holder.seedWidgetLong = (SeedWidgetLong) convertView.findViewById(R.id.idSeedWidgetLong);
            holder.actionBox = (LinearLayout) convertView.findViewById(R.id.IdActionsLayout);
            holder.imageSelectedState = (ImageView) convertView.findViewById(R.id.idSeedImageSelected);
            convertView.setTag(holder);
        } else
            holder = (Holder) convertView.getTag();

        holder.seedWidgetLong.setSeed(getItem(position));
        holder.seedWidgetLong.setTag(holder);
        holder.seedWidgetLong.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Holder holder = (Holder) v.getTag();
                if (holder.actionBox == null)
                    return;
                if (holder.actionBox.getVisibility() == View.VISIBLE) {
                    holder.actionBox.setVisibility(View.GONE);
                    holder.imageSelectedState.setSelected(false);
                } else {
                    holder.actionBox.setVisibility(View.VISIBLE);
                    holder.imageSelectedState.setSelected(true);
                }
            }
        });
        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        // VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
        // vendorSeeds = myBank.getVendorSeeds();

        super.notifyDataSetChanged();
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
            if (constraint == null || constraint.length() == 0) {
                results.values = vendorSeeds;
                results.count = vendorSeeds.size();
            } else {
                List<BaseSeedInterface> nHolderList = new ArrayList<BaseSeedInterface>();
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