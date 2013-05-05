package org.gots.seed.adapter;

import org.gots.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ListSpeciesAdapter extends BaseAdapter {
	// private List<BaseSeedInterface> allSpecies = new
	// ArrayList<BaseSeedInterface>();
	private Context mContext;
	private String[] mSpecieList;
	private String mSelectedSpecies;

	public ListSpeciesAdapter(Context context, String[] specieList, String selectedSpecies) {
		mContext = context;
		mSpecieList = specieList;
		mSelectedSpecies = selectedSpecies;
	}

	@Override
	public int getCount() {
		return mSpecieList.length;
	}

	@Override
	public String getItem(int position) {
		return mSpecieList[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
				"org.gots:drawable/specie_" + specie.trim().toLowerCase().replaceAll("\\s", ""), null, null);
		ImageView v = new ImageView(mContext);
		v.setImageResource(vegetableImageRessource);
		v.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));
		v.setTag(specie);
		
		return v;
	}

}
