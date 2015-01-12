package org.gots.nuxeo;

import java.util.Iterator;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.gots.bean.TaskInfo;
import org.gots.seed.BaseSeedInterface;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoWorkflowProvider {
    private String TAG = NuxeoWorkflowProvider.class.getSimpleName();

    public NuxeoWorkflowProvider(Context mContext) {
        NuxeoManager.getInstance().initIfNew(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    private void setLifeCycle(BaseSeedInterface seed, String state) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            Log.d(TAG, seed.getUUID());
            service.setState(new IdRef(seed.getUUID()), state);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setLifeCycleApproved(BaseSeedInterface seed) {
        setLifeCycle(seed, "approve");
    }

    public void setLifeCycleRefused(BaseSeedInterface seed) {
        setLifeCycle(seed, "backToProject");
    }

    public void startWorkflowValidation(BaseSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            service.startWorkflow(new IdRef(seed.getUUID()), "validation");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getWorkflowOpenTasks(BaseSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            Documents tasks = service.getOpenTasks(new IdRef(seed.getUUID()));
            for (Document doc : tasks) {
                Log.i(TAG, doc.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Blob getWorkflowTask() {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        Blob tasks = null;
        try {
            tasks = service.getUserTaskPageProvider();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public void completeTaskValidate(TaskInfo task) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            // PropertyMap nodeVar = new PropertyMap();
            // nodeVar.set("button", "validate");
            Document doc = service.completeTaskOperation(new IdRef(task.getId()), "Un commentaire car c'est bon",
                    null, "validate", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void completeTaskRefuse( TaskInfo task) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            // PropertyMap nodeVar = new PropertyMap();
            // nodeVar.set("button", "reject");
            Document doc = service.completeTaskOperation(new IdRef(task.getId()), "Un commentaire de refus", null,
                    "reject", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
