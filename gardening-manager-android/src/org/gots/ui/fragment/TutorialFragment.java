package org.gots.ui.fragment;

import org.gots.R;
import org.nuxeo.android.fragments.BaseNuxeoFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialFragment extends BaseGotsFragment{

    private int mCurrentTutorialRessource;

    public TutorialFragment() {
    }

    @Override
    public void update() {
        requireAsyncDataRetrieval();
    }

    public TutorialFragment(int currentRessource) {
        mCurrentTutorialRessource = currentRessource;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        if (mCurrentTutorialRessource == 0)
            mCurrentTutorialRessource = R.layout.tutorial_a;
        view = inflater.inflate(mCurrentTutorialRessource, container, false);
        return view;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }
}