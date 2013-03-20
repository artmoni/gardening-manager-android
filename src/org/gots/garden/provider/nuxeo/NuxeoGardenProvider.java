package org.gots.garden.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes.Name;

import org.gots.action.GardeningActionInterface;
import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.gots.garden.provider.GardenProvider;
import org.gots.preferences.GotsPreferences;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NuxeoGardenProvider implements GardenProvider {

	private Context mContext;

	public NuxeoGardenProvider(Context context) {
		mContext = context;
	}

	@Override
	public GardenInterface createGarden(GardenInterface garden) {

		try {
			new AsyncTask<GardenInterface, Integer, Document>() {

				private Document newGarden;

				@Override
				protected Document doInBackground(GardenInterface... params) {
					GardenInterface currentGarden = params[0];
					HttpAutomationClient client = new HttpAutomationClient(
							GotsPreferences.getGardeningManagerServerURI());
					Session session = client.getSession(GotsPreferences.getInstance(mContext).getNUXEO_LOGIN(),
							GotsPreferences.getInstance(mContext).getNUXEO_PASSWORD());

					try {
						DocRef wsRef = new DocRef("/default-domain/UserWorkspaces/"
								+ GotsPreferences.getInstance(mContext).getNUXEO_LOGIN());

						newGarden = (Document) session.newRequest("Document.Create").setInput(wsRef)
								.set("type", "Garden").set("name", currentGarden.getLocality())

								.set("properties", "dc:title=" + currentGarden.getLocality()).execute();

					} catch (Exception e) {
						e.printStackTrace();
					}
					return newGarden;
				}

			}.execute(garden).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return garden;
	}

	@Override
	public GardenInterface getCurrentGarden() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GardenInterface> getMyGardens() {

		List<GardenInterface> myGardens = new ArrayList<GardenInterface>();

		try {
			myGardens = new AsyncTask<Object, Integer, List<GardenInterface>>() {
				List<GardenInterface> gardens = new ArrayList<GardenInterface>();

				@Override
				protected List<GardenInterface> doInBackground(Object... params) {
					HttpAutomationClient client = new HttpAutomationClient(
							GotsPreferences.getGardeningManagerServerURI());
					Session session = client.getSession(GotsPreferences.getInstance(mContext).getNUXEO_LOGIN(),
							GotsPreferences.getInstance(mContext).getNUXEO_PASSWORD());

					// DocumentService rs = new DocumentService(session);
					try {
						// DocRef wsRef = new
						// DocRef("/default-domain/UserWorkspaces/" +
						// GotsPreferences.getUsername());

						// Documents gardensWorkspaces = (Documents)
						// rs.getChildren(wsRef);
						Documents gardensWorkspaces = (Documents) session.newRequest("Document.Query")
								.setHeader(Constants.HEADER_NX_SCHEMAS, "*")
								.set("query", "SELECT * FROM Garden ORDER BY dc:modified DESC").execute();
						for (Iterator<Document> iterator = gardensWorkspaces.iterator(); iterator.hasNext();) {
							Document gardenWorkspace = iterator.next();
							// Document gardenWorkspace = iterator.next();
							
							GardenInterface garden = NuxeoGardenConvertor.convert(gardenWorkspace);
							
							gardens.add(garden);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
					return gardens;
				}

			}.execute(new Object()).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myGardens;
	}
}
