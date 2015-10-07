/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.garden.view.OnProfileEventListener;

public class ProfileEditorFragment extends BaseGotsFragment {
    public static final int OPTION_EDIT = 1;

    private static final String TAG = ProfileEditorFragment.class.getSimpleName();
    public static final String PROFILE_EDITOR_MODE = "option";

    private LocationManager mlocManager;

//    GardenInterface garden;

    private int mode = 0;


    private GotsGardenManager gardenManager;

    private OnProfileEventListener mCallback;

    private View buttonValidate;
    private RadioButton publicButton;
    private RadioButton privateButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profilecreation, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        mlocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (getArguments() != null)
            mode = getArguments().getInt(PROFILE_EDITOR_MODE);
        privateButton = (RadioButton) view.findViewById(R.id.radioGardenPrivate);
        publicButton = (RadioButton) view.findViewById(R.id.radioGardenIncredibleEdible);
//        if (mode == OPTION_EDIT) {
//            garden = getCurrentGarden();
//        } else {
//            garden = new Garden();
//        }

//        buttonValidate = view.findViewById(R.id.buttonValidatePosition);
//        buttonValidate.setOnClickListener(this);


    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnProfileEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnProfileEventListener");
        }
        super.onAttach(activity);
    }


//    private boolean verifyForm() {
//        garden.setName(editTextName.getText().toString());
//        garden.setLocalityForecast(editTextWeatherLocality.getText().toString());
//        if (garden.getLocality() == null || "".equals(garden.getLocality())) {
//            Toast.makeText(getActivity(), "Please localize your garden", Toast.LENGTH_LONG).show();
////            buttonWeatherState.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shrink_from_bottom));
//            return false;
//        }
//        if ("".equals(garden.getName())) {
//            Toast.makeText(getActivity(), "Please name your garden", Toast.LENGTH_LONG).show();
//            if (Build.VERSION.SDK_INT >= 16) {
//                editTextName.setBackground(getResources().getDrawable(R.drawable.border_red));
//            } else {
//                editTextName.setBackgroundDrawable(getResources().getDrawable(R.drawable.border_red));
//            }
//            return false;
//        }
//        return true;
//    }
//
//    private void createNewProfile() {
//        if (!verifyForm())
//            return;
//
//        new AsyncTask<Void, Void, GardenInterface>() {
//            @Override
//            protected GardenInterface doInBackground(Void... params) {
//                // garden = buildGarden(new Garden());
//                if (((RadioGroup) getView().findViewById(R.id.radioGardenType)).getCheckedRadioButtonId() == getView().findViewById(
//                        R.id.radioGardenIncredibleEdible).getId()) {
//                    garden.setIncredibleEdible(true);
//                }
////                garden.setLocality(editTextLocality.getText().toString());
//                garden.setLocalityForecast(editTextWeatherLocality.getText().toString());
//                garden = gardenManager.addGarden(garden);
//                if (garden.isIncredibleEdible())
//                    gardenManager.share(garden, "members", "ReadWrite");
//                //gardenManager.setCurrentGarden(garden);
//
//                return garden;
//            }
//
//            protected void onPostExecute(GardenInterface result) {
//                if (result == null)
//                    Toast.makeText(getActivity(), "Error creating new garden, please verify your connection.",
//                            Toast.LENGTH_SHORT).show();
//                else {
//                    mCallback.onProfileCreated(result);
//                }
//
//            }
//
//            ;
//        }.execute();
//
//    }


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
        return gardenManager.getCurrentGarden();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        GardenInterface gardenInterface = (GardenInterface) data;
        if (gardenInterface.isIncredibleEdible())
            publicButton.setSelected(true);
        else
            privateButton.setSelected(true);
        super.onNuxeoDataRetrieved(data);
    }

}
