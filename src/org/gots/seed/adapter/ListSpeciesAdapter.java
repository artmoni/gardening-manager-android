package org.gots.seed.adapter;

import java.util.Locale;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListSpeciesAdapter extends BaseAdapter {
    private Context mContext;

    private String[] mSpecies;

    private BaseSeedInterface mSelectedSeed;

    public ListSpeciesAdapter(Context context, String[] species,
            BaseSeedInterface newSeed) {
        mContext = context;
        mSpecies = species;
        mSelectedSeed = newSeed;
    }

    @Override
    public int getCount() {
        return mSpecies.length;
    }

    @Override
    public String getItem(int position) {
        return mSpecies[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // SeedWidget seedWidget = new SeedWidget(mContext);
        // BaseSeedInterface seed = new GrowingSeed();
        // seed.setSpecie(getItem(position));
        // seedWidget.setSeed(seed);
        //
        String specie = getItem(position);
        int vegetableImageRessource = mContext.getResources().getIdentifier(
                "org.gots:drawable/specie_"
                        + specie.trim().toLowerCase(Locale.US).replaceAll(
                                "\\s", ""), null, null);
        ImageView v = new ImageView(mContext);
        v.setImageResource(vegetableImageRessource);
        v.setBackgroundDrawable(mContext.getResources().getDrawable(
                R.drawable.seed_selector));
        if (specie.equals(mSelectedSeed.getSpecie())) {
            v.setBackgroundDrawable(mContext.getResources().getDrawable(
                    R.drawable.bg_state_warning));
        }
        v.setTag(specie);
        return v;
    }

}
