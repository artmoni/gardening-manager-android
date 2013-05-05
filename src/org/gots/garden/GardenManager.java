package org.gots.garden;

import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.garden.sql.GardenDBHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GardenManager {
	private SharedPreferences preferences;
	private Context mContext;
	private boolean isLocalStore = false;
	private GardenProvider gardenProvider;

	public GardenManager(Context mContext) {
		this.mContext = mContext;
		preferences = mContext.getSharedPreferences("org.gots.preference", 0);
		// gardenProvider = new NuxeoGardenProvider(mContext);
		gardenProvider = new LocalGardenProvider(mContext);

	}

	public long addGarden(GardenInterface garden) {

		return addGarden(garden, false);
	}

	public long addGarden(GardenInterface garden, boolean localStore) {

		GardenInterface newGarden = gardenProvider.createGarden(garden);

		setCurrentGarden(newGarden);

		isLocalStore = localStore;

		GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
		tracker.trackEvent("Garden", "location", newGarden.getLocality(), 0);

		return newGarden.getId();
	}

	private void changeDatabase(int position) {
		DatabaseHelper helper = new DatabaseHelper(mContext);
		helper.setDatabase(position);

		// WeatherManager wm = new WeatherManager(mContext);
		// wm.getWeatherFromWebService(getcurrentGarden());

	}

	public GardenInterface getcurrentGarden() {
		GardenInterface garden = gardenProvider.getCurrentGarden();
		if (garden != null)
			changeDatabase((int) garden.getId());
		return garden;
	}

	public void setCurrentGarden(GardenInterface garden) {
		SharedPreferences.Editor prefedit = preferences.edit();
		prefedit.putInt("org.gots.preference.gardenid", (int) garden.getId());
		prefedit.commit();
		Log.d("setCurrentGarden", "[" + (int) garden.getId() + "] " + garden.getLocality()
				+ " has been set as current workspace");

		changeDatabase((int) garden.getId());
	}

	public void removeGarden(GardenInterface garden) {
		gardenProvider.removeGarden(garden);
	}

	public void updateCurrentGarden(GardenInterface garden) {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		helper.updateGarden(garden);
	}

	public void update() {
		// new RefreshTask().execute(new Object(), false);

	}

	// private class RefreshTask extends AsyncTask<Object, Boolean, Long> {
	// @Override
	// protected Long doInBackground(Object... params) {
	//
	// GotsConnector connector;
	// if (!isLocalStore)
	// // connector = new SimpleConnector();
	// connector = new NuxeoConnector(mContext);
	// else
	// connector = new LocalConnector(mContext);
	// List<BaseSeedInterface> seeds = connector.getAllSeeds();
	//
	// VendorSeedDBHelper theSeedBank = new VendorSeedDBHelper(mContext);
	// for (Iterator<BaseSeedInterface> iterator = seeds.iterator();
	// iterator.hasNext();) {
	// BaseSeedInterface baseSeedInterface = iterator.next();
	// if (theSeedBank.getSeedByReference(baseSeedInterface.getReference()) ==
	// null)
	// theSeedBank.insertSeed(baseSeedInterface);
	//
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Long result) {
	// // VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
	// // ArrayList<BaseSeedInterface> vendorSeeds;
	// // vendorSeeds = myBank.getVendorSeeds();
	//
	// // setListAdapter(new ListVendorSeedAdapter(mContext, vendorSeeds));
	// Toast.makeText(mContext, "Updated", 20).show();
	//
	// super.onPostExecute(result);
	// }
	// }

	public List<GardenInterface> getMyGardens() {
		return gardenProvider.getMyGardens();
	}
}
