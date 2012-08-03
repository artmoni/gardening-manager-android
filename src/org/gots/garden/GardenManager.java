package org.gots.garden;

import java.util.Iterator;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.bean.Garden;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.providers.simple.SimpleConnector;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.sql.VendorSeedDBHelper;

import android.content.Context;
import android.content.SharedPreferences;

public class GardenManager {
	private SharedPreferences preferences;
	private Context mContext;

	public GardenManager(Context mContext) {
		this.mContext = mContext;
		preferences = mContext.getSharedPreferences("org.gots.preference", 0);

	}

	public long addGarden() {

		Garden garden = new Garden();
		garden.setLocality("Non localisé");

		GardenDBHelper helper = new GardenDBHelper(mContext);
		GardenInterface newGarden = helper.insertGarden(garden);

		changeDatabase((int) newGarden.getId());

		refreshData();

		return newGarden.getId();
	}

	private void changeDatabase(int position) {
		DatabaseHelper helper = new DatabaseHelper(mContext);
		helper.setDatabase(position);

		SharedPreferences.Editor prefedit = preferences.edit();
		prefedit.putInt("org.gots.preference.gardenid", position);
		prefedit.commit();

	}

	public GardenInterface getcurrentGarden() {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		int gardenId = preferences.getInt("org.gots.preference.gardenid", 0);
		GardenInterface garden = helper.getGarden(gardenId);
		return garden;
	}

	public void selectGarden(int position) {
		changeDatabase(position);
	}

	public void refreshData() {
		SimpleConnector connector = new SimpleConnector();
		List<BaseSeedInterface> seeds = connector.getAllSeeds();

		VendorSeedDBHelper theSeedBank = new VendorSeedDBHelper(mContext);
		for (Iterator iterator = seeds.iterator(); iterator.hasNext();) {
			BaseSeedInterface baseSeedInterface = (BaseSeedInterface) iterator.next();
			if (theSeedBank.getSeedByReference(baseSeedInterface.getReference()) == null)
				theSeedBank.insertSeed(baseSeedInterface);

		}
	}
}
