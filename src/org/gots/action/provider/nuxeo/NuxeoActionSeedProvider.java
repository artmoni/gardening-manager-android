package org.gots.action.provider.nuxeo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeedInterface;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocumentStatus;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import com.google.android.gms.dynamic.LifecycleDelegate;

import android.content.Context;
import android.util.Log;

public class NuxeoActionSeedProvider extends LocalActionSeedProvider {

    protected static final String TAG = "NuxeoActionSeedProvider";

    public NuxeoActionSeedProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsToDo() {
        // TODO Auto-generated method stub
        return super.getActionsToDo();
    }

    @Override
    public SeedActionInterface insertAction(BaseActionInterface action, GrowingSeedInterface seed) {
        Session session = NuxeoManager.getInstance().getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        try {
            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", action.getName());
            properties.set("action:duration", String.valueOf(action.getDuration()));

            Document docAction = documentMgr.getDocument(action.getUUID());
            if (docAction != null) {

                Document doc = documentMgr.copy(docAction, getActionsFolder(seed, documentMgr));
                documentMgr.update(doc, properties);
                action.setUUID(doc.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return super.insertAction(action, seed);
    }

    protected Document getActionsFolder(GrowingSeedInterface seed, DocumentManager documentMgr) throws Exception {
        boolean subFolderExists = false;
        Document actionFolder = null;
        for (Document subFolder : documentMgr.getChildren(new IdRef(seed.getUUID()))) {

            if ("Actions".equals(subFolder.getTitle())) {
                Document currentDoc = documentMgr.getDocument(subFolder);
                if ("deleted".equals(currentDoc.getState()))
                    continue;
                subFolderExists = true;
                actionFolder = subFolder;
                break;
            }
        }
        if (!subFolderExists) {
            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", "Actions");
            actionFolder = documentMgr.createDocument(new IdRef(seed.getUUID()), "Folder", "Actions", properties);
        }
        return actionFolder;
    }

    @Override
    public long doAction(SeedActionInterface action, GrowingSeedInterface seed) {
        Session session = NuxeoManager.getInstance().getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        try {

            PropertyMap properties = new PropertyMap();
            properties.set("action:dateactiondone", Calendar.getInstance().getTime());
            properties.set("action:description", action.getDescription());
            if (action.getData() != null)
                properties.set("action:data", action.getData().toString());

            // Document newDoc = documentMgr.copy(new DocRef(action.getUUID()), getActionsFolder(seed, documentMgr));
            documentMgr.update(new DocRef(action.getUUID()), properties);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return super.doAction(action, seed);
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
        Session session = NuxeoManager.getInstance().getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        ArrayList<SeedActionInterface> actionsToDo = new ArrayList<SeedActionInterface>();
        try {
            // for (Document actionDoc : documentMgr.getChildren(getActionsFolder(seed, documentMgr))) {
            Documents actionDocs = documentMgr.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + getActionsFolder(seed, documentMgr).getId() + "\" AND action:dateactiondone is null",
                    null, new String[] { "dc:modified DESC" }, "*", 0, 50, CacheBehavior.FORCE_REFRESH);

            for (Document actionDoc : actionDocs) {
                SeedActionInterface action = convert(actionDoc);
                // super.populateState(action, seed);
                action.setGrowingSeedId(seed.getGrowingSeedId());
                actionsToDo.add(action);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return actionsToDo;
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
        Session session = NuxeoManager.getInstance().getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        ArrayList<SeedActionInterface> actionsDone = new ArrayList<SeedActionInterface>();
        try {
            Documents actionDocs = documentMgr.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + getActionsFolder(seed, documentMgr).getId() + "\" AND action:dateactiondone is not null",
                    null, new String[] { "dc:modified DESC" }, "*", 0, 50, CacheBehavior.FORCE_REFRESH);

            for (Document actionDoc : actionDocs) {
                SeedActionInterface action = convert(actionDoc);
                // super.populateState(action, seed);
                action.setGrowingSeedId(seed.getGrowingSeedId());
                actionsDone.add(action);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return actionsDone;
    }

    private SeedActionInterface convert(Document actionDoc) {
        SeedActionInterface action = (SeedActionInterface) ActionFactory.buildAction(mContext, actionDoc.getTitle());
        action.setDateActionDone(actionDoc.getDate("action:dateactiondone"));
        action.setDescription(actionDoc.getString("action:description"));
        action.setData(actionDoc.getString("action:data"));
        action.setUUID(actionDoc.getId());

        if (actionDoc.getString("action:duration") != null)
            action.setDuration(Integer.parseInt(actionDoc.getString("action:duration")));

        return action;
    }
}
