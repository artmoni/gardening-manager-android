package org.gots.ui.fragment;

import org.gots.R;
import org.gots.bean.DefaultGarden;
import org.gots.ui.ProfileActivity;
import org.gots.ui.ProfileCreationFragment;

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
    protected void onCurrentGardenChanged() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onWeatherChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected void onActionChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        if (getCurrentGarden() instanceof DefaultGarden)
            tutorialLevel = COMPLETE_LOCATION;

        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        switch (tutorialLevel) {
        case 1:
            textViewTitle.setText(getResources().getString(R.string.tutorial_a_title));
            textViewDescription.setText(getResources().getString(R.string.tutorial_a_description));
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

        default:
            break;
        }
    }
}
