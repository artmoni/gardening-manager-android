package org.gots.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import org.gots.R;
import org.gots.seed.BotanicSpecie;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.ListSpeciesAdapter;
import org.gots.ui.ExpandableHeightGridView;

import java.util.List;

/**
 * Created by sfleury on 09/07/15.
 */
public class SpeciesFragment extends SeedContentFragment {

    private GotsSeedManager seedManager;
    private ExpandableHeightGridView gridView;

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
        return seedManager.getSpecies(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(final Object data) {
        List<BotanicSpecie> botanicSpecies = (List<BotanicSpecie>) data;
        final ListSpeciesAdapter listSpeciesAdapter = new ListSpeciesAdapter(getActivity(), botanicSpecies,
                mSeed);
        gridView.setAdapter(listSpeciesAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mCallback.onSpeciesSelected(listSpeciesAdapter.getItem(position));
                mSeed.setSpecie(listSpeciesAdapter.getItem(position).getSpecieName());
                gridView.setItemChecked(position, true);
                listSpeciesAdapter.notifyDataSetChanged();
                notifyObservers();
            }


        });

        super.onNuxeoDataRetrieved(data);
    }


}
