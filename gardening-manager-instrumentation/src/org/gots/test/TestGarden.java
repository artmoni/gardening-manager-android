package org.gots.test;

import org.gots.ui.GardenActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public class TestGarden extends ActivityInstrumentationTestCase2<GardenActivity> {
    private Solo solo;

    public TestGarden() {
        super(GardenActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        Log.i("TestGarden", "setUp");
    }
    
    private void testPlantGardening() {
        // TODO Auto-generated method stub

    }
}
