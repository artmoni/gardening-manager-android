package org.gots.nuxeo;

import java.util.ArrayList;
import java.util.Properties;

import org.gots.bean.TaskInfo;
import org.gots.seed.BaseSeedInterface;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyList;
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

    public Documents getDocumentsRoute(BaseSeedInterface seed) {
        Documents documentsRoute = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            documentsRoute = service.query(
                    "Select * from DocumentRoute where docri:participatingDocuments = ? AND ecm:currentLifeCycleState = 'running'",
                    new String[] { seed.getUUID() }, null, "*", 0, 50, CacheBehavior.STORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentsRoute;
    }

    // public Documents getTasksDoc(String docId) {
    // Session session = getNuxeoClient().getSession();
    // DocumentManager service = session.getAdapter(DocumentManager.class);
    // Documents tasksDoc = null;
    // try {
    // tasksDoc = service.query(
    // "Select * from TaskDoc where ecm:currentLifeCycleState = 'opened' AND nt:targetDocumentId=?",
    // new String[] { docId }, null, "*", 0, 50, CacheBehavior.STORE);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // return tasksDoc;
    // }

    public Documents getRouteNode(String docId) {

        Documents tasksDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            tasksDoc = service.query(
                    "Select * from RouteNode  where rnode:nodeId = ? and ecm:currentLifeCycleState = 'suspended'",
                    new String[] { "Task7a0" }, null, "*", 0, 50, CacheBehavior.STORE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasksDoc;
    }

    public Document getTaskDoc(String taskDocId) {
        Document doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.getDocument(new IdRef(taskDocId), true);
            doc = service.getDocument(new IdRef(taskDocId), "*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public Document startWorkflowValidation(BaseSeedInterface seed) {
        Document workflowDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            workflowDoc = service.startWorkflow(new IdRef(seed.getUUID()), "validation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflowDoc;
    }

    /*
     * return open tasks for a user on the document 
     */
    public Documents getWorkflowOpenTasks(String docId) {
        Documents workflowTaskDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            workflowTaskDoc = service.getOpenTasks(new IdRef(docId), null, null, session.getLogin().getUsername());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflowTaskDoc;
    }

    public Blob getUserTaskPageProvider() {
        Blob tasks = null;
        try {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
            tasks = service.getUserTaskPageProvider();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public Document completeTaskValidate(String taskId, String comment) {
        Document doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            doc = service.completeTaskOperation(new IdRef(taskId), comment, null, "validate", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public Document completeTaskRefuse(String taskId, String comment) {
        Document doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Properties properties = new Properties();
            ArrayList<String> array = new ArrayList<>();
            array.add("gardening.manager@gmail.com");
            properties.setProperty("assignees", array.toArray().toString());
            doc = service.completeTaskOperation(new IdRef(taskId), comment, properties, "reject", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public void setWorkflowNodeVar(String key, String value) {
        Document doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.setWorkflowNodeVar(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWorkflowVar(String key, String value) {
    }
}
