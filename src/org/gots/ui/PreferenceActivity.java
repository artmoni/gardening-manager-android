package org.gots.ui;


import org.gots.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class PreferenceActivity extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
