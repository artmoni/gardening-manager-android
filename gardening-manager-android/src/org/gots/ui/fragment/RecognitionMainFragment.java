package org.gots.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.view.SeedWidgetTile;
import org.gots.ui.PlantDescriptionActivity;

import java.util.List;

/**
 * Created by sfleury on 22/07/15.
 */
public class RecognitionMainFragment extends BaseGotsFragment {

    private LinearLayout horizontalScrollViewRecognition;
    private boolean force = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition_description, null);
        horizontalScrollViewRecognition = (LinearLayout) v.findViewById(R.id.layoutRecognitionResults);
        return v;
    }

    @Override
    public void update() {
        force = true;
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        GotsSeedProvider seedManager = GotsSeedManager.getInstance();
        List<BaseSeed> recognitionSeeds = seedManager.getRecognitionSeeds(force);
        return recognitionSeeds;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseSeed> myRecognitionPlants = (List<BaseSeed>) data;
        if (isAdded()) {
            horizontalScrollViewRecognition.removeAllViews();
            for (final BaseSeed plant : myRecognitionPlants) {
                SeedWidgetTile seedWidget = new SeedWidgetTile(getActivity());
                seedWidget.setSeed(plant);
                seedWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), PlantDescriptionActivity.class);
                        intent.putExtra(PlantDescriptionActivity.GOTS_VENDORSEED_ID, plant.getSeedId());
                        startActivity(intent);
                    }
                });
                horizontalScrollViewRecognition.addView(seedWidget);
            }
            force = false;
        }
        super.onNuxeoDataRetrieved(data);
    }


}
