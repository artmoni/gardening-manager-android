package org.gots.allotment.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.allotment.provider.local.LocalAllotmentProvider;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.garden.GardenManager;
import org.gots.nuxeo.NuxeoManager;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;

import android.content.Context;
import android.util.Log;

public class NuxeoAllotmentProvider extends LocalAllotmentProvider {
    protected static final String TAG = "NuxeoSeedProvider";

    String myToken;

    String myLogin;

    String myDeviceId;

    protected String myApp;

    protected LazyUpdatableDocumentsList documentsList = null;

    public NuxeoAllotmentProvider(Context context) {
        super(context);
        myToken = gotsPrefs.getToken();
        myLogin = gotsPrefs.getNuxeoLogin();
        myDeviceId = gotsPrefs.getDeviceId();
        myApp = gotsPrefs.getGardeningManagerAppname();
    }

    @Override
    public BaseAllotmentInterface getCurrentAllotment() {
        return super.getCurrentAllotment();
    }

    @Override
    public List<BaseAllotmentInterface> getMyAllotments() {
        List<BaseAllotmentInterface> remoteAllotments = super.getMyAllotments();
        // if (documentsList != null && documentsList.getLoadedPageCount() > 0) {
        // remoteAllotments = new ArrayList<BaseAllotmentInterface>();
        // documentsList.refreshAll();
        // for (int i = 0; i < documentsList.getLoadedPageCount(); i++) {
        // Document documentAllotment = documentsList.getDocument(i);
        // if (documentAllotment == null) {
        // break;
        // }
        // BaseAllotmentInterface allotment = NuxeoAllotmentConverter.convert(documentAllotment);
        // remoteAllotments.add(super.updateAllotment(allotment));
        // Log.d(TAG, "documentsList=" + documentAllotment.getId() + " / " + allotment);
        // }
        // } else
        // if (remoteAllotments.size() == 0)
        remoteAllotments = getNuxeoAllotments();
        // myVendorSeeds = synchronize(localVendorSeeds, remoteVendorSeeds);
        return remoteAllotments;
    }

    protected List<BaseAllotmentInterface> getNuxeoAllotments() {
        List<BaseAllotmentInterface> remoteAllotments = new ArrayList<BaseAllotmentInterface>();
        // List<BaseAllotmentInterface> myAllotments = new ArrayList<BaseAllotmentInterface>();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean refresh = true;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            DocRef wsRef = service.getUserHome();
            // TODO Change this when garden UUID manage uuid and not path
            Document gardenFolder = service.getDocument(new IdRef(
                    GardenManager.getInstance().getCurrentGarden().getUUID()));
            Document allotmentsFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Allotment"));

            Documents docs = service.query(
                    "SELECT * FROM Allotment WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + allotmentsFolder.getId() + "\"", null, new String[] { "dc:modified true" }, "*", 0, 50,
                    cacheParam);
            documentsList = docs.asUpdatableDocumentsList();

            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
                Document document = iterator.next();
                BaseAllotmentInterface allotment = NuxeoAllotmentConverter.convert(document);
                remoteAllotments.add(allotment);
                Log.i(TAG, "Nuxeo Allotment " + allotment);
            }
        } catch (Exception e) {
            Log.e(TAG, "getMyAllotments " + e.getMessage(), e);
        }
        return remoteAllotments;
    }

    @Override
    public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment) {
        super.createAllotment(allotment);
        return createNuxeoAllotment(allotment);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    private BaseAllotmentInterface createNuxeoAllotment(BaseAllotmentInterface allotment) {

        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        BaseAllotmentInterface allotmentInterface = null;
        DocRef wsRef;
        try {
            wsRef = service.getUserHome();
            // TODO Change this when garden UUID manage uuid and not path
            Document gardenFolder = service.getDocument(new IdRef(
                    GardenManager.getInstance().getCurrentGarden().getUUID()));
            Document allotmentsFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Allotment"));
            Document allotmentDocument = service.createDocument(allotmentsFolder, "Allotment", allotment.getName());

            Log.d(TAG, NuxeoAllotmentConverter.convert(allotmentDocument).toString());

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return allotmentInterface;
    }

    @Override
    public int removeAllotment(BaseAllotmentInterface allotment) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            service.remove(allotment.getUUID());

            Log.d(TAG, "removing document " + allotment.getUUID());

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment) {
        // TODO Auto-generated method stub
        return super.updateAllotment(allotment);
    }

    @Override
    public void setCurrentAllotment(BaseAllotmentInterface allotmentInterface) {
        super.setCurrentAllotment(allotmentInterface);
    }
}
