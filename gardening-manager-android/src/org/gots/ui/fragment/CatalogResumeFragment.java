package org.gots.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Gallery;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.CatalogueActivity;
import org.gots.ui.PlantDescriptionActivity;

import java.util.Calendar;
import java.util.List;

public class CatalogResumeFragment extends BaseGotsFragment implements OnItemClickListener {
    Gallery gallery;

    GotsSeedManager gotsSeedManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.catalog_resume, null);
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        gotsSeedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
        gallery = (Gallery) getView().findViewById(R.id.gallery1);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (!(data instanceof List<?>))
            return;
        List<BaseSeed> list = (List<BaseSeed>) data;

        if (isAdded()) {
            SeedListAdapter adapter = new VendorSeedListAdapter(getActivity(), list.subList(0,
                    list.size() >= 5 ? 5 : list.size()));
            gallery.setAdapter(adapter);
            gallery.setOnItemClickListener(this);

            getView().findViewById(R.id.buttonHut).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(), CatalogueActivity.class));
                }
            });
        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        List<BaseSeed> seeds = gotsSeedManager.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
        if (seeds.size() == 0)
            seeds = gotsSeedManager.getVendorSeeds(true, 1, 5);
        return seeds;
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        BaseSeed baseSeed = (BaseSeed) arg0.getItemAtPosition(arg2);
        Intent i = new Intent(getActivity(), PlantDescriptionActivity.class);
        i.putExtra(PlantDescriptionActivity.GOTS_VENDORSEED_ID, baseSeed.getSeedId());
        startActivity(i);
    }
}
