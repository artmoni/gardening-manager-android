package org.gots.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.gots.R;
import org.gots.seed.provider.local.LocalSeedProvider;

/**
 * Created by sfleury on 10/07/15.
 */
public class VarietyFragment extends SeedContentFragment {
    private AutoCompleteTextView autoCompleteVariety;
    LocalSeedProvider helper;
    private OnVarietySelected mCallback;

    public interface OnVarietySelected {
        void onVarietySelected(String variety);
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnVarietySelected) activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(VarietyFragment.class.getSimpleName()
                    + " must implements OnVarietySelected");
        }
        super.onAttach(activity);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_variety, null);
        autoCompleteVariety = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextViewVariety);

        return v;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        helper = new LocalSeedProvider(getActivity());
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        String[] referenceList = null;
        if (mSeed.getSpecie() != null)
            referenceList = helper.getArrayVarietyBySpecie(mSeed.getSpecie());
        else
            referenceList = helper.getArrayVariety();
        return referenceList;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, (String[]) data);
        autoCompleteVariety.setAdapter(adapter);
        autoCompleteVariety.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String variety = autoCompleteVariety.getText().toString();
                mCallback.onVarietySelected(variety);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCompleteVariety.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                String variety = adapter.getItem(arg2);
                mSeed.setVariety(variety);

            }
        });
        autoCompleteVariety.invalidate();
        super.onNuxeoDataRetrieved(data);
    }

}
