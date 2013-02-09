package org.gots.garden;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.action.service.ActionNotificationService;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.ListVendorSeedAdapter;
import org.gots.seed.providers.GotsConnector;
import org.gots.seed.providers.local.LocalConnector;
import org.gots.seed.providers.simple.SimpleConnector;
import org.gots.seed.sql.VendorSeedDBHelper;
import org.gots.weather.WeatherManager;
import org.gots.weather.service.WeatherUpdateService;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GardenManager {
	private SharedPreferences preferences;
	private Context mContext;
	private boolean isLocalStore = false;

	public GardenManager(Context mContext) {
		this.mContext = mContext;
		preferences = mContext.getSharedPreferences("org.gots.preference", 0);

	}

	public long addGarden(GardenInterface garden) {

		GardenDBHelper helper = new GardenDBHelper(mContext);
		GardenInterface newGarden = helper.insertGarden(garden);

		setCurrentGarden((int) newGarden.getId());

		new RefreshTask().execute(new Object());

		return newGarden.getId();
	}

	public long addGarden(GardenInterface garden, boolean localStore) {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		GardenInterface newGarden = helper.insertGarden(garden);

		setCurrentGarden((int) newGarden.getId());

		isLocalStore = localStore;
		new RefreshTask().execute(new Object());

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
		GardenDBHelper helper = new GardenDBHelper(mContext);
		int gardenId = preferences.getInt("org.gots.preference.gardenid", 0);
		GardenInterface garden = helper.getGarden(gardenId);
		
		changeDatabase(gardenId);
		return garden;
	}

	public void setCurrentGarden(int position) {
		SharedPreferences.Editor prefedit = preferences.edit();
		prefedit.putInt("org.gots.preference.gardenid", position);
		prefedit.commit();

		changeDatabase(position);
	}

	public void removeCurrentGarden() {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		helper.deleteGarden(getcurrentGarden());
	}

	public void updateCurrentGarden(GardenInterface garden) {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		helper.updateGarden(garden);
	}

	public void update() {
		new RefreshTask().execute(new Object(), false);

	}

	public int getNbGarden() {
		GardenDBHelper helper = new GardenDBHelper(mContext);

		return helper.getCountGarden();
	}

	private class RefreshTask extends AsyncTask<Object, Boolean, Long> {
		@Override
		protected Long doInBackground(Object... params) {

			GardenManager garden = new GardenManager(mContext);
			// garden.update();
			GotsConnector connector;
			if (!isLocalStore)
				connector = new SimpleConnector();
			else
				connector = new LocalConnector(mContext);
			List<BaseSeedInterface> seeds = connector.getAllSeeds();

			VendorSeedDBHelper theSeedBank = new VendorSeedDBHelper(mContext);
			for (Iterator<BaseSeedInterface> iterator = seeds.iterator(); iterator.hasNext();) {
				BaseSeedInterface baseSeedInterface = iterator.next();
				if (theSeedBank.getSeedByReference(baseSeedInterface.getReference()) == null)
					theSeedBank.insertSeed(baseSeedInterface);

			}
			return null;
		}

		@Override
		protected void onPostExecute(Long result) {
			// VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
			// ArrayList<BaseSeedInterface> vendorSeeds;
			// vendorSeeds = myBank.getVendorSeeds();

			// setListAdapter(new ListVendorSeedAdapter(mContext, vendorSeeds));
			Toast.makeText(mContext, "Updated", 20).show();

			super.onPostExecute(result);
		}
	}
}
