package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gots.R;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.provider.GotsSeedProvider;

import java.util.List;

/**
 * Created by sfleury on 22/07/15.
 */
public class RecognitionResumeFragment extends BaseGotsFragment {

    private TextView maxCounterTextView;
    private TextView currentCounterTextView;
    private TextView counterPurchased;
    private TextView textViewPurchased;
    private boolean force = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition_resume, null);
        currentCounterTextView = (TextView) v.findViewById(R.id.textViewCounterCurrent);
        maxCounterTextView = (TextView) v.findViewById(R.id.textViewCounterMax);
        counterPurchased = (TextView) v.findViewById(R.id.textViewCounterPurchased);
        textViewPurchased = (TextView) v.findViewById(R.id.textViewPurchased);
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
            force = false;
        }
        super.onNuxeoDataRetrieved(data);
    }

}
