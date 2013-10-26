package org.gots.action.provider.nuxeo;

import java.util.ArrayList;

import org.gots.action.BaseActionInterface;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.GrowingSeedInterface;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.cache.DeferredUpdateManager;
import org.nuxeo.ecm.automation.client.cache.OperationType;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoActionSeedProvider extends LocalActionSeedProvider {

    protected static final String TAG = "NuxeoActionSeedProvider";

    public NuxeoActionSeedProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsToDo() {
        // TODO Auto-generated method stub
        return super.getActionsToDo();
    }

    @Override
    public long insertAction(BaseActionInterface action, GrowingSeedInterface seed) {
        Session session = NuxeoManager.getInstance().getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        DeferredUpdateManager deferredUpdateMgr = NuxeoManager.getInstance().getNuxeoClient().getDeferredUpdatetManager();
        try {
            Document root = documentMgr.getUserHome();

            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", action.getName());

            //Get growing seed document
            Document seedDoc = documentMgr.getDocument(seed.getUUID());
            //Get action document
            Document actionDoc = documentMgr.getDocument(action.getUUID());
            //Create relation
            
            
            OperationRequest createOperation = NuxeoManager.getInstance().getSession().newRequest("Document.Create").setHeader(
                    Constants.HEADER_NX_SCHEMAS, "*").setInput(root).set("type", "ActionSeed").set("properties",
                    properties);

            AsyncCallback<Object> callback = new AsyncCallback<Object>() {
                @Override
                public void onSuccess(String executionId, Object data) {
                    Document doc = (Document) data;
                    Log.d(TAG, "onSuccess ");

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
        return super.insertAction(action, seed);
    }
    

}
