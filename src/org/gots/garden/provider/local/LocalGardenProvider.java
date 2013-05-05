package org.gots.garden.provider.local;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.sql.GardenDBHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalGardenProvider implements GardenProvider {

	private SharedPreferences preferences;
	private Context mContext;

	public LocalGardenProvider(Context context) {
		mContext = context;
		preferences = mContext.getSharedPreferences("org.gots.preference", 0);
	}

	
	
	
	
	@Override
	public GardenInterface createGarden(GardenInterface garden) {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		GardenInterface newGarden = helper.insertGarden(garden);
		return newGarden;
	}

	@Override
	public GardenInterface getCurrentGarden() {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		int gardenId = preferences.getInt("org.gots.preference.gardenid", 0);
		GardenInterface garden = helper.getGarden(gardenId);
		return garden;
	}

	@Override
	public List<GardenInterface> getMyGardens() {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		return helper.getGardens();
	}
	@Override
	public int removeGarden(GardenInterface garden) {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		helper.deleteGarden(garden);
		return 0;
	}

}
