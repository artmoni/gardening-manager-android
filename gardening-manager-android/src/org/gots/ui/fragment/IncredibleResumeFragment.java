package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gots.R;

public class IncredibleResumeFragment extends BaseGotsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.incredible_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();

    }

    @Override
    public void onResume() {
        displayIncredibleInformation();
        super.onResume();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

    private void displayIncredibleInformation() {
        if (getCurrentGarden().isIncredibleEdible()) {
            getView().findViewById(R.id.layoutIncredibleDescription).setVisibility(View.VISIBLE);
        } else
            getView().findViewById(R.id.layoutIncredibleDescription).setVisibility(View.GONE);
    }
}
