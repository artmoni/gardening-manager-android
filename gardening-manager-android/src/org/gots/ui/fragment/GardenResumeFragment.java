package org.gots.ui.fragment;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class GardenResumeFragment extends BaseGotsFragment {

    ListView listViewActions;

    private GotsGardenManager gardenManager;

    private ImageView gardenType;

    private TextView gardenName;

    private TextView gardenLocality;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.garden_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        gardenType = (ImageView) getView().findViewById(R.id.textViewGardenTypeDescription);
        gardenName = (TextView) getView().findViewById(R.id.textViewGardenName);
        gardenLocality = (TextView) getView().findViewById(R.id.buttonGardenLocality);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        GardenInterface gardenInterface = (GardenInterface) data;
        gardenName.setText(gardenInterface.getName());
        gardenLocality.setText(gardenInterface.getLocality());
        if (gardenInterface.isIncredibleEdible())
            gardenType.setImageDrawable(getResources().getDrawable(R.drawable.ic_garden_incredible));
        else
            gardenType.setImageDrawable(getResources().getDrawable(R.drawable.ic_garden_private));

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return gardenManager.getCurrentGarden();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }
}
