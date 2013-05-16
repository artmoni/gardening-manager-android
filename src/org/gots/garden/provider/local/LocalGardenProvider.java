package org.gots.garden.provider.local;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.sql.GardenDBHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalGardenProvider implements GardenProvider {

	protected SharedPreferences preferences;
	protected Context mContext;
	protected GardenDBHelper helper;

	public LocalGardenProvider(Context context) {
		mContext = context;
		preferences = mContext.getSharedPreferences("org.gots.preference", 0);
		helper = new GardenDBHelper(mContext);

	}

	@Override
	public GardenInterface createGarden(GardenInterface garden) {
		GardenInterface newGarden = helper.insertGarden(garden);
		return newGarden;
	}

	@Override
	public GardenInterface updateGarden(GardenInterface garden) {
		GardenInterface newGarden = helper.updateGarden(garden);
		return newGarden;
	}

	@Override
	public GardenInterface getCurrentGarden() {
		int gardenId = preferences.getInt("org.gots.preference.gardenid", 0);
		GardenInterface garden = helper.getGarden(gardenId);
		return garden;
	}

	@Override
	public List<GardenInterface> getMyGardens() {
		return helper.getGardens();
	}

	@Override
	public int removeGarden(GardenInterface garden) {
		helper.deleteGarden(garden);
		return 0;
	}

}
