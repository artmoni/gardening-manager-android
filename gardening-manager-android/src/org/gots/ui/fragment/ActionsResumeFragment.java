package org.gots.ui.fragment;

import java.util.List;

import org.gots.R;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.ui.ActionActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class ActionsResumeFragment extends BaseGotsFragment {

    ListView listViewActions;

    GotsActionSeedProvider actionSeedManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.actions_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        actionSeedManager = GotsActionSeedManager.getInstance().initIfNew(getActivity());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onCurrentGardenChanged() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onWeatherChanged() {
    }

    @Override
    protected void onActionChanged() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        listViewActions = (ListView) getView().findViewById(R.id.listActions);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<SeedActionInterface> listActions = (List<SeedActionInterface>) data;
        if (isAdded()) {
            ListAllActionAdapter actionAdapter = new ListAllActionAdapter(getActivity(), listActions.subList(0,
                    listActions.size() >= 5 ? 5 : listActions.size()), ListAllActionAdapter.STATUS_TODO);
            listViewActions.setAdapter(actionAdapter);

            getView().findViewById(R.id.buttonActions).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), ActionActivity.class));
                }
            });
            if (actionAdapter.getCount() > 0) {
                getView().findViewById(R.id.layoutDashboardActions).setVisibility(View.VISIBLE);
            } else
                getView().findViewById(R.id.layoutDashboardActions).setVisibility(View.GONE);

        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return actionSeedManager.getActionsToDo();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }
}
