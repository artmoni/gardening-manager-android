package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gots.R;
import org.gots.inapp.GotsPurchaseItem;

/**
 * Created by sfleury on 22/07/15.
 */
public class RecognitionMainFragment extends BaseGotsFragment {

    private TextView maxCounterTextView;
    private TextView currentCounterTextView;
    private TextView counterPurchased;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition_description, null);
        currentCounterTextView = (TextView) v.findViewById(R.id.textViewCounterCurrent);
        maxCounterTextView = (TextView) v.findViewById(R.id.textViewCounterMax);
        counterPurchased = (TextView) v.findViewById(R.id.textViewCounterPurchased);

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
        if (isAdded()) {
            GotsPurchaseItem gotsPurchaseItem = new GotsPurchaseItem(getActivity());
            currentCounterTextView.setText(String.valueOf(gotsPurchaseItem.getFeatureRecognitionFreeCounter()));
            maxCounterTextView.setText(String.valueOf(gotsPurchaseItem.RECOGNITION_FREE_COUNTER_MAX));
            counterPurchased.setText(String.valueOf(gotsPurchaseItem.getFeatureRecognitionCounter()));
        }
        super.onNuxeoDataRetrieved(data);
    }
}
