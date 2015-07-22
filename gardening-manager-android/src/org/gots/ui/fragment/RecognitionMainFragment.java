package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.gots.R;
import org.gots.inapp.GotsPurchaseItem;

import javax.xml.soap.Text;

/**
 * Created by sfleury on 22/07/15.
 */
public class RecognitionMainFragment extends BaseGotsFragment {

    private TextView maxCounterTextView;
    private TextView currentCounterTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition_description, null);
        currentCounterTextView = (TextView) v.findViewById(R.id.textViewCounterCurrent);
        maxCounterTextView = (TextView) v.findViewById(R.id.textViewCounterMax);

        return v;
    }

    @Override
    public void update() {

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
        GotsPurchaseItem gotsPurchaseItem = new GotsPurchaseItem(getActivity());
        currentCounterTextView.setText(String.valueOf(gotsPurchaseItem.getFeatureRecognitionCounter()));
        maxCounterTextView.setText(String.valueOf(gotsPurchaseItem.getFeatureRecognitionMaxCounter()));
        super.onNuxeoDataRetrieved(data);
    }
}
