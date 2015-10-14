package org.gots.seed.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.gots.R;
import org.gots.seed.BotanicSpecie;
import org.gots.seed.SeedUtil;
import org.gots.ui.view.MyTextView;

import java.io.File;
import java.util.List;
import java.util.Locale;

public class ListSpeciesAdapter extends BaseAdapter {
    private Context mContext;

    private List<BotanicSpecie> botanicSpecies;


    public ListSpeciesAdapter(Context context, List<BotanicSpecie> botanicSpecies) {
        mContext = context;
        this.botanicSpecies = botanicSpecies;
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
        public MyTextView textViewSpecies;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_species_simple, parent, false);
            holder.imageView = (ImageView) convertView.findViewById(R.id.imageViewSpecies);
            holder.textViewSpecies = (MyTextView) convertView.findViewById(R.id.textViewSpecies);

            convertView.setTag(holder);
        } else holder = (Holder) convertView.getTag();

        BotanicSpecie specie = getItem(position);
        if (specie.getFilepath() != null)
            holder.imageView.setImageBitmap(BitmapFactory.decodeFile(specie.getFilepath()));
        else {
            int vegetableImageRessource = mContext.getResources().getIdentifier(
                    "org.gots:drawable/specie_" + specie.getSpecieName().trim().toLowerCase(Locale.US).replaceAll("\\s", ""), null, null);
            holder.imageView.setImageResource(vegetableImageRessource);
        }

        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            holder.imageView.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.seed_selector));
        } else {
            holder.imageView.setBackground(mContext.getResources().getDrawable(
                    R.drawable.seed_selector));
        }
        holder.imageView.setTag(specie);

        holder.textViewSpecies.setText(SeedUtil.translateSpecie(mContext, specie));
        return convertView;
    }

}
