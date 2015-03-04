package org.gots.ui.fragment;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.authentication.provider.google.User;
import org.gots.bean.RouteNode;
import org.gots.bean.TaskButton;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.ui.fragment.ActionsResumeFragment.OnActionsClickListener;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WorkflowTaskFragment extends BaseGotsFragment {
    public static final String GOTS_TASKWORKFLOW_ID = "org.gots.task.id";

    public static final String GOTS_DOC_ID = "org.gots.doc.id";

    TextView textviewTaskName;

    TextView textviewTaskDirective;

    TextView textviewTaskInitiator;

    LinearLayout buttonContainer;

    String docId;

    private String taskId;

    private RouteNode node;

    private String TAG = WorkflowTaskFragment.class.getSimpleName();

    private Documents historyTaskDocuments;

    // private TextView textviewTaskLogs;

    private User lastactor;

    private OnWorkflowClickListener mCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.workflow_task, null);
        textviewTaskDirective = (TextView) view.findViewById(R.id.textWorkflowTaskDirective);
        textviewTaskName = (TextView) view.findViewById(R.id.textWorkflowTaskTitle);
        textviewTaskInitiator = (TextView) view.findViewById(R.id.textWorkflowTaskInitiator);
        // textviewTaskLogs = (TextView) view.findViewById(R.id.textWorkflowLogs);
        buttonContainer = (LinearLayout) view.findViewById(R.id.buttonWorkflowLayout);

        return view;
    }
    public interface OnWorkflowClickListener {
        public void onWorkflowFinished();

    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnWorkflowClickListener) activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(WorkflowTaskFragment.class.getSimpleName() + " must implements OnWorkflowClickListener");

        }
        super.onAttach(activity);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        if (getActivity().getIntent() != null)
            docId = getActivity().getIntent().getExtras().getString(GOTS_DOC_ID);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {

    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        NuxeoWorkflowProvider workflowProvider = new NuxeoWorkflowProvider(getActivity());
        Documents taskDocs = workflowProvider.getWorkflowOpenTasks(docId, true);
        Document currentTask = null;
        if (taskDocs != null && taskDocs.size() > 0) {
            currentTask = workflowProvider.getTaskDoc(taskDocs.get(0).getId());
            node = workflowProvider.getRouteNode(currentTask.getId());
        }

        PropertyMap map = currentTask.getProperties();

        lastactor = workflowProvider.getPrincipal(map.getString("nt:initiator"));

        // historyTaskDocuments = workflowProvider.getTaskHistory(new DocRef(docId));
        // for (Document workflowTask : historyTaskDocuments) {
        // Document task = workflowProvider.getTaskDoc(workflowTask.getId());
        // Log.i(TAG, task.getTitle());
        // }
        return currentTask;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (getActivity() == null)
            return;
        Document doc = (Document) data;
        PropertyMap map = doc.getProperties();
        textviewTaskDirective.setText(map.getString("nt:directive"));
        textviewTaskName.setText(map.getString("nt:name"));
        if (lastactor != null)
            textviewTaskInitiator.setText(lastactor.getName() + " " + lastactor.getLastname());
        // if (historyTaskDocuments != null) {
        // StringBuilder builder = new StringBuilder();
        // for (Document logDoc : historyTaskDocuments) {
        // builder.append(logDoc.getTitle());
        // }
        // builder.append("\n");
        // textviewTaskLogs.setText(builder.toString());
        // }
        taskId = doc.getId();
        if (node != null) {
            buttonContainer.removeAllViews();
            for (final TaskButton taskButton : node.getTaskButtons()) {
                Button b = new Button(getActivity());
                b.setText(taskButton.getLabel());
                b.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Log a comment");
//                        alert.setMessage("Tell :");

                        // Set an EditText view to get user input
                        final EditText input = new EditText(getActivity());
                        alert.setView(input);

                        alert.setPositiveButton(getResources().getString(R.string.button_ok),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        // String value = input.getText().toString();
                                        new AsyncTask<Void, Void, Document>() {

                                            @Override
                                            protected Document doInBackground(Void... params) {
                                                NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(
                                                        getActivity());
                                                Document doc = nuxeoWorkflowProvider.completeTask(taskId,
                                                        taskButton.getName(), input.getText().toString());
                                                return doc;
                                            }

                                            protected void onPostExecute(Document result) {
                                                if (result == null) {
                                                    Log.w(TAG, "Error processing workflow " + taskButton.getName());
                                                } else {
                                                    Log.i(TAG, result.getId() + " follow workflow with task : "
                                                            + taskButton.getName());
                                                    mCallback.onWorkflowFinished();
                                                }
                                                runAsyncDataRetrieval();
                                            };

                                        }.execute();
                                        return;
                                    }
                                });

                        alert.setNegativeButton(getResources().getString(R.string.button_cancel),
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        return;
                                    }
                                });
                        alert.show();

                    }
                });
                b.setPadding(5, 5, 5, 5);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(5, 2, 5, 2);
                b.setLayoutParams(lp);
                buttonContainer.addView(b);

            }
        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        super.onNuxeoDataRetrieveFailed();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

}
