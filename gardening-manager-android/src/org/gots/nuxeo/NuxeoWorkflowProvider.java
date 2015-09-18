package org.gots.nuxeo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.authentication.provider.google.User;
import org.gots.bean.RouteNode;
import org.gots.seed.BaseSeed;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class NuxeoWorkflowProvider {
    private String TAG = NuxeoWorkflowProvider.class.getSimpleName();

    public NuxeoWorkflowProvider(Context mContext) {
        NuxeoManager.getInstance().initIfNew(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    public Documents getDocumentsRoute(BaseSeed seed) {
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

    public RouteNode getRouteNode(String docId) {
        RouteNode node = null;
        try {
            DefaultSession session = (DefaultSession) getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            String json = GET("http://my.gardening-manager.com/nuxeo/site/api/v1/id/" + docId, session);

            JSONObject x = new JSONObject(json);
            try {
                JSONObject properties = x.getJSONObject("properties");
                JSONArray array = properties.getJSONArray("nt:task_variables");
                String routeNodeId = null;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    if ("document.routing.step".equals(object.getString("key"))) {
                        routeNodeId = object.getString("value");
                        break;
                    }

                }
                if (routeNodeId != null) {
                    String routeNodeString = GET("http://my.gardening-manager.com/nuxeo/site/api/v1/id/" + routeNodeId,
                            session);
                    JSONObject routeNodeJson = new JSONObject(routeNodeString);
                    JSONObject nodeProperties = routeNodeJson.getJSONObject("properties");
                    // Gson gson = new GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create();
                    // Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssz").create();
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
                    node = gson.fromJson(nodeProperties.toString(), RouteNode.class);

                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            ;
            // node = service.getDocument(new IdRef("1bb85fc1-904c-4d0d-9753-e7a0a2653e80"), "*");
            // tasksDoc = service.query(
            // "Select * from RouteNode  where rnode:nodeId = ? and ecm:currentLifeCycleState = 'suspended'",
            // new String[] { "Task7a0" }, null, "*", 0, 50, CacheBehavior.STORE);
            // node = service.getDocument(new IdRef(docId), "*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return node;
    }

    public User getPrincipal(String username) {
        User user = null;
        try {
            DefaultSession session = (DefaultSession) getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            String jsonPrincipal = GET("http://my.gardening-manager.com/nuxeo/site/api/v1/user/" + username, session);

            JSONObject x = new JSONObject(jsonPrincipal);
            try {
                JSONObject properties = x.getJSONObject("properties");
                user = new User();
                user.setFirstName(properties.getString("firstName"));
                user.setLastname(properties.getString("lastName"));
                user.setEmail(properties.getString("email"));
                Log.d(TAG, "firstName" + user);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
            ;
            // node = service.getDocument(new IdRef("1bb85fc1-904c-4d0d-9753-e7a0a2653e80"), "*");
            // tasksDoc = service.query(
            // "Select * from RouteNode  where rnode:nodeId = ? and ecm:currentLifeCycleState = 'suspended'",
            // new String[] { "Task7a0" }, null, "*", 0, 50, CacheBehavior.STORE);
            // node = service.getDocument(new IdRef(docId), "*");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public Document getTaskDoc(String taskDocId) {
        Document doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            doc = service.getDocument(new IdRef(taskDocId), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    public void getUsersAndGroups(String username) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Document doc = service.getDocument(username);
            String var = "";
            Document docUsers = service.getUsersAndGroups(doc, "Write", var);

            Log.i(TAG, "Var=" + var);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            // cancel(false);
        }
    }

    public Documents getTaskHistory(DocRef docRef) {
        Documents docs = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            docs = service.query("Select * from TaskDoc where nt:targetDocumentId='" + docRef + "'");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docs;
    }

    public Document startWorkflowValidation(Document doc) {
        Document workflowDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            workflowDoc = service.startWorkflow(doc, "validation");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return workflowDoc;
    }

    /*
     * return open tasks for a user on the document
     */
    public Documents getWorkflowOpenTasks(String docId, boolean force) {
        Documents workflowTaskDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            workflowTaskDoc = service.getOpenTasks(new IdRef(docId), null, null, session.getLogin().getUsername(),
                    force);
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
            tasks = service.getUserTaskPageProvider(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return tasks;
    }

    public Document completeTask(String taskId, String action, String comment) {
        Document doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            doc = service.completeTaskOperation(new IdRef(taskId), comment, null, action, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
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

    public static String GET(String url, DefaultSession session) {
        InputStream inputStream = null;
        String result = "";
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("X-NXDocumentProperties", "*");
            // for (Map.Entry<String, String> entry : req.getConnector().getHeaders().entrySet()) {
            // httpGet.setHeader(entry.getKey(), entry.getValue());
            // }
            // make GET request to the given URL_CLASSNAME
            // HttpResponse httpResponse = httpclient.execute(httpGet);
            HttpResponse httpResponse = session.getConnector().executeSimpleHttp(httpGet);

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}
