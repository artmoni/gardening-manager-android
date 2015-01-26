package org.gots.ui.fragment;

import org.gots.R;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.bean.DefaultGarden;
import org.gots.garden.GardenInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.ui.HutActivity;
import org.gots.ui.MyMainGarden;
import org.gots.ui.ProfileActivity;
import org.gots.ui.ProfileCreationFragment;

import com.google.ads.t;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TutorialResumeFragment extends BaseGotsFragment implements OnClickListener {
    private static final int COMPLETE_LOCATION = 1;

    private static final int COMPLETE_ALLOTMENT = 5;

    private static final int COMPLETE_SEED = 10;

    private static final int COMPLETE_ACTION = 15;

    private int tutorialLevel;

    private TextView textViewTitle;

    private TextView textViewDescription;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tutorial_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        textViewTitle = (TextView) view.findViewById(R.id.textViewTutorial);
        textViewDescription = (TextView) view.findViewById(R.id.textViewTutorialDescription);
        Button button = (Button) view.findViewById(R.id.buttonTutorial);
        button.setOnClickListener(this);
        super.onViewCreated(view, savedInstanceState);
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
        GardenInterface garden = getCurrentGarden();
        if (garden instanceof DefaultGarden || garden.getGpsLatitude() == 0 || garden.getGpsLongitude() == 0)
            tutorialLevel = COMPLETE_LOCATION;
        else if (GotsAllotmentManager.getInstance().initIfNew(getActivity()).getMyAllotments(false).size() == 0)
            tutorialLevel = COMPLETE_ALLOTMENT;
        else if (GotsSeedManager.getInstance().initIfNew(getActivity()).getMyStock(garden).size() == 0)
            tutorialLevel = COMPLETE_SEED;
        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (!isAdded())
            return;
        switch (tutorialLevel) {
        case COMPLETE_LOCATION:
            textViewTitle.setText(getResources().getString(R.string.tutorial_a_title));
            textViewDescription.setText(getResources().getString(R.string.tutorial_a_description));
            break;
        case COMPLETE_ALLOTMENT:
            textViewTitle.setText(getResources().getString(R.string.tutorial_c_title));
            textViewDescription.setText(getResources().getString(R.string.tutorial_c_description));
            break;
        case COMPLETE_SEED:
            textViewTitle.setText(getResources().getString(R.string.tutorial_b_title));
            textViewDescription.setText(getResources().getString(R.string.tutorial_b_description));
            break;
        default:
            break;
        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onClick(View view) {
        switch (tutorialLevel) {
        case COMPLETE_LOCATION:
            Intent i = new Intent(getActivity(), ProfileActivity.class);
            startActivity(i);
            break;
        case COMPLETE_SEED:
            Intent i2 = new Intent(getActivity(), HutActivity.class);
            startActivity(i2);
            break;
        case COMPLETE_ALLOTMENT:
            Intent i3 = new Intent(getActivity(), MyMainGarden.class);
            startActivity(i3);
            break;
        default:
            break;
        }
    }
}
