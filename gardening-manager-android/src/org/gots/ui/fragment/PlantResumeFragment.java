/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.view.SeedWidgetLong;
import org.gots.ui.GrowingPlantDescriptionActivity;
import org.gots.ui.PlantDescriptionActivity;

public class PlantResumeFragment extends BaseGotsFragment {

    private int seedId;

    protected int resultCameraActivity = 1;

    private GotsSeedProvider seedManager;
    private GotsGrowingSeedManager growingSeedManager;

    private OnDescriptionFragmentClicked mCallback;
    private SeedWidgetLong seedWidgetLong;

    public interface OnDescriptionFragmentClicked {
        public void onInformationClick(BaseSeed seed, String url);

    }

    public void setOnDescriptionFragmentClicked(OnDescriptionFragmentClicked descriptionFragmentClicked) {
        mCallback = descriptionFragmentClicked;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.plant_resume, container, false);
        seedWidgetLong = (SeedWidgetLong) v.findViewById(R.id.idSeedWidgetLong);

        return v;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        Bundle bundle = this.getArguments();
        BaseSeed baseSeed = null;
        if (bundle != null && bundle.getInt(PlantDescriptionActivity.GOTS_VENDORSEED_ID) != 0) {
            baseSeed = seedManager.getSeedById(bundle.getInt(PlantDescriptionActivity.GOTS_VENDORSEED_ID));
        } else if (bundle != null && bundle.getInt(GrowingPlantDescriptionActivity.GOTS_GROWINGSEED_ID) != 0)
            baseSeed = growingSeedManager.getGrowingSeedById(bundle.getInt(GrowingPlantDescriptionActivity.GOTS_GROWINGSEED_ID)).getPlant();
        return baseSeed;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        final BaseSeed mSeed = (BaseSeed) data;

        seedWidgetLong.setSeed(mSeed);
        seedWidgetLong.setOnSeedWidgetLongClickListener(new SeedWidgetLong.OnSeedWidgetLongClickListener() {
            @Override
            public void onInformationClick(String url) {
                mCallback.onInformationClick(mSeed, url);
            }


        });
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }
}
