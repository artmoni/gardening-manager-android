package org.gots.ui;

import org.gots.preferences.GotsPreferences;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockDialogFragment;

public class AbstractDialogFragment extends SherlockDialogFragment {

    protected GotsPreferences gotsPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(getActivity());
        super.onCreate(savedInstanceState);
    }
}
