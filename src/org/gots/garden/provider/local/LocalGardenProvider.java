package org.gots.garden.provider.local;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.jar.Attributes.Name;

import org.gots.action.GardeningActionInterface;
import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.sql.GardenDBHelper;
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

public class LocalGardenProvider implements GardenProvider {

	private Context mContext;

	public LocalGardenProvider(Context context) {
		mContext = context;
	}

	@Override
	public GardenInterface createGarden(GardenInterface garden) {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		GardenInterface newGarden = helper.insertGarden(garden);
		return newGarden;
	}

	@Override
	public GardenInterface getCurrentGarden() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<GardenInterface> getMyGardens() {
		GardenDBHelper helper = new GardenDBHelper(mContext);
		return helper.getGardens();
	}
}
