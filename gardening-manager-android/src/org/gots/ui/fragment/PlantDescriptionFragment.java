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

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.view.SeedWidgetLong;
import org.gots.ui.GrowingPlantDescriptionActivity;
import org.gots.ui.PlantDescriptionActivity;

public class PlantDescriptionFragment extends BaseGotsFragment {
//    public static final String GOTS_SEED_ID = "org.gots.seed.id";
//    private static final String GOTS_GROWINGSEED_ID = "org.gots.growingseed.id";

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

    private OnDescriptionFragmentClicked mCallback;

    public interface OnDescriptionFragmentClicked {
        public void onInformationClick(String url);

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

        seedDescriptionEnvironnement = (TextView) v.findViewById(R.id.IdSeedDescriptionEnvironment);
        seedDescriptionTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionEnvironmentTitle);
        seedDescriptionCulture = (TextView) v.findViewById(R.id.IdSeedDescriptionCulture);
        seedDescriptionCultureTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionCultureTitle);
        seedDescriptionEnnemi = (TextView) v.findViewById(R.id.IdSeedDescriptionEnnemi);
        seedDescriptionEnnemiTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionEnnemiTitle);
        seedDescriptionCultureHarvest = (TextView) v.findViewById(R.id.IdSeedDescriptionHarvest);
        seedDescriptionHarvest = (TextView) v.findViewById(R.id.IdSeedDescriptionHarvestTitle);
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
        BaseSeed mSeed = (BaseSeed) data;

        seedDescriptionEnvironnement.setText(mSeed.getDescriptionEnvironment() != null ? Html.fromHtml(mSeed.getDescriptionEnvironment()) : "");

        seedDescriptionTitle.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (seedDescriptionEnvironnement.getVisibility() == View.VISIBLE)
                    seedDescriptionEnvironnement.setVisibility(View.GONE);
                else
                    seedDescriptionEnvironnement.setVisibility(View.VISIBLE);
            }
        });

        if (mSeed.getDescriptionCultivation() != null && !"null".equals(mSeed.getDescriptionCultivation())) {

            seedDescriptionCulture.setText(Html.fromHtml(mSeed.getDescriptionCultivation() != null ? mSeed.getDescriptionCultivation() : ""));
            seedDescriptionCultureTitle.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (seedDescriptionCulture.getVisibility() == View.VISIBLE)
                        seedDescriptionCulture.setVisibility(View.GONE);
                    else
                        seedDescriptionCulture.setVisibility(View.VISIBLE);
                }
            });

            seedDescriptionEnnemi.setText(Html.fromHtml(mSeed.getDescriptionDiseases() != null ? mSeed.getDescriptionDiseases() : ""));
            seedDescriptionEnnemiTitle.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (seedDescriptionEnnemi.getVisibility() == View.VISIBLE)
                        seedDescriptionEnnemi.setVisibility(View.GONE);
                    else
                        seedDescriptionEnnemi.setVisibility(View.VISIBLE);
                }
            });

            seedDescriptionCultureHarvest.setText(Html.fromHtml(mSeed.getDescriptionHarvest() != null ? mSeed.getDescriptionHarvest() : ""));
            seedDescriptionHarvest.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    if (seedDescriptionCultureHarvest.getVisibility() == View.VISIBLE)
                        seedDescriptionCultureHarvest.setVisibility(View.GONE);
                    else
                        seedDescriptionCultureHarvest.setVisibility(View.VISIBLE);
                }
            });
        }

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }
}
