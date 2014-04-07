package org.gots.test;

import org.gots.ui.SplashScreenActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.jayway.android.robotium.solo.Solo;

public class TestDashboard extends ActivityInstrumentationTestCase2<SplashScreenActivity> {
    private Solo solo;

    public TestDashboard() {
//         super("org.gots.ui",SplashScreenActivity.class);
        super(SplashScreenActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
         solo = new Solo(getInstrumentation(), getActivity());
        Log.i("TestDashboard", "setUp");
    }

    public void testActivity() {
        SplashScreenActivity activity = getActivity();
        assertNotNull(activity);
    }
    @Override
    protected void tearDown() throws Exception {
         solo.finishOpenedActivities();
    }

//    public void testDisplayBlackBox() {
//        // solo.assertCurrentActivity("wrong activity", SplashScreenActivity.class);
//        // //Enter 10 in first editfield
//        // solo.enterText(0, "10");
//        // //Enter 20 in first editfield
//        // solo.enterText(1, "20");
//        // //Click on Multiply button
//        // solo.clickOnButton("Multiply");
//        // //Verify that resultant of 10 x 20
//        // assertTrue(solo.searchText("200"));
//    }
}
