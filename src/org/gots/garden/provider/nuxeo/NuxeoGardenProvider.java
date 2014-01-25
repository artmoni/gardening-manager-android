package org.gots.garden.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.action.GardeningActionInterface;
import org.gots.garden.GardenInterface;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.nuxeo.NuxeoManager;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;
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

    private boolean force_force = false;

    public NuxeoGardenProvider(Context context) {
        super(context);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    @Override
    public List<GardenInterface> getMyGardens(boolean force) {
        // List<GardenInterface> myCachedGardens = super.getMyGardens();
        List<GardenInterface> myCachedGardens;
        // if (documentsList != null) {
        // myCachedGardens = new ArrayList<GardenInterface>();
        // documentsList.refreshAll();
        // for (Iterator<Document> iterator = documentsList.getIterator(); iterator.hasNext();) {
        // // GardenInterface gardenInterface = (GardenInterface) iterator.next();
        // // for (Iterator<Document> iterator = documentsList.getIterator(); iterator.hasNext();) {
        // Document documentGarden = iterator.next();
        // if (documentGarden == null) {
        // break;
        // }
        // GardenInterface garden = NuxeoGardenConvertor.convert(documentGarden);
        // myCachedGardens.add(super.updateGarden(garden));
        // Log.d(TAG, "documentsList=" + documentGarden.getId() + " / " + garden);
        // }
        //
        // // return myCachedGardens;
        // } else
        // if(force)

        myCachedGardens = getMyNuxeoGardens(super.getMyGardens(force), force);

        return myCachedGardens;

    }

    /**
     * Returns either the list of remote gardens or the full list of gardens
     * with synchronization between local and
     * remote
     * 
     * @param myLocalGardens can be null if not syncWithLocalGardens
     * @param syncWithLocalGardens whether to sync or not local and remote
     *            gardens
     * @return
     */
    protected List<GardenInterface> getMyNuxeoGardens(List<GardenInterface> myLocalGardens, final boolean force) {
        List<GardenInterface> remoteGardens = new ArrayList<GardenInterface>();

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            // gardensWorkspaces = service.getChildren(wsRef);
            // Documents gardensWorkspaces =
            // service.query("SELECT * FROM Garden WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC");
            byte cacheParam = CacheBehavior.STORE;
            boolean refresh = force || force_force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Documents gardensWorkspaces = service.query(
                    "SELECT * FROM Garden WHERE ecm:currentLifeCycleState != \"deleted\"", null,
                    new String[] { "dc:modified desc" }, "*", 0, 50, cacheParam);

            // TODO JC Documents gardensWorkspaces = service.query(nxql, queryParams, sortInfo, schemaList, page,
            // pageSize, cacheFlags);
            // documentsList = gardensWorkspaces.asUpdatableDocumentsList();
            for (Iterator<Document> iterator = gardensWorkspaces.iterator(); iterator.hasNext();) {
                Document gardenWorkspace = iterator.next();
                GardenInterface garden = NuxeoGardenConvertor.convert(gardenWorkspace);
                remoteGardens.add(garden);
                Log.d(TAG, "Document=" + gardenWorkspace.getId() + " / " + garden);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // TODO check workaround need and consequences
            // remoteGardens = getMyLocalGardens();
            // cancel(false);
            // return myLocalGardens;
        }

        return synchronize(myLocalGardens, remoteGardens);

    }

    protected List<GardenInterface> synchronize(List<GardenInterface> myLocalGardens,
            List<GardenInterface> remoteGardens) {
        List<GardenInterface> myGardens = new ArrayList<GardenInterface>();
        // Synchronize remote garden with local gardens
        for (GardenInterface remoteGarden : remoteGardens) {
            boolean found = false;
            for (GardenInterface localGarden : myLocalGardens) {
                if (remoteGarden.getUUID() != null && remoteGarden.getUUID().equals(localGarden.getUUID())) {
                    found = true;
                    break;
                }
            }
            if (found) { // local and remote => update local
                // TODO check if remote can be out of date
                // syncGardens(localGarden,remoteGarden);
                myGardens.add(super.updateGarden(remoteGarden));
            } else { // remote only => create local
                myGardens.add(super.createGarden(remoteGarden));
            }
        }

        // Create remote garden when not exist remotely and remove local
        // garden if no more referenced online
        for (GardenInterface localGarden : myLocalGardens) {
            if (localGarden.getUUID() == null) { // local only without
                                                 // UUID => create
                                                 // remote
                myGardens.add(createNuxeoGarden(localGarden));
            } else {
                boolean found = false;
                for (GardenInterface remoteGarden : remoteGardens) {
                    if (remoteGarden.getUUID() != null && remoteGarden.getUUID().equals(localGarden.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) { // local only with UUID -> delete local
                    super.removeGarden(localGarden);
                }
            }
        }
        return myGardens;
    }

    @Override
    public GardenInterface createGarden(GardenInterface garden) {
        Log.i(TAG, "createGarden " + garden);
        currentGarden = garden;

        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        try {
            Document root = documentMgr.getUserHome();

            PropertyMap properties = NuxeoGardenConvertor.convert(root.getPath(), garden).getProperties();

            Document newGarden = documentMgr.createDocument(root, "Garden", garden.getLocality());
            documentMgr.update(newGarden, properties);
            garden.setUUID(newGarden.getId());
            //
            //
            //
            // OperationRequest createOperation =
            // NuxeoManager.getInstance().getSession().newRequest("Document.Create").setHeader(
            // Constants.HEADER_NX_SCHEMAS, "*").setInput(root).set("type", "Garden").set("properties", properties);
            //
            // AsyncCallback<Object> callback = new AsyncCallback<Object>() {
            // @Override
            // public void onSuccess(String executionId, Object data) {
            // Document doc = (Document) data;
            // currentGarden.setUUID(doc.getId());
            // currentGarden = NuxeoGardenProvider.super.updateGarden(currentGarden);
            // Log.d(TAG, "onSuccess " + data);
            // force_force = true;
            // }
            //
            // @Override
            // public void onError(String executionId, Throwable e) {
            // Log.d(TAG, "onError " + e.getMessage());
            //
            // }
            // };
            // deferredUpdateMgr.execDeferredUpdate(createOperation, callback, OperationType.CREATE, true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return super.createGarden(garden);
    }

    protected GardenInterface createNuxeoGarden(GardenInterface localGarden) {

        return localGarden;
    }

    @Override
    public GardenInterface updateGarden(GardenInterface garden) {
        return updateNuxeoGarden(super.updateGarden(garden));
    }

    protected GardenInterface updateNuxeoGarden(final GardenInterface garden) {

        Log.i(TAG, "updateRemoteGarden " + garden);

        // TODO get document by id
        Session session = getNuxeoClient().getSession();

        DocumentManager service = session.getAdapter(DocumentManager.class);
        DeferredUpdateManager deferredUpdateMgr = getNuxeoClient().getDeferredUpdatetManager();
        currentGarden = garden;
        OperationRequest updateOperation;
        try {
            // PropertyMap props = new PropertyMap();
            // props.set("dc:title", garden.getLocality());

            Document doc = service.getDocument(garden.getUUID());
            PropertyMap props = NuxeoGardenConvertor.convert(doc.getParentPath(), garden).getProperties();

            updateOperation = NuxeoManager.getInstance().getSession().newRequest("Document.Update").setHeader(
                    Constants.HEADER_NX_SCHEMAS, "*").setInput(doc).set("properties", props);

            AsyncCallback<Object> callback = new AsyncCallback<Object>() {
                @Override
                public void onSuccess(String executionId, Object data) {
                    Document doc = (Document) data;
                    currentGarden.setUUID(doc.getId());
                    NuxeoGardenProvider.super.updateGarden(currentGarden);
                    Log.d(TAG, "onSuccess " + currentGarden);
                    // getNuxeoClient().getTransientStateManager().flushTransientState(
                    // doc.getId());
                    force_force = true;
                }

                @Override
                public void onError(String executionId, Throwable e) {
                    Log.d(TAG, "onError " + e.getMessage());

                }
            };
            deferredUpdateMgr.execDeferredUpdate(updateOperation, callback, OperationType.UPDATE, true);
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
        if (garden != null && garden.getUUID() == null) {
            garden = createGarden(garden);
            garden = super.updateGarden(garden);
        }

        return garden;
    }

    protected void removeNuxeoGarden(final GardenInterface garden) {
        Log.i(TAG, "removeNuxeoGarden " + garden);

        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            service.remove(new IdRef(garden.getUUID()));
            // documentsList.remove(updatedDocument);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // cancel(false);
        }

    }
}
