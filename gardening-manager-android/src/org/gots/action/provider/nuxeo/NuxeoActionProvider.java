package org.gots.action.provider.nuxeo;

import android.content.Context;
import android.util.Log;

import org.gots.action.BaseAction;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.nuxeo.NuxeoManager;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import java.util.ArrayList;
import java.util.Iterator;

public class NuxeoActionProvider extends LocalActionProvider {

    protected static final String TAG = "NuxeoActionProvider";

    private ArrayList<BaseAction> remoteActions;

    public NuxeoActionProvider(Context mContext) {
        super(mContext);
        NuxeoManager.getInstance().initIfNew(mContext);
    }

    @Override
    public BaseAction getActionByName(String name) {
        BaseAction action = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean force = true;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Document root = service.getDocument("/default-domain/workspaces/Public hut/Actions");
            // Document actionDoc = service.getchi
            Documents docs = service.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" And ecm:parentId = \""
                            + root.getId() + "\" AND dc:title = \"" + name + "\"", null,
                    new String[]{"dc:modified DESC"}, "*", 0, 50, cacheParam);

            if (docs.size() > 0)
                action = NuxeoActionConverter.convert(mContext, docs.get(0));

        } catch (Exception e) {
            Log.e(TAG, "getActionByName (" + name + ")" + e.getMessage(), e);
            action = super.getActionByName(name);
        }

        return action;
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    @Override
    public ArrayList<BaseAction> getActions(boolean force) {
        remoteActions = new ArrayList<BaseAction>();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Document root = service.getDocument("/default-domain/workspaces/Public hut/Actions");
            Documents docs = service.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" And ecm:parentId = ?",
                    new String[]{root.getId()}, new String[]{"dc:modified DESC"}, "*", 0, 50, cacheParam);

            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext(); ) {
                Document document = iterator.next();
                BaseAction action = NuxeoActionConverter.convert(mContext, document);
                if (action != null) {

                    remoteActions.add(super.updateAction(action));
                    Log.i(TAG, "Nuxeo action: " + action);
                } else {
                    Log.w(TAG, "Nuxeo action conversion problem " + document.getTitle() + "- " + document.getId());
                }
            }
            // getNuxeoClient().shutdown();

        } catch (Exception e) {
            Log.e(TAG, "getAllSeeds " + e.getMessage(), e);
            remoteActions = super.getActions(force);
        }
        return remoteActions;
    }

}
