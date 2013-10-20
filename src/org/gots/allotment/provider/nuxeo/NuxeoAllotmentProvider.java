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
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoAllotmentProvider extends LocalAllotmentProvider {
    protected static final String TAG = "NuxeoSeedProvider";

    String myToken;

    String myLogin;

    String myDeviceId;

    protected String myApp;

    protected LazyUpdatableDocumentsList documentsList = null;

    protected BaseAllotmentInterface currentAllotment;

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

            List<BaseAllotmentInterface> localAllotments = super.getMyAllotments();
            boolean found;
            for (BaseAllotmentInterface remoteAllotment : remoteAllotments) {
                found = false;
                for (BaseAllotmentInterface localAllotment : localAllotments) {

                    if (remoteAllotment.getUUID().equals(localAllotment.getUUID())) {
                        found = true;
                    }
                    if (found)
                        remoteAllotment = super.updateAllotment(remoteAllotment);
                    else
                        remoteAllotment = super.createAllotment(remoteAllotment);
                }

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
        DeferredUpdateManager deferredUpdateMgr = getNuxeoClient().getDeferredUpdatetManager();

        currentAllotment = allotment;
        DocRef wsRef;
        try {
            wsRef = service.getUserHome();
            // TODO Change this when garden UUID manage uuid and not path
            Document gardenFolder = service.getDocument(new IdRef(
                    GardenManager.getInstance().getCurrentGarden().getUUID()));
            Document allotmentsFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Allotment"));
            // Document allotmentDocument = service.createDocument(allotmentsFolder, "Allotment", allotment.getName());

            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", allotment.getName());

            OperationRequest createOperation = NuxeoManager.getInstance().getSession().newRequest("Document.Create").setHeader(
                    Constants.HEADER_NX_SCHEMAS, "*").setInput(allotmentsFolder).set("type", "Allotment").set(
                    "properties", properties);
            AsyncCallback<Object> callback = new AsyncCallback<Object>() {
                @Override
                public void onSuccess(String executionId, Object data) {
                    Document doc = (Document) data;
                    currentAllotment.setUUID(doc.getId());
                    currentAllotment = NuxeoAllotmentProvider.super.updateAllotment(currentAllotment);
                    Log.d(TAG, "onSuccess " + data);
                }

                @Override
                public void onError(String executionId, Throwable e) {
                    Log.d(TAG, "onError " + e.getMessage());

                }
            };
            deferredUpdateMgr.execDeferredUpdate(createOperation, callback, OperationType.CREATE, true);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return currentAllotment;
    }

    @Override
    public int removeAllotment(BaseAllotmentInterface allotment) {
        super.removeAllotment(allotment);
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
