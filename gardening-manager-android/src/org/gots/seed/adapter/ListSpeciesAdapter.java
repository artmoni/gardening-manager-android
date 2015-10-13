package org.gots.seed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.BotanicSpecie;

import java.util.List;
import java.util.Locale;

public class ListSpeciesAdapter extends BaseAdapter {
    private Context mContext;

    private List<BotanicSpecie> botanicSpecies;

    private BaseSeed mSelectedSeed;

    public ListSpeciesAdapter(Context context, List<BotanicSpecie> botanicSpecies, BaseSeed newSeed) {
        mContext = context;
        this.botanicSpecies = botanicSpecies;
        mSelectedSeed = newSeed;
    }

    @Override
    public int getCount() {
        return botanicSpecies.size();
    }

    @Override
    public BotanicSpecie getItem(int position) {
        return botanicSpecies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class Holder {
        public ImageView imageView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // SeedWidget seedWidget = new SeedWidget(mContext);
        // BaseSeed seed = new GrowingSeed();
        // seed.setSpecie(getItem(position));
        // seedWidget.setSeed(seed);
        //
        Holder holder;
        if (convertView == null) {
            holder = new Holder();
            convertView = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_species_simple, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewSpecies);
            convertView.setTag(holder);
        } else holder = (Holder) convertView.getTag();

        BotanicSpecie specie = getItem(position);
        int vegetableImageRessource = mContext.getResources().getIdentifier(
                "org.gots:drawable/specie_" + specie.getSpecieName().trim().toLowerCase(Locale.US).replaceAll("\\s", ""), null, null);
        holder.imageView.setImageResource(vegetableImageRessource);

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.seed_selector));
            if (specie.equals(mSelectedSeed.getSpecie())) {
                holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_state_warning));
            }
        } else {
            holder.imageView.setBackground(mContext.getResources().getDrawable(
                    R.drawable.seed_selector));
            if (specie.equals(mSelectedSeed.getSpecie())) {
                holder.imageView.setBackground(mContext.getResources().getDrawable(
                        R.drawable.bg_state_warning));
            }
        }
        holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.seed_selector));
        if (specie.equals(mSelectedSeed.getSpecie())) {
            holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_state_warning));
        }
        holder.imageView.setTag(specie);
        return convertView  ;
    }

}
