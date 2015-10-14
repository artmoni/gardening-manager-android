package org.gots.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import org.gots.R;
import org.gots.seed.BotanicSpecie;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.ListSpeciesAdapter;

import java.util.List;

/**
 * Created by sfleury on 09/07/15.
 */
public class SpeciesFragment extends BaseGotsFragment {

    private GotsSeedManager seedManager;
    private ListView listView;
    private OnSpeciesSelected mCallBack;

    public interface OnSpeciesSelected {
        public void onSpeciesClicked(BotanicSpecie botanicSpecie);
    }

    @Override
    public void onAttach(Activity activity) {
        if (getActivity() instanceof OnSpeciesSelected)
            mCallBack = (OnSpeciesSelected) getActivity();
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_species, null);
        listView = (ListView) v.findViewById(R.id.listViewSpecies);
        seedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
//        listView.setExpanded(true);
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

        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return seedManager.getSpecies(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(final Object data) {
        List<BotanicSpecie> botanicSpecies = (List<BotanicSpecie>) data;
        final ListSpeciesAdapter listSpeciesAdapter = new ListSpeciesAdapter(getActivity(), botanicSpecies
        );
        listView.setAdapter(listSpeciesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    mCallback.onSpeciesSelected(listSpeciesAdapter.getItem(position));
//                mSeed.setSpecie(listSpeciesAdapter.getItem(position).getSpecieName());
                mCallBack.onSpeciesClicked(listSpeciesAdapter.getItem(position));
                listView.setItemChecked(position, true);
                listSpeciesAdapter.notifyDataSetChanged();
                listView.setSelection(position);
            }


        });

        super.onNuxeoDataRetrieved(data);
    }


}
