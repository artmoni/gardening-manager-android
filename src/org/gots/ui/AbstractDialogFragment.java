package org.gots.ui;

import org.gots.preferences.GotsPreferences;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;


public class AbstractDialogFragment extends DialogFragment {

    protected GotsPreferences gotsPrefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(getActivity());
        super.onCreate(savedInstanceState);
    }
}
