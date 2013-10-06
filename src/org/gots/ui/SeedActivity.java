/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

public class SeedActivity extends SherlockFragment {
    private int seedId;

    protected BaseSeedInterface mSeed;

    protected int resultCameraActivity = 1;

    private GotsSeedManager seedManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        seedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.seed, container, false);

        Bundle bundle = this.getArguments();
        seedId = bundle.getInt("org.gots.seed.id");

        if (seedId <= 0) {
            Log.e("SeedActivity", "You must provide a org.gots.seed.id as an Extra Int");
            return v;
        }

        mSeed = seedManager.getSeedById(seedId);

        final TextView seedDescriptionEnvironnement = (TextView) v.findViewById(R.id.IdSeedDescriptionEnvironment);
        seedDescriptionEnvironnement.setText(mSeed.getDescriptionGrowth());

        TextView seedDescriptionTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionEnvironmentTitle);
        seedDescriptionTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (seedDescriptionEnvironnement.getVisibility() == View.VISIBLE)
                    seedDescriptionEnvironnement.setVisibility(View.GONE);
                else
                    seedDescriptionEnvironnement.setVisibility(View.VISIBLE);
            }
        });

        final TextView seedDescriptionCulture = (TextView) v.findViewById(R.id.IdSeedDescriptionCulture);
        seedDescriptionCulture.setText(mSeed.getDescriptionCultivation());
        TextView seedDescriptionCultureTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionCultureTitle);
        seedDescriptionCultureTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (seedDescriptionCulture.getVisibility() == View.VISIBLE)
                    seedDescriptionCulture.setVisibility(View.GONE);
                else
                    seedDescriptionCulture.setVisibility(View.VISIBLE);
            }
        });

        final TextView seedDescriptionEnnemi = (TextView) v.findViewById(R.id.IdSeedDescriptionEnnemi);
        seedDescriptionEnnemi.setText(mSeed.getDescriptionDiseases());
        TextView seedDescriptionEnnemiTitle = (TextView) v.findViewById(R.id.IdSeedDescriptionEnnemiTitle);
        seedDescriptionEnnemiTitle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (seedDescriptionEnnemi.getVisibility() == View.VISIBLE)
                    seedDescriptionEnnemi.setVisibility(View.GONE);
                else
                    seedDescriptionEnnemi.setVisibility(View.VISIBLE);
            }
        });

        final TextView seedDescriptionCultureHarvest = (TextView) v.findViewById(R.id.IdSeedDescriptionHarvest);
        seedDescriptionCultureHarvest.setText(mSeed.getDescriptionHarvest());
        TextView seedDescriptionHarvest = (TextView) v.findViewById(R.id.IdSeedDescriptionHarvestTitle);
        seedDescriptionHarvest.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (seedDescriptionCultureHarvest.getVisibility() == View.VISIBLE)
                    seedDescriptionCultureHarvest.setVisibility(View.GONE);
                else
                    seedDescriptionCultureHarvest.setVisibility(View.VISIBLE);
            }
        });

        return v;
    }
    // @Override
    // protected void onActivityResult(int requestCode, int resultCode, Intent
    // data) {
    //
    // if (requestCode == resultCameraActivity) {
    // gallery.refreshDrawableState();
    // gallery.invalidate();
    // }
    // super.onActivityResult(requestCode, resultCode, data);
    // }
    //
    // @Override
    // protected void onDestroy() {
    // GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
    // super.onDestroy();
    // }

}
