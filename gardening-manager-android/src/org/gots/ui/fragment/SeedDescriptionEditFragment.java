package org.gots.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;

import java.util.ArrayList;

/**
 * Created by sfleury on 11/07/15.
 */
public class SeedDescriptionEditFragment extends SeedContentFragment implements View.OnClickListener {

    public static final int REQUEST_HARVEST = 1;
    public static final int REQUEST_DISEASES = 2;
    public static final int REQUEST_GROWTH = 3;
    public static final int REQUEST_ENVIRONMENT = 4;
    private EditText descriptionGrowth;
    private EditText descriptionDiseases;
    private View descriptionGrowthVoice;
    private View descriptionDiseasesVoice;
    private View descriptionEnvironmentVoice;
    private View descriptionHarvestVoice;
    private EditText descriptionEnvironment;
    private EditText descriptionHarvest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_description, null);
        descriptionGrowthVoice = v.findViewById(R.id.IdSeedDescriptionCultureVoice);
        descriptionDiseasesVoice = v.findViewById(R.id.IdSeedDescriptionEnnemiVoice);
        descriptionEnvironmentVoice = v.findViewById(R.id.IdSeedDescriptionEnvironmentVoice);
        descriptionHarvestVoice = v.findViewById(R.id.IdSeedDescriptionHarvestVoice);

        descriptionDiseasesVoice.setOnClickListener(this);
        descriptionEnvironmentVoice.setOnClickListener(this);
        descriptionGrowthVoice.setOnClickListener(this);
        descriptionHarvestVoice.setOnClickListener(this);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        descriptionGrowth = (EditText) view.findViewById(R.id.IdSeedDescriptionCulture);
        descriptionDiseases = (EditText) view.findViewById(R.id.IdSeedDescriptionEnnemi);
        descriptionEnvironment = (EditText) view.findViewById(R.id.IdSeedDescriptionEnvironment);
        descriptionHarvest = (EditText) view.findViewById(R.id.IdSeedDescriptionHarvest);

        descriptionGrowth.setText(mSeed.getDescriptionEnvironment());
        descriptionDiseases.setText(mSeed.getDescriptionDiseases());
        descriptionHarvest.setText(mSeed.getDescriptionHarvest());
        descriptionEnvironment.setText(mSeed.getDescriptionCultivation());
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
    protected Object retrieveNuxeoData() throws Exception {
        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {

        super.onNuxeoDataRetrieved(data);
    }


    @Override
    public void onClick(View view) {
        mSeed.setDescriptionDiseases(descriptionDiseases.getText().toString());
        mSeed.setDescriptionCultivation(descriptionGrowth.getText().toString());
        mSeed.setDescriptionEnvironment(descriptionEnvironment.getText().toString());
        mSeed.setDescriptionHarvest(descriptionHarvest.getText().toString());
        Intent intent;
        switch (view.getId()) {
            case R.id.IdSeedDescriptionCultureVoice:
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Command me");
                startActivityForResult(intent, REQUEST_GROWTH);
                break;
            case R.id.IdSeedDescriptionEnnemiVoice:
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Command me");
                startActivityForResult(intent, REQUEST_DISEASES);
                break;
            case R.id.IdSeedDescriptionEnvironmentVoice:
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Command me");
                startActivityForResult(intent, REQUEST_ENVIRONMENT);
                break;
            case R.id.IdSeedDescriptionHarvestVoice:
                intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Command me");
                startActivityForResult(intent, REQUEST_HARVEST);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        if (matches.size() > 0) {
            switch (requestCode) {
                case REQUEST_GROWTH:
                    descriptionGrowth.setText(matches.get(0));
                    mSeed.setDescriptionEnvironment(matches.toArray().toString());
                    break;
                case REQUEST_DISEASES:
                    descriptionDiseases.setText(matches.get(0));
                    mSeed.setDescriptionDiseases(matches.toArray().toString());
                    break;
                case REQUEST_ENVIRONMENT:
                    descriptionEnvironment.setText(matches.get(0));
                    mSeed.setDescriptionCultivation(matches.toArray().toString());
                    break;
                case REQUEST_HARVEST:
                    descriptionHarvest.setText(matches.get(0));
                    mSeed.setDescriptionHarvest(matches.toArray().toString());
                    break;
                default:
                    break;
            }
            notifyObservers();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}