package org.gots.ui.fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gots.R;
import org.gots.bean.TaskInfo;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;
import org.gots.ui.TabSeedActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;
import android.widget.TextView;

public class WorkflowResumeFragment extends BaseGotsFragment implements OnItemClickListener {

    private Blob tasks;

    TextView workflowTasksTextView;

    private Gallery gallery;

    Map<Integer, TaskInfo> map = new HashMap<>();

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
        NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
        tasks = nuxeoWorkflowProvider.getWorkflowTask();

        BufferedReader r = new BufferedReader(new InputStreamReader(tasks.getStream()));
        StringBuilder total = new StringBuilder();
        String line;
        while ((line = r.readLine()) != null) {
            total.append(line);
        }

        JSONObject json;
        List<BaseSeedInterface> seeds = new ArrayList<>();
        try {
            json = new JSONObject(String.valueOf(total.toString()));
            JSONArray tasksEntries = json.getJSONArray("entries");
            Gson gson = new Gson();
            for (int i = 0; i < tasksEntries.length(); i++) {
                TaskInfo task = gson.fromJson(tasksEntries.getString(i), TaskInfo.class);
                NuxeoSeedProvider nuxeoSeedProvider = new NuxeoSeedProvider(getActivity());
                BaseSeedInterface seed = nuxeoSeedProvider.getSeedByUUID(task.getDocref());
                if (seed != null)
                    seeds.add(seed);
                map.put(seed.getSeedId(), task);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return seeds;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseSeedInterface> seeds = (List<BaseSeedInterface>) data;

        SeedListAdapter adapter = new VendorSeedListAdapter(getActivity(), seeds);
        gallery.setAdapter(adapter);
        gallery.setOnItemClickListener(this);
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
        BaseSeedInterface baseSeedInterface = (BaseSeedInterface) arg0.getItemAtPosition(arg2);
        Intent i = new Intent(getActivity(), TabSeedActivity.class);
        i.putExtra(WorkflowTaskFragment.GOTS_DOC_ID, baseSeedInterface.getUUID());
        i.putExtra(TabSeedActivity.GOTS_VENDORSEED_ID, baseSeedInterface.getSeedId());
        startActivity(i);
    }

}