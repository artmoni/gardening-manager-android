package org.gots.seed.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.TokenRequestInterceptor;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class NuxeoSeedProvider extends LocalSeedProvider {

    protected static final String TAG = "NuxeoSeedProvider";

    private static final long TIMEOUT = 10;

    String myToken;

    String myLogin;

    String myDeviceId;

    protected String myApp;

    public NuxeoSeedProvider(Context context) {
        super(context);
        myToken = gotsPrefs.getToken();
        myLogin = gotsPrefs.getNuxeoLogin();
        myDeviceId = gotsPrefs.getDeviceId();
        myApp = gotsPrefs.getGardeningManagerAppname();
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds() {

        List<BaseSeedInterface> localVendorSeeds = super.getVendorSeeds();
        List<BaseSeedInterface> remoteVendorSeeds = new ArrayList<BaseSeedInterface>();

        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);


        // client = new HttpAutomationClient(gotsPrefs.getNuxeoAutomationURI());
        // if (gotsPrefs.isConnectedToServer())
        // client.setRequestInterceptor(new TokenRequestInterceptor(myApp, myToken, myLogin, myDeviceId));

        try {

            // Session session = client.getSession();

//            Documents docs = (Documents) session.newRequest("Document.Query") //
//            .setHeader(Constants.HEADER_NX_SCHEMAS, "*") //
//            .set("query",
//                    "SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC") //
//            .execute();
            Documents docs = service.query("SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC");
            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
                Document document = iterator.next();
                BaseSeedInterface seed = NuxeoSeedConverter.convert(document);
                remoteVendorSeeds.add(seed);
                Log.i(TAG, "Nuxeo Seed Specie " + seed.getSpecie());
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllSeeds " + e.getMessage(), e);
        }



       

        // TODO send as intent
        List<BaseSeedInterface> myLocalSeeds = super.getVendorSeeds();
        for (BaseSeedInterface remoteSeed : remoteVendorSeeds) {
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
                localVendorSeeds.add(super.createSeed(remoteSeed));
            }
        }

        for (BaseSeedInterface localSeed : myLocalSeeds) {
            if (localSeed.getUUID() == null) {
                createNuxeoVendorSeed(localSeed);
            }
        }

        return localVendorSeeds;
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
        return createNuxeoVendorSeed(seed);
    }

    /*
     * Return new remote seed or null if error
     */
    protected BaseSeedInterface createNuxeoVendorSeed(BaseSeedInterface currentSeed) {

        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);

        Document catalog = null;
        try {
            DocRef wsRef = new PathRef(service.getUserHome().getPath() + "/Catalog");
            catalog = service.getDocument(wsRef);
        } catch (Exception e) {
            Log.e(TAG, "Fetching folder Catalog " + e.getMessage(), e);

            Document folder;
            try {
                Document root = service.getDocument(service.getUserHome());

                // folder = (Document) session.newRequest("Document.Create").setInput(root).setHeader(
                // Constants.HEADER_NX_SCHEMAS, "*").set("type", "Hut").set("name", "Catalog").set("properties",
                // "dc:title=" + "Catalog").execute();
                folder = service.createDocument(root, "Hut", "Catalog");
                catalog = folder;

                Log.d(TAG, "create folder Catalog UUID " + folder.getId());
            } catch (Exception e1) {
                Log.e(TAG, "Creating folder Catalog " + e.getMessage(), e);
            }

        }

        if (catalog == null)
            return null;

        try {
            Document documentVendorSeed = service.createDocument(catalog, "VendorSeed", currentSeed.getVariety(),
                    NuxeoSeedConverter.convert(catalog.getPath(), currentSeed).getProperties());
            currentSeed.setUUID(documentVendorSeed.getPath());
            Log.d(TAG, "RemoteSeed UUID " + documentVendorSeed.getId());
            super.updateSeed(currentSeed);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return currentSeed;
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);

        DocRef wsRef;
        try {
            wsRef = service.getUserHome();
            service.createRelation(wsRef, "predicate", new DocRef(vendorSeed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }
}
