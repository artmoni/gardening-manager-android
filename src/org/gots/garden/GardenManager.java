package org.gots.garden;

import java.util.Iterator;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.garden.provider.nuxeo.NuxeoGardenProvider;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.LocalSeedProvider;
import org.gots.seed.providers.nuxeo.NuxeoSeedProvider;
import org.gots.seed.providers.simple.SimpleSeedProvider;
import org.gots.seed.sql.VendorSeedDBHelper;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GardenManager {
	private SharedPreferences preferences;
	private Context mContext;
	private boolean isLocalStore = false;
	private GardenProvider gardenProvider;
	

	public GardenManager(Context mContext) {
		this.mContext = mContext;
		preferences = mContext.getSharedPreferences("org.gots.preference", 0);
//		gardenProvider = new NuxeoGardenProvider(mContext);
		gardenProvider = new LocalGardenProvider(mContext);
		
	}

	public long addGarden(GardenInterface garden) {

		return addGarden(garden, false);
	}

	public long addGarden(GardenInterface garden, boolean localStore) {
		
		GardenInterface newGarden = gardenProvider.createGarden(garden);

		setCurrentGarden((int) newGarden.getId());

		isLocalStore = localStore;
//		new RefreshTask().execute(new Object());

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
//		new RefreshTask().execute(new Object(), false);

	}

	

//	private class RefreshTask extends AsyncTask<Object, Boolean, Long> {
//		@Override
//		protected Long doInBackground(Object... params) {
//
//			GotsConnector connector;
//			if (!isLocalStore)
//				// connector = new SimpleConnector();
//				connector = new NuxeoConnector(mContext);
//			else
//				connector = new LocalConnector(mContext);
//			List<BaseSeedInterface> seeds = connector.getAllSeeds();
//
//			VendorSeedDBHelper theSeedBank = new VendorSeedDBHelper(mContext);
//			for (Iterator<BaseSeedInterface> iterator = seeds.iterator(); iterator.hasNext();) {
//				BaseSeedInterface baseSeedInterface = iterator.next();
//				if (theSeedBank.getSeedByReference(baseSeedInterface.getReference()) == null)
//					theSeedBank.insertSeed(baseSeedInterface);
//
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Long result) {
//			// VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);
//			// ArrayList<BaseSeedInterface> vendorSeeds;
//			// vendorSeeds = myBank.getVendorSeeds();
//
//			// setListAdapter(new ListVendorSeedAdapter(mContext, vendorSeeds));
//			Toast.makeText(mContext, "Updated", 20).show();
//
//			super.onPostExecute(result);
//		}
//	}

	public List<GardenInterface> getMyGardens() {
		return gardenProvider.getMyGardens();
	}
}
