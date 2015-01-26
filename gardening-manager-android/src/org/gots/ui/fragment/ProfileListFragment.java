package org.gots.ui.fragment;

import java.util.List;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.garden.adapter.ProfileAdapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ProfileListFragment extends BaseGotsFragment implements OnItemClickListener {
    GardenInterface currentGarden;

    GotsGardenManager gardenManager;

    private ProfileAdapter profileAdapter;

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_list, container, false);
        listView = (ListView) v.findViewById(R.id.myList);
        gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        if (listView != null) {
            listView.setOnItemClickListener(this);
        }
        super.onViewCreated(v, savedInstanceState);
    }

    public interface OnGardenSelectedListener {
        public void onGardenSelected(int position);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = gardenManager.getCurrentGarden();
        return gardenManager.getMyGardens(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {

        profileAdapter = new ProfileAdapter(getActivity(), (List<GardenInterface>) data, currentGarden);
        listView.setAdapter(profileAdapter);
        // profileAdapter.notifyDataSetChanged();
        // if (profileAdapter != null && profileAdapter.getCount() == 0) {
        // Intent intentCreation = new Intent(getActivity(), ProfileCreationFragment.class);
        // startActivity(intentCreation);
        // } else {
        // // Select default current garden
        // if (currentGarden == null || currentGarden != null && currentGarden.getId() == -1) {
        // gardenManager.setCurrentGarden(profileAdapter.getItem(0));
        // }
        // }
        super.onNuxeoDataRetrieved(data);
    }

@Override
public void update() {
    // TODO Auto-generated method stub
    
}

    @Override
    protected boolean requireAsyncDataRetrieval() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        gardenManager.setCurrentGarden(profileAdapter.getItem(position));

    }

}
