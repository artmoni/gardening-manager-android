package org.gots.action.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.action.BaseActionInterface;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoManager;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Context;
import android.util.Log;

public class NuxeoActionProvider extends LocalActionProvider {

    protected static final String TAG = "NuxeoActionProvider";

    private ArrayList<BaseActionInterface> remoteActions;

    public NuxeoActionProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public ArrayList<BaseActionInterface> getActions() {
        remoteActions = new ArrayList<BaseActionInterface>();
        List<BaseActionInterface> localActions = super.getActions();
        try {
            NuxeoManager nuxeoManager = NuxeoManager.getInstance();
            nuxeoManager.initIfNew(mContext);
            Session session = nuxeoManager.getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean force = true;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Document root = service.getDocument("/default-domain/workspaces/Public hut/Actions");
            Documents docs = service.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" And ecm:parentId = ?",
                    new String[] { root.getId() }, new String[] { "dc:modified DESC" }, "*", 0, 50, cacheParam);

            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
                Document document = iterator.next();
                BaseActionInterface action = NuxeoActionConverter.convert(mContext, document);
                if (action != null) {

                    remoteActions.add(action);
                    Log.i(TAG, "Nuxeo action: " + action);
                } else {
                    Log.w(TAG, "Nuxeo action conversion problem " + document.getTitle() + "- " + document.getId());
                }
            }
            // getNuxeoClient().shutdown();

        } catch (Exception e) {
            Log.e(TAG, "getAllSeeds " + e.getMessage(), e);
            remoteActions = super.getActions();
        }
        return synchronize(localActions, remoteActions);
    }

    private ArrayList<BaseActionInterface> synchronize(List<BaseActionInterface> localActions,
            ArrayList<BaseActionInterface> remoteActions2) {
        ArrayList<BaseActionInterface> myActions = new ArrayList<BaseActionInterface>();
        // Synchronize remote action with local gardens
        for (BaseActionInterface remoteAction : remoteActions2) {
            boolean found = false;
            for (BaseActionInterface localAction : localActions) {
                if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                    found = true;
                    break;
                }
            }
            if (found) { // local and remote => update local
                myActions.add(super.updateAction(remoteAction));
            } else { // remote only => create local
                myActions.add(super.createAction(remoteAction));
            }
        }

        for (BaseActionInterface localAction : localActions) {
            if (localAction.getUUID() == null) { // local only without
                                                 // UUID => create
                                                 // remote
                                                 // myActions.add(createNuxeoGarden(localAction));
            } else {
                boolean found = false;
                for (BaseActionInterface remoteAction : remoteActions2) {
                    if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) { // local only with UUID -> delete local
                    // super.removeGarden(localAction);
                }
            }
        }
        return myActions;
    }
}
