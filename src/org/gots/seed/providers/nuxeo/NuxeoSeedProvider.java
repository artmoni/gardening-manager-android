package org.gots.seed.providers.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.gots.garden.GardenInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.LocalSeedProvider;
import org.gots.utils.TokenRequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NuxeoSeedProvider extends LocalSeedProvider {
	protected static final String TAG = "NuxeoSeedProvider";
	private static final long TIMEOUT = 10;
	String myToken = GotsPreferences.getInstance(mContext).getToken();
	String myLogin = GotsPreferences.getInstance(mContext).getNUXEO_LOGIN();
	String myDeviceId = GotsPreferences.getInstance(mContext).getDeviceId();

	public NuxeoSeedProvider(Context context) {
		super(context);
	}

	@Override
	public List<BaseSeedInterface> getVendorSeeds() {

		List<BaseSeedInterface> vendorSeeds = super.getVendorSeeds();

		AsyncTask<Object, Integer, List<BaseSeedInterface>> task = new AsyncTask<Object, Integer, List<BaseSeedInterface>>() {

			private HttpAutomationClient client;

			@Override
			protected List<BaseSeedInterface> doInBackground(Object... params) {
				List<BaseSeedInterface> nuxeoSeeds = new ArrayList<BaseSeedInterface>();

				client = new HttpAutomationClient(GotsPreferences.getGardeningManagerServerURI());
				if (GotsPreferences.getInstance(mContext).isConnectedToServer())
					client.setRequestInterceptor(new TokenRequestInterceptor(myToken, myLogin, myDeviceId));

				try {

					Session session = client.getSession();

					Documents docs = (Documents) session
							.newRequest("Document.Query")
							.setHeader(Constants.HEADER_NX_SCHEMAS, "*")
							.set("query",
									"SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC")
							.execute();
					for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
						Document document = (Document) iterator.next();
						BaseSeedInterface seed = NuxeoSeedConverter.convert(document);
						nuxeoSeeds.add(seed);
						Log.i(TAG, "Nuxeo Seed Specie " + seed.getSpecie());
					}
				} catch (Exception e) {
					Log.e(TAG, "getAllSeeds " + e.getMessage());
				}

				return nuxeoSeeds;
			}
		}.execute(new Object());

		List<BaseSeedInterface> remoteSeeds = new ArrayList<BaseSeedInterface>();

		try {
			remoteSeeds = task.get(TIMEOUT, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			Log.e(TAG, GotsPreferences.getGardeningManagerServerURI() + "\n" + e.getMessage(), e);
		}

		// TODO send as intent
		List<BaseSeedInterface> myLocalSeeds = super.getVendorSeeds();
		for (BaseSeedInterface remoteSeed : remoteSeeds) {
			boolean found = false;
			for (BaseSeedInterface localSeed : myLocalSeeds) {
				if (remoteSeed.getUUID() != null && remoteSeed.getUUID().equals(localSeed.getUUID())) {
					// local and remote
					// 1: overwrite remote
					// updateRemoteGarden(localSeed);
					// 2: TODO sync with remote instead
					// syncGardens(localGarden,remoteGarden);
					found = true;
					break;
				}
			}
			if (!found) {
				// remote only
				vendorSeeds.add(super.createSeed(remoteSeed));
			}
		}
		
		for (BaseSeedInterface localSeed : myLocalSeeds) {
			if (localSeed.getUUID() == null) {
				createRemoteSeed(localSeed);
			}
		}

		return vendorSeeds;
	}

	@Override
	public void getAllFamilies() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getFamilyById(int id) {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseSeedInterface getSeedById() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BaseSeedInterface createSeed(BaseSeedInterface seed) {
		super.createSeed(seed);
		return createRemoteSeed(seed);
	}

	protected BaseSeedInterface createRemoteSeed(BaseSeedInterface seed) {
		try {
			AsyncTask<BaseSeedInterface, Integer, Document> task = new AsyncTask<BaseSeedInterface, Integer, Document>() {

				private Document documentVendorSeed;

				@Override
				protected Document doInBackground(BaseSeedInterface... params) {
					BaseSeedInterface currentSeed = params[0];
					Log.d(TAG, "doInBackground createSeed " + currentSeed);

					HttpAutomationClient client = new HttpAutomationClient(
							GotsPreferences.getGardeningManagerServerURI());

					client.setRequestInterceptor(new TokenRequestInterceptor(myToken, myLogin, myDeviceId));

					Session session = client.getSession();

					try {
						DocRef wsRef = new DocRef("/default-domain/UserWorkspaces/"
								+ GotsPreferences.getInstance(mContext).getNUXEO_LOGIN());

						PropertyMap props = new PropertyMap();
						props.set("dc:title", currentSeed.getVariety());
						props.set("dc:description", "test");
						props.set("vendorseed:datesowingmin", String.valueOf(currentSeed.getDateSowingMin()));
						props.set("vendorseed:datesowingmax", String.valueOf(currentSeed.getDateSowingMax()));
						props.set("vendorseed:durationmin", String.valueOf(currentSeed.getDurationMin()));
						props.set("vendorseed:durationmax", String.valueOf(currentSeed.getDurationMax()));
						props.set("vendorseed:family", currentSeed.getFamily());
						props.set("vendorseed:specie", currentSeed.getSpecie());
						props.set("vendorseed:variety", currentSeed.getVariety());
						props.set("vendorseed:barcode", currentSeed.getBareCode());

						documentVendorSeed = (Document) session.newRequest("Document.Create").setInput(wsRef)
								.setHeader(Constants.HEADER_NX_SCHEMAS, "*").set("type", "VendorSeed")
								.set("name", currentSeed.getVariety()).set("properties", props).execute();
						Log.d(TAG, "doInBackground remoteSeed UUID " + documentVendorSeed.getId());

					} catch (Exception e) {
						Log.e(TAG, e.getMessage(), e);
					}
					return documentVendorSeed;

				}

			}.execute(seed);
			// TODO wait for task.getStatus() == Status.FINISHED; in a thread
			//
			// TODO send as intent
			// TODO get(timeout)
			Document remoteVendorSeed = task.get();
			seed.setUUID(remoteVendorSeed.getId());

			super.updateSeed(seed);

		} catch (InterruptedException e) {
			Log.e(TAG, e.getMessage());
		} catch (ExecutionException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return seed;
	}
}
