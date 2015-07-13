package org.gots.ui.fragment;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.ListSpeciesAdapter;
import org.gots.ui.ExpandableHeightGridView;

/**
 * Created by sfleury on 09/07/15.
 */
public class SpeciesFragment extends SeedContentFragment {

    private GotsSeedManager seedManager;
    private ExpandableHeightGridView gridView;
//    private OnSpeciesSelected mCallback;

//    public interface OnSpeciesSelected {
//        void onSpeciesSelected(String species);
//    }

//    @Override
//    public void onAttach(Activity activity) {
//        try {
//            mCallback = (OnSpeciesSelected) activity;
//        } catch (ClassCastException castException) {
//            throw new ClassCastException(SpeciesFragment.class.getSimpleName()
//                    + " must implements OnSpeciesSelected");
//        }
//        super.onAttach(activity);
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_species, null);
        gridView = (ExpandableHeightGridView) v.findViewById(R.id.layoutSpecieGallery);
        gridView.setExpanded(true);

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
    protected void onNuxeoDataRetrievalStarted() {
        seedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return seedManager.getArraySpecies(true);
    }

    @Override
    protected void onNuxeoDataRetrieved(final Object data) {
        final ListSpeciesAdapter listSpeciesAdapter = new ListSpeciesAdapter(getActivity(), (String[]) data,
                mSeed);
        gridView.setAdapter(listSpeciesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mCallback.onSpeciesSelected(listSpeciesAdapter.getItem(position));
                mSeed.setSpecie(listSpeciesAdapter.getItem(position));
                gridView.setSelection(position);
            }
        });

        super.onNuxeoDataRetrieved(data);
    }


}
