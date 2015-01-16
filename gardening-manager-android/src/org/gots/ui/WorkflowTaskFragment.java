package org.gots.ui;

import org.gots.R;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.ui.fragment.BaseGotsFragment;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class WorkflowTaskFragment extends BaseGotsFragment implements OnClickListener {
    public static final String GOTS_TASKWORKFLOW_ID = "org.gots.task.id";

    public static final String GOTS_DOC_ID = "org.gots.doc.id";

    // private TaskInfo taskWorkflow;

    TextView workflowTaskName;

    TextView workflowTaskDirective;

    TextView workflowTaskInitiator;

    String docId;

    private String taskId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workflow_task, null);
        workflowTaskDirective = (TextView) view.findViewById(R.id.textWorkflowTaskDirective);
        workflowTaskName = (TextView) view.findViewById(R.id.textWorkflowTaskTitle);
        workflowTaskInitiator = (TextView) view.findViewById(R.id.textView1);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // if (getActivity().getIntent().getSerializableExtra(GOTS_TASKWORKFLOW_ID) != null)
        // taskWorkflow = (TaskInfo) getActivity().getIntent().getSerializableExtra(GOTS_TASKWORKFLOW_ID);
        if (getActivity().getIntent() != null)
            docId = getActivity().getIntent().getExtras().getString(GOTS_DOC_ID);

        Button buttonRefuse = (Button) view.findViewById(R.id.buttonRefused);
        buttonRefuse.setOnClickListener(this);
        Button buttonApprove = (Button) view.findViewById(R.id.buttonApproved);
        buttonApprove.setOnClickListener(this);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonRefused:
            new AsyncTask<Void, Void, Void>() {
                String comment = "";

                protected void onPreExecute() {
                };

                @Override
                protected Void doInBackground(Void... params) {
                    NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
                    nuxeoWorkflowProvider.setWorkflowNodeVar("assignees", "gardening.manager@gmail.com");
                    nuxeoWorkflowProvider.completeTaskRefuse(taskId, comment);
                    return null;
                }
            }.execute();
            break;
        case R.id.buttonApproved:
            new AsyncTask<Void, Void, Void>() {
                String comment = "";

                @Override
                protected Void doInBackground(Void... params) {
                    NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
                    nuxeoWorkflowProvider.completeTaskValidate(taskId, comment);
                    return null;
                }
            }.execute();
            break;

        default:
            break;
        }

        closeFragment();
    }

    @Override
    protected void onCurrentGardenChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onWeatherChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActionChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        NuxeoWorkflowProvider workflowProvider = new NuxeoWorkflowProvider(getActivity());
        Documents taskDocs = workflowProvider.getWorkflowOpenTasks(docId);
        Document currentTask = null;
        if (taskDocs != null && taskDocs.size() > 0)
            currentTask = workflowProvider.getTaskDoc(taskDocs.get(0).getId());
        return currentTask;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        Document doc = (Document) data;
        PropertyMap map = doc.getProperties();
        workflowTaskDirective.setText(map.getString("nt:directive"));
        workflowTaskName.setText(map.getString("nt:name"));
        workflowTaskInitiator.setText(map.getString("nt:initiator"));
        taskId = doc.getId();
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        closeFragment();
        super.onNuxeoDataRetrieveFailed();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    private void closeFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        getFragmentManager().popBackStack();
        transaction.hide(this);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        transaction.commit();
    }

}
