package org.gots.seed.providers;

import java.util.Iterator;

import org.gots.garden.GardenInterface;
import org.gots.preferences.GotsPreferences;
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

public class RetrieveNuxeoDocs extends AsyncTask<GardenInterface, Integer, Documents> {
	private Context mContext;

	public RetrieveNuxeoDocs(Context context) {
		mContext = context;
	}

	@Override
	protected Documents doInBackground(GardenInterface... params) {
		Documents docs = new Documents();
		GardenInterface garden = params[0];

		try {

			HttpAutomationClient client = new HttpAutomationClient(GotsPreferences.getGardeningManagerServerURI());
			Session session = client.getSession("bob", "password");

			DocumentService rs = new DocumentService(session);
			DocRef wsRef = new DocRef("/default-domain/workspaces/Public hut");

			docs = (Documents) session.newRequest("Document.Query").set("query", "SELECT * FROM VendorSeed").execute();

			for (Iterator iterator = docs.iterator(); iterator.hasNext();) {
				Document docref = (Document) iterator.next();
				Document document = rs.getDocument(docref.getParentPath() + "/" + docref.getName());
				PropertyMap map = document.getProperties();

				Log.i("Public Hut", document.getTitle() + "--" + document.getString("vendorseed:datesowingmin"));
			}

			// Document seed =
			// (Document)session.newRequest("Document.Fetch").set("value",
			// "/default-domain/workspaces/Public hut").execute();
			//
			// Documents seeds =
			// (Documents)session.newRequest("Document.Query").set("query",
			// "SELECT * FROM Document WHERE ecm:parentId="+seed.getId()).execute();

			Log.i("Nuxeo seeds", "" + docs.getIds());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return docs;
	}
}
