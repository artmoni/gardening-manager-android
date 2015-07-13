package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gots.R;
import org.gots.seed.view.SeedWidgetLong;

/**
 * Created by sfleury on 13/07/15.
 */
public class PlantCreationFragment extends SeedContentFragment{

    private SeedWidgetLong seedWidgetLong;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_main,null);
        seedWidgetLong = (SeedWidgetLong) v.findViewById(R.id.idSeedWidgetLong);
        return v;
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        seedWidgetLong.setSeed(mSeed);
        super.onNuxeoDataRetrieved(data);
    }
}
