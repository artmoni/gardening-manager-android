package org.gots.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.action.provider.GotsActionSeedProvider;

import java.util.List;

public class ActionsResumeFragment extends BaseGotsFragment {

    ListView listViewActions;

    GotsActionSeedProvider actionSeedManager;

    OnActionsClickListener mCallback;

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
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnActionsClickListener) activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(ActionsResumeFragment.class.getSimpleName() + " must implements OnActionsClickListener");

        }
        super.onAttach(activity);
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        listViewActions = (ListView) getView().findViewById(R.id.listActions);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<ActionOnSeed> listActions = (List<ActionOnSeed>) data;
        if (isAdded()) {
            ListAllActionAdapter actionAdapter = new ListAllActionAdapter(getActivity(), listActions.subList(0,
                    listActions.size() >= 5 ? 5 : listActions.size()), ListAllActionAdapter.STATUS_TODO);
            listViewActions.setAdapter(actionAdapter);

            getView().findViewById(R.id.buttonActions).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mCallback.onActionMenuClick(v);
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
        return actionSeedManager.getActionsToDo(false);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    public interface OnActionsClickListener {
        public void onActionClick(View v, BaseAction actionInterface);

        public void onActionMenuClick(View v);
    }
}
