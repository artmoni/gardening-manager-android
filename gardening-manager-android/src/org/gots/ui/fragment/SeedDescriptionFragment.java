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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.view.SeedWidgetLong;

public class SeedDescriptionFragment extends BaseGotsFragment {
    public static final String GOTS_SEED_ID = "org.gots.seed.id";
    private static final String GOTS_GROWINGSEED_ID = "org.gots.growingseed.id";

    private int seedId;

    // protected BaseSeed mSeed;

    protected int resultCameraActivity = 1;

    private GotsSeedProvider seedManager;
    private GotsGrowingSeedManager growingSeedManager;

    private TextView seedDescriptionEnvironnement;

    private TextView seedDescriptionTitle;

    private TextView seedDescriptionCulture;

    private TextView seedDescriptionCultureTitle;

    private TextView seedDescriptionEnnemi;

    private TextView seedDescriptionEnnemiTitle;

    private TextView seedDescriptionCultureHarvest;

    private TextView seedDescriptionHarvest;

    private SeedWidgetLong seedWidgetLong;
    private OnDescriptionFragmentClicked mCallback;

    public interface OnDescriptionFragmentClicked {
        public void onInformationClick(String url);

        public void onLogClick();
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
        View v = inflater.inflate(R.layout.seed, container, false);

//        Bundle bundle = this.getArguments();
//        if (bundle != null && bundle.getInt(GOTS_SEED_ID) != 0) {
//            seedId = bundle.getInt(GOTS_SEED_ID);
//        } else if (bundle != null && bundle.getInt(GOTS_GROWINGSEED_ID) != 0)
//            seedId = bundle.getInt(GOTS_GROWINGSEED_ID);
//
//        if (seedId <= 0) {
//            Log.e("SeedActivity", "You must provide a org.gots.seed.id as an Extra Int");
//            return v;
//        }

        seedDescriptionEnvironnement = (TextView) v.findViewById(R.id.IdSeedDescriptionEnvironment);
        seedDescriptionTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionEnvironmentTitle);
        seedDescriptionCulture = (TextView) v.findViewById(R.id.IdSeedDescriptionCulture);
        seedDescriptionCultureTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionCultureTitle);
        seedDescriptionEnnemi = (TextView) v.findViewById(R.id.IdSeedDescriptionEnnemi);
        seedDescriptionEnnemiTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionEnnemiTitle);
        seedDescriptionCultureHarvest = (TextView) v.findViewById(R.id.IdSeedDescriptionHarvest);
        seedDescriptionHarvest = (TextView) v.findViewById(R.id.IdSeedDescriptionHarvestTitle);
        seedWidgetLong = (SeedWidgetLong) v.findViewById(R.id.IdSeedWidgetLong);
        seedWidgetLong.setOnSeedWidgetLongClickListener(new SeedWidgetLong.OnSeedWidgetLongClickListener() {
            @Override
            public void onInformationClick(String url) {
                mCallback.onInformationClick(url);
            }

            @Override
            public void onLogClick() {
                mCallback.onLogClick();
            }
        });
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
        if (bundle != null && bundle.getInt(GOTS_SEED_ID) != 0) {
            baseSeed = seedManager.getSeedById(bundle.getInt(GOTS_SEED_ID));
        } else if (bundle != null && bundle.getInt(GOTS_GROWINGSEED_ID) != 0)
            baseSeed = growingSeedManager.getGrowingSeedById(bundle.getInt(GOTS_GROWINGSEED_ID));
        return baseSeed;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        BaseSeed mSeed = (BaseSeed) data;

        seedWidgetLong.setSeed(mSeed);

        seedDescriptionEnvironnement.setText(Html.fromHtml(mSeed.getDescriptionEnvironment()));

        seedDescriptionTitle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (seedDescriptionEnvironnement.getVisibility() == View.VISIBLE)
                    seedDescriptionEnvironnement.setVisibility(View.GONE);
                else
                    seedDescriptionEnvironnement.setVisibility(View.VISIBLE);
            }
        });

        seedDescriptionCulture.setText(Html.fromHtml(mSeed.getDescriptionCultivation()));
        seedDescriptionCultureTitle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (seedDescriptionCulture.getVisibility() == View.VISIBLE)
                    seedDescriptionCulture.setVisibility(View.GONE);
                else
                    seedDescriptionCulture.setVisibility(View.VISIBLE);
            }
        });

        seedDescriptionEnnemi.setText(Html.fromHtml(mSeed.getDescriptionDiseases()));
        seedDescriptionEnnemiTitle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (seedDescriptionEnnemi.getVisibility() == View.VISIBLE)
                    seedDescriptionEnnemi.setVisibility(View.GONE);
                else
                    seedDescriptionEnnemi.setVisibility(View.VISIBLE);
            }
        });

        seedDescriptionCultureHarvest.setText(Html.fromHtml(mSeed.getDescriptionHarvest()));
        seedDescriptionHarvest.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (seedDescriptionCultureHarvest.getVisibility() == View.VISIBLE)
                    seedDescriptionCultureHarvest.setVisibility(View.GONE);
                else
                    seedDescriptionCultureHarvest.setVisibility(View.VISIBLE);
            }
        });
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }
}
