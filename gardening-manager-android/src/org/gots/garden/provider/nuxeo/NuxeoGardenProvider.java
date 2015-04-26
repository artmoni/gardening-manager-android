package org.gots.garden.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.nuxeo.NuxeoManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

/**
 * See <a href="http://doc.nuxeo.com/x/mQAz">Nuxeo documentation on Content
 * Automation</a>
 */
public class NuxeoGardenProvider extends LocalGardenProvider {

    public static final String DOCTYPE_GARDEN = "Garden";

    private static final String TAG = "NuxeoGardenProvider";

    String myToken;

    String myLogin;

    String myDeviceId;

    String myApp;

    // private static final long TIMEOUT = 10;

    protected NuxeoServerConfig nxConfig;

    protected NuxeoContext nuxeoContext;

    protected AndroidAutomationClient nuxeoClient;

    protected LazyUpdatableDocumentsList documentsList;

    private GardenInterface currentGarden;

    public NuxeoGardenProvider(Context context) {
        super(context);
        NuxeoManager.getInstance().initIfNew(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    @Override
    public List<GardenInterface> getMyGardens(boolean force) {
        List<GardenInterface> myGardens = new ArrayList<GardenInterface>();

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            // gardensWorkspaces = service.getChildren(wsRef);
            // Documents gardensWorkspaces =
            // service.query("SELECT * FROM Garden WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC");
            byte cacheParam = CacheBehavior.STORE;
            if (force) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
            }
            Documents gardensWorkspaces = service.query("SELECT * FROM " + DOCTYPE_GARDEN
                    + " WHERE ecm:currentLifeCycleState != \"deleted\"", null, new String[] { "dc:modified desc" },
                    "*", 0, 50, cacheParam);

            // TODO JC Documents gardensWorkspaces = service.query(nxql, queryParams, sortInfo, schemaList, page,
            // pageSize, cacheFlags);
            // documentsList = gardensWorkspaces.asUpdatableDocumentsList();
            for (Iterator<Document> iterator = gardensWorkspaces.iterator(); iterator.hasNext();) {
                Document gardenWorkspace = iterator.next();

                GardenInterface garden = NuxeoGardenConvertor.convert(gardenWorkspace);
                myGardens.add(super.updateGarden(garden));
                Log.d(TAG, "Document=" + gardenWorkspace.getId() + " / " + garden);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            myGardens = super.getMyGardens(force);
        }
        return myGardens;

    }

    @Override
    public GardenInterface createGarden(GardenInterface garden) {
        Log.i(TAG, "createGarden " + garden);

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
            Document root = documentMgr.getUserHome();

            PropertyMap properties = NuxeoGardenConvertor.convert(root.getPath(), garden).getProperties();

            Document newGarden = documentMgr.createDocument(root, DOCTYPE_GARDEN, garden.getName());
            documentMgr.update(newGarden, properties);
            garden.setUUID(newGarden.getId());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        if (garden.getId() == 0)
            garden = super.createGarden(garden);

        currentGarden = garden;

        return garden;
    }

    @Override
    public GardenInterface updateGarden(GardenInterface garden) {
        return updateNuxeoGarden(super.updateGarden(garden));
    }

    protected GardenInterface updateNuxeoGarden(final GardenInterface garden) {

        Log.i(TAG, "updateRemoteGarden " + garden);

        // TODO get document by id
        // DeferredUpdateManager deferredUpdateMgr = getNuxeoClient().getDeferredUpdatetManager();
        currentGarden = garden;
        // OperationRequest updateOperation;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            // PropertyMap props = new PropertyMap();
            // props.set("dc:title", garden.getLocality());

            Document doc = service.getDocument(garden.getUUID());
            PropertyMap props = NuxeoGardenConvertor.convert(doc.getParentPath(), garden).getProperties();

            Document updatedDoc = service.update(doc, props);
            if (updatedDoc != null)
                super.updateGarden(garden);
            // updateOperation = NuxeoManager.getInstance().getSession().newRequest("Document.Update").setHeader(
            // Constants.HEADER_NX_SCHEMAS, "*").setInput(doc).set("properties", props);
            //
            // AsyncCallback<Object> callback = new AsyncCallback<Object>() {
            // @Override
            // public void onSuccess(String executionId, Object data) {
            // Document doc = (Document) data;
            // currentGarden.setUUID(doc.getId());
            // NuxeoGardenProvider.super.updateGarden(currentGarden);
            // Log.d(TAG, "onSuccess " + currentGarden);
            // // getNuxeoClient().getTransientStateManager().flushTransientState(
            // // doc.getId());
            // }
            //
            // @Override
            // public void onError(String executionId, Throwable e) {
            // Log.d(TAG, "onError " + e.getMessage());
            //
            // }
            // };
            // deferredUpdateMgr.execDeferredUpdate(updateOperation, callback, OperationType.UPDATE, true);
            // try {
            // Document updatedDocument = NuxeoGardenConvertor.convert(service.getUserHome().getPath(), garden);
            // // TODO JC: documentsList.updateDocument(updatedDocument, updateOperation);
            // documentsList.updateDocument(updatedDocument);
            // // service.update(idRef, props);
            // } catch (Exception e) {
            // Log.e(TAG, e.getMessage(), e);
            // // cancel(false);
            // return garden;
            // }
        } catch (NotAvailableOffline e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return garden;
    }

    @Override
    public void removeGarden(GardenInterface garden) {
        super.removeGarden(garden);
        removeNuxeoGarden(garden);
    }

    @Override
    public GardenInterface getCurrentGarden() {
        GardenInterface garden = super.getCurrentGarden();
        // if (garden != null && garden.getUUID() == null) {
        // garden = createGarden(garden);
        // garden = super.updateGarden(garden);
        // }

        return garden;
    }

    protected void removeNuxeoGarden(final GardenInterface garden) {
        Log.i(TAG, "removeNuxeoGarden " + garden);

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.remove(new IdRef(garden.getUUID()));
            // documentsList.remove(updatedDocument);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // cancel(false);
        }

    }

    @Override
    public int share(GardenInterface garden, String user, String permission) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Document doc = service.getDocument(new IdRef(garden.getUUID()));
            Document docACL = service.setPermission(doc, user, permission);

            if (docACL == null)
                return -1;
            // documentsList.remove(updatedDocument);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // cancel(false);
        }
        return 0;
    }

    @Override
    public void getUsersAndGroups(GardenInterface garden) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Document doc = service.getDocument(new IdRef(garden.getUUID()));
            String var = "";
            Document docUsers = service.getUsersAndGroups(doc, "Write", var);

            Log.i(TAG, "Var=" + var);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // cancel(false);
        }
    }

}
