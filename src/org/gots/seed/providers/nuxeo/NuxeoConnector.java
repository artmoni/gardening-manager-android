package org.gots.seed.providers.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsConnector;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.adapters.DocumentService;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.util.Log;

public class NuxeoConnector implements GotsConnector {

	@Override
	public List<BaseSeedInterface> getAllSeeds() {
		List<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();
		try {

			HttpAutomationClient client = new HttpAutomationClient(GotsPreferences.getGardeningManagerServerURI());
			Session session = client.getSession("bob", "password");

			DocumentService rs = new DocumentService(session);
			DocRef wsRef = new DocRef("/default-domain/workspaces/Public hut");

			Documents docs = rs.getChildren(wsRef);
			for (Iterator iterator = docs.iterator(); iterator.hasNext();) {
				Document document = (Document) iterator.next();

				Log.i("Public Hut", document.getTitle());
			}
		} catch (Exception e) {
			Log.e("getAllSeeds", e.getMessage());
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

}
