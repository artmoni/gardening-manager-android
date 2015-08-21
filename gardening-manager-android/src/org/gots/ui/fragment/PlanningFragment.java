package org.gots.ui.fragment;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import org.gots.R;

import java.lang.reflect.Field;
import java.util.Calendar;

/**
 * Created by sfleury on 10/07/15.
 */
public class PlanningFragment extends SeedContentFragment implements DatePicker.OnDateChangedListener {

    private DatePicker planningSowMin;
    private DatePicker planningSowMax;
    private DatePicker planningHarvestMin;
    private DatePicker planningHarvestMax;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.input_seed_planning, null);
        planningSowMin = (DatePicker) v.findViewById(R.id.IdSeedDateSowingPlanningMin);
        planningSowMax = (DatePicker) v.findViewById(R.id.IdSeedDateSowingPlanningMax);
        planningHarvestMin = (DatePicker) v.findViewById(R.id.IdSeedDateHarvestPlanningMin);
        planningHarvestMax = (DatePicker) v.findViewById(R.id.IdSeedDateHarvestPlanningMax);
        return v;
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        Calendar sowTimeMin = Calendar.getInstance();
        Calendar sowTimeMax = Calendar.getInstance();
        Calendar harvestTimeMin = Calendar.getInstance();
        Calendar harvestTimeMax = Calendar.getInstance();

        if (mSeed.getDateSowingMin() > 0)
            sowTimeMin.set(Calendar.MONTH, mSeed.getDateSowingMin() - 1);
        if (mSeed.getDateSowingMax() > 0)
            sowTimeMax.set(Calendar.MONTH, mSeed.getDateSowingMax() - 1);

        if (mSeed.getDateSowingMin() > 0)
            harvestTimeMin.set(Calendar.MONTH, mSeed.getDateSowingMin() - 1 + mSeed.getDurationMin() / 30);
        planningSowMin.init(sowTimeMin.get(Calendar.YEAR), sowTimeMin.get(Calendar.MONTH),
                sowTimeMin.get(Calendar.DAY_OF_MONTH), this);
        monthFilter(planningSowMin);

        planningSowMax.init(sowTimeMax.get(Calendar.YEAR), sowTimeMax.get(Calendar.MONTH),
                sowTimeMax.get(Calendar.DAY_OF_MONTH), this);
        monthFilter(planningSowMax);

        planningHarvestMin.init(harvestTimeMin.get(Calendar.YEAR), harvestTimeMin.get(Calendar.MONTH),
                harvestTimeMin.get(Calendar.DAY_OF_MONTH), this);
        monthFilter(planningHarvestMin);

        planningHarvestMax.init(harvestTimeMax.get(Calendar.YEAR), harvestTimeMax.get(Calendar.MONTH),
                harvestTimeMax.get(Calendar.DAY_OF_MONTH), this);
        monthFilter(planningHarvestMax);

        super.onNuxeoDataRetrievalStarted();
    }


    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    private void monthFilter(DatePicker picker) {
        try {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
                if (daySpinnerId != 0) {
                    View daySpinner = picker.findViewById(daySpinnerId);
                    if (daySpinner != null) {
                        daySpinner.setVisibility(View.GONE);
                    }
                }
//                int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
//                if (monthSpinnerId != 0) {
//                    View monthSpinner = picker.findViewById(monthSpinnerId);
//                    if (monthSpinner != null) {
//                        monthSpinner.setVisibility(View.GONE);
//                    }
//                }

                int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
                if (yearSpinnerId != 0) {
                    View yearSpinner = picker.findViewById(yearSpinnerId);
                    if (yearSpinner != null) {
                        yearSpinner.setVisibility(View.GONE);
                    }
                }
            } else {
                Field f[] = picker.getClass().getDeclaredFields();
                for (Field field : f) {

                    if (field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner")) {
                        field.setAccessible(true);
                        Object dayPicker = new Object();
                        dayPicker = field.get(picker);
                        ((View) dayPicker).setVisibility(View.GONE);
                    }
                    if (field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner")) {
                        field.setAccessible(true);
                        Object yearPicker = new Object();
                        yearPicker = field.get(picker);
                        ((View) yearPicker).setVisibility(View.GONE);
                    }

                }
            }
        } catch (
                SecurityException e
                )

        {
            Log.d("ERROR", e.getMessage());
        } catch (
                IllegalArgumentException e
                )

        {
            Log.d("ERROR", e.getMessage());
        } catch (
                IllegalAccessException e
                )

        {
            Log.d("ERROR", e.getMessage());
        }

    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        updatePlanning();
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {

        updatePlanning();
    }

    private void updatePlanning() {
        int harvestMin = planningHarvestMin.getMonth() + 1;
        int harvestMax = planningHarvestMax.getMonth() + 1;
        int sowMin = planningSowMin.getMonth() + 1;
        int sowMax = planningSowMax.getMonth() + 1;

        int durationmin;
        if (sowMin > harvestMin)
            durationmin = 12 - sowMin + harvestMin;
        else
            durationmin = harvestMin - sowMin;

        int durationmax;
        if (sowMax > harvestMax)
            durationmax = 12 - sowMax + harvestMax;
        else
            durationmax = harvestMax - sowMax;

        mSeed.setDateSowingMin(sowMin);
        mSeed.setDateSowingMax(sowMax);
        mSeed.setDurationMin(durationmin * 30);
        mSeed.setDurationMax(durationmax * 30);
        notifyObservers();
    }
}
