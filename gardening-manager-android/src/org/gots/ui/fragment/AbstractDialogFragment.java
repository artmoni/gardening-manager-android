package org.gots.ui.fragment;

import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class AbstractDialogFragment extends DialogFragment {

    protected GotsPreferences gotsPrefs;

    protected GotsContext getGotsContext() {
        return GotsContext.get(getActivity());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        gotsPrefs = getGotsContext().getServerConfig();
        super.onCreate(savedInstanceState);
    }
}
