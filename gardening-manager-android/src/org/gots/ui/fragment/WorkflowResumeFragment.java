package org.gots.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

import com.google.gson.Gson;

import org.gots.R;
import org.gots.bean.TaskInfo;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.ui.PlantDescriptionActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkflowResumeFragment extends BaseGotsFragment implements OnItemClickListener {

    TextView workflowTasksTextView;
    Map<Integer, TaskInfo> map = new HashMap<>();
    private Blob tasks;
    private Gallery gallery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.workflow_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gallery = (Gallery) view.findViewById(R.id.galleryWorkflow);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();

    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {

        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
        List<BaseSeed> seeds = new ArrayList<>();
        tasks = nuxeoWorkflowProvider.getUserTaskPageProvider();
        if (tasks != null) {
            BufferedReader r = new BufferedReader(new InputStreamReader(tasks.getStream()));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }

            JSONObject json;

            try {
                json = new JSONObject(String.valueOf(total.toString()));
                JSONArray tasksEntries = json.getJSONArray("entries");
                Gson gson = new Gson();
                for (int i = 0; i < tasksEntries.length(); i++) {
                    TaskInfo task = gson.fromJson(tasksEntries.getString(i), TaskInfo.class);
                    GotsSeedProvider gotsSeedProvider = GotsSeedManager.getInstance().initIfNew(getActivity());
                    BaseSeed seed = gotsSeedProvider.getSeedByUUID(task.getDocref());
                    if (seed != null) {
                        seeds.add(seed);
                        map.put(seed.getSeedId(), task);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return seeds;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (isAdded()) {
            List<BaseSeed> seeds = (List<BaseSeed>) data;
            SeedListAdapter adapter = new VendorSeedListAdapter(getActivity(), seeds);
            gallery.setAdapter(adapter);
            gallery.setOnItemClickListener(this);
        }
        // workflowTasksTextView = (TextView) getView().findViewById(R.id.textViewWorkflowDescription);
        // workflowTasksTextView.setText("" + tasksEntries.length());

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        BaseSeed baseSeed = (BaseSeed) arg0.getItemAtPosition(arg2);
        Intent i = new Intent(getActivity(), PlantDescriptionActivity.class);
        i.putExtra(WorkflowTaskFragment.GOTS_DOC_ID, baseSeed.getUUID());
        i.putExtra(PlantDescriptionActivity.GOTS_VENDORSEED_ID, baseSeed.getSeedId());
        startActivity(i);
    }


}
