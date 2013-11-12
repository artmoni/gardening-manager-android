package org.gots.seed.provider.nuxeo;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoGrowingSeedProvider extends LocalGrowingSeedProvider {

    private static final String TAG = "NuxeoGrowingSeedProvider";

    private GrowingSeedInterface currentGrowingSeed=null;
    public NuxeoGrowingSeedProvider(Context mContext) {
        super(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        NuxeoManager nuxeoManager = NuxeoManager.getInstance();
        nuxeoManager.initIfNew(mContext);
        return nuxeoManager.getNuxeoClient();
    }

    @Override
    public GrowingSeedInterface insertSeed(final GrowingSeedInterface growingSeed,
            final BaseAllotmentInterface allotment) {

        return insertNuxeoSeed(super.insertSeed(growingSeed, allotment), allotment);
    }

    protected GrowingSeedInterface insertNuxeoSeed( GrowingSeedInterface growingSeed,
            final BaseAllotmentInterface allotment) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
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
