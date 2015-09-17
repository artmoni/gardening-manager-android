package org.gots.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gots.R;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.view.SeedWidgetTile;
import org.gots.ui.PlantDescriptionActivity;
import org.gots.ui.TabSeedActivity;

import java.util.List;

/**
 * Created by sfleury on 22/07/15.
 */
public class RecognitionMainFragment extends BaseGotsFragment {

    private TextView maxCounterTextView;
    private TextView currentCounterTextView;
    private TextView counterPurchased;
    private TextView textViewPurchased;
    private LinearLayout horizontalScrollViewRecognition;
//    private TextView textViewMessage;
    private boolean force = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition_description, null);
        currentCounterTextView = (TextView) v.findViewById(R.id.textViewCounterCurrent);
        maxCounterTextView = (TextView) v.findViewById(R.id.textViewCounterMax);
        counterPurchased = (TextView) v.findViewById(R.id.textViewCounterPurchased);
        textViewPurchased = (TextView) v.findViewById(R.id.textViewPurchased);
        horizontalScrollViewRecognition = (LinearLayout) v.findViewById(R.id.layoutRecognitionResults);
//        textViewMessage = (TextView) v.findViewById(R.id.textViewRecognitionMessage);
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
        List<BaseSeedInterface> recognitionSeeds = seedManager.getRecognitionSeeds(force);
        return recognitionSeeds;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseSeedInterface> myRecognitionPlants = (List<BaseSeedInterface>) data;
        if (isAdded()) {
            GotsPurchaseItem gotsPurchaseItem = new GotsPurchaseItem(getActivity());
            currentCounterTextView.setText(String.valueOf(gotsPurchaseItem.getFeatureRecognitionFreeCounter()));
            maxCounterTextView.setText(String.valueOf(gotsPurchaseItem.RECOGNITION_FREE_COUNTER_MAX));
            if (gotsPurchaseItem.getFeatureRecognitionCounter() > 0) {
                counterPurchased.setVisibility(View.VISIBLE);
                textViewPurchased.setVisibility(View.VISIBLE);
                counterPurchased.setText(String.valueOf(gotsPurchaseItem.getFeatureRecognitionCounter()));
            } else {
                textViewPurchased.setVisibility(View.GONE);
                counterPurchased.setVisibility(View.GONE);
            }
            horizontalScrollViewRecognition.removeAllViews();
            for (final BaseSeedInterface plant : myRecognitionPlants) {
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


//    public void setMessage(String message) {
//        textViewMessage.setText(message);
//    }
}
