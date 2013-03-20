package org.gots.seed.providers.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NuxeoSeedProvider implements GotsSeedProvider {
	private Context mContext;

	public NuxeoSeedProvider(Context context) {
		mContext = context;
	}

	@Override
	public List<BaseSeedInterface> getAllSeeds() {
		List<BaseSeedInterface> nuxeoSeeds = new ArrayList<BaseSeedInterface>(); ;
		try {
			nuxeoSeeds = new AsyncTask<Object, Integer, List<BaseSeedInterface>>() {

				@Override
				protected List<BaseSeedInterface> doInBackground(Object... params) {
					List<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();

					try {
						HttpAutomationClient client = new HttpAutomationClient(
								GotsPreferences.getGardeningManagerServerURI());

						Session session = client.getSession();

						Documents docs = (Documents) session.newRequest("Document.Query")
								.setHeader(Constants.HEADER_NX_SCHEMAS, "*")
								.set("query", "SELECT * FROM VendorSeed ORDER BY dc:modified DESC").execute();
						for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
							Document document = (Document) iterator.next();
							BaseSeedInterface seed = NuxeoSeedConverter.convert(document);
							vendorSeeds.add(seed);
							Log.i("Seed Specie", " " + seed.getSpecie());
							Log.i("Nuxeo Seed", "" + seed.toString());

						}
					} catch (Exception e) {
						Log.e("getAllSeeds", e.getMessage());
					}
					return vendorSeeds;
				}
			}.execute(new Object()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return nuxeoSeeds;
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

}
