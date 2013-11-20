package org.gots.seed.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoGrowingSeedProvider extends LocalGrowingSeedProvider {

    private static final String TAG = "NuxeoGrowingSeedProvider";

    private GrowingSeedInterface currentGrowingSeed = null;

    public NuxeoGrowingSeedProvider(Context mContext) {
        super(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        NuxeoManager nuxeoManager = NuxeoManager.getInstance();
        nuxeoManager.initIfNew(mContext);
        return nuxeoManager.getNuxeoClient();
    }

    @Override
    public List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment) {
        List<GrowingSeedInterface> remoteGrowingSeeds = new ArrayList<GrowingSeedInterface>();
        List<GrowingSeedInterface> myGrowingSeeds = new ArrayList<GrowingSeedInterface>();
        List<GrowingSeedInterface> localGrowingSeeds = super.getGrowingSeedsByAllotment(allotment);

        boolean refresh = true;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }

            Documents docs = service.query(
                    "SELECT * FROM GrowingSeed WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\'"
                            + allotment.getUUID() + "\'", null, new String[] { "dc:modified DESC" }, "*", 0, 50,
                    cacheParam);
            // Documents docs = service.getChildren(new IdRef(allotment.getUUID()));
            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
                Document document = iterator.next();
                GrowingSeedInterface growingSeed = NuxeoGrowingSeedConverter.convert(document);
                if (growingSeed != null) {

                    growingSeed = super.getGrowingSeedsByUUID(growingSeed.getUUID());
                    remoteGrowingSeeds.add(growingSeed);
                    Log.i(TAG, "Nuxeo GrowingSeed: " + growingSeed);
                } else {
                    Log.w(TAG, "Nuxeo GrowingSeed conversion problem " + document.getTitle() + "- " + document.getId());
                }
            }
            myGrowingSeeds = synchronize(localGrowingSeeds, remoteGrowingSeeds, allotment);
            // myGrowingSeeds = remoteGrowingSeeds;
        } catch (Exception e) {
            Log.e(TAG, "getGrowingSeedsByAllotment " + e.getMessage(), e);
            myGrowingSeeds = localGrowingSeeds;
        }
        return myGrowingSeeds;
    }

    private List<GrowingSeedInterface> synchronize(List<GrowingSeedInterface> localGrowingSeeds,
            List<GrowingSeedInterface> remoteGrowingSeeds, BaseAllotmentInterface allotment) {
        List<GrowingSeedInterface> myGrowingSeeds = remoteGrowingSeeds;

        for (GrowingSeedInterface remoteGrowingSeed : remoteGrowingSeeds) {
            boolean found = false;
            for (GrowingSeedInterface localGrowingSeed : localGrowingSeeds) {
                if (remoteGrowingSeed.getUUID().equals(localGrowingSeed.getUUID())) {
                    found = true;
                    break;
                }
            }
            
            if (!found)
                super.insertSeed(remoteGrowingSeed, allotment);
        }
        
        for (GrowingSeedInterface localGrowingSeed : localGrowingSeeds) {
            boolean found=false;
            for (GrowingSeedInterface remoteGrowingSeed : remoteGrowingSeeds) {
                if (localGrowingSeed.getUUID()!=null&&localGrowingSeed.getUUID().equals(remoteGrowingSeed.getUUID()))
                    found = true;
            }
            if (!found)
                insertNuxeoSeed(localGrowingSeed, allotment);
        }
        return null;
    }

    @Override
    public GrowingSeedInterface insertSeed(GrowingSeedInterface growingSeed, BaseAllotmentInterface allotment) {
        growingSeed.setUUID(null);
        return insertNuxeoSeed(super.insertSeed(growingSeed, allotment), allotment);
    }

    protected GrowingSeedInterface insertNuxeoSeed(GrowingSeedInterface growingSeed,
            final BaseAllotmentInterface allotment) {
        Session session = getNuxeoClient().getSession();
        final DocumentManager service = session.getAdapter(DocumentManager.class);
        DeferredUpdateManager deferredUpdateMgr = getNuxeoClient().getDeferredUpdatetManager();

        Document allotmentDoc = null;
        try {
            allotmentDoc = service.getDocument(allotment.getUUID());
        } catch (Exception e) {
            Log.e(TAG, "Fetching folder allotment " + e.getMessage(), e);
        }

        if (allotmentDoc == null)
            return null;

        try {

            PropertyMap properties = getProperties(growingSeed);

            OperationRequest createOperation = NuxeoManager.getInstance().getSession().newRequest("Document.Create").setHeader(
                    Constants.HEADER_NX_SCHEMAS, "*").setInput(allotmentDoc).set("type", "GrowingSeed").set(
                    "properties", properties);

            currentGrowingSeed = growingSeed;
            AsyncCallback<Object> callback = new AsyncCallback<Object>() {
                @Override
                public void onSuccess(String executionId, Object data) {
                    Document doc = (Document) data;
                    currentGrowingSeed.setUUID(doc.getId());
                    NuxeoGrowingSeedProvider.super.updateSeed(currentGrowingSeed, allotment);

                    try {
                        BaseSeedInterface seed = GotsSeedManager.getInstance().initIfNew(mContext).getSeedById(
                                currentGrowingSeed.getSeedId());
                        service.createRelation(new IdRef(seed.getUUID()), "http://purl.org/dc/terms/isFormatOf",
                                new IdRef(doc.getId()));
                        Log.d(TAG, "onSuccess " + data);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        Log.e(TAG, "Create GrowingSeed relation " + e.getMessage(), e);
                    }
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
        return growingSeed;
    }

    protected PropertyMap getProperties(GrowingSeedInterface growingSeed) {
        PropertyMap properties = new PropertyMap();
        properties.set("dc:title", growingSeed.getSpecie() + " " + growingSeed.getVariety());
        if (growingSeed.getDateSowing() != null)
            properties.set("growingseed:datesowing", growingSeed.getDateSowing());

        return properties;
    }

    @Override
    public void deleteGrowingSeed(GrowingSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            service.remove(new IdRef(seed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        super.deleteGrowingSeed(seed);
    }

}
