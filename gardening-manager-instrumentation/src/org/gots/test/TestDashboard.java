package org.gots.test;

import org.gots.R;
import org.gots.ui.ActionActivity;
import org.gots.ui.DashboardActivity;
import org.gots.ui.HutActivity;
import org.gots.ui.MyMainGarden;
import org.gots.ui.ProfileActivity;
import org.gots.ui.SplashScreenActivity;

import android.app.Activity;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;

import com.google.api.client.util.Lists;
import com.jayway.android.robotium.solo.Solo;

public class TestDashboard extends ActivityInstrumentationTestCase2<DashboardActivity> {
    private Solo solo;

    public TestDashboard() {
        super(DashboardActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
        Log.i("TestDashboard", "setUp");
    }

    public void testSimpleNavigation() {
        DashboardActivity activity = getActivity();
        assertNotNull(activity);
        // Hut Activity
        Button btHut = (Button) solo.getView(R.id.dashboard_button_hut);
        solo.clickOnView(btHut);
        solo.waitForActivity(HutActivity.class);
        solo.assertCurrentActivity("Wrong activity Hut", HutActivity.class);

        // Return dashboard
        View btHome = (View) solo.getView(android.R.id.home);
        solo.clickOnView(btHome);
        solo.assertCurrentActivity("Wrong activity Dashboard", DashboardActivity.class);

        // Allotment Activity
        View btGarden = (View) solo.getView(R.id.dashboard_button_allotment);
        solo.clickOnView(btGarden);
        solo.waitForActivity(MyMainGarden.class);
        solo.assertCurrentActivity("Wrong activity Allotment", MyMainGarden.class);

        solo.waitForView(R.id.IdGardenAllotmentsList);
        Activity allotmentActivity = solo.getCurrentActivity();
        ListView listAllotments = (ListView) allotmentActivity.findViewById(R.id.IdGardenAllotmentsList);
        if (listAllotments != null && listAllotments.getCount() > 0) {
            Log.i("allotmentActivity", "listAllotments.getCount=" + listAllotments.getCount());
            GridView listSeeds = (GridView) listAllotments.getChildAt(0).findViewById(R.id.IdGrowingSeedList);
            if (listSeeds != null && listSeeds.getCount() > 0) {
                Log.i("allotmentActivity", "listSeeds.getCount=" + listSeeds.getCount());
                View btSeed = (View) listSeeds.getChildAt(0);
                solo.clickOnView(btSeed);
            }
        }
      
        // Return dashboard
        btHome = (View) solo.getView(android.R.id.home);
        solo.clickOnView(btHome);
        solo.clickOnView(btHome);

        solo.assertCurrentActivity("Wrong activity Dashboard", DashboardActivity.class);

        // Action Activity
        View btAction = (View) solo.getView(R.id.dashboard_button_action);
        solo.clickOnView(btAction);
        solo.waitForActivity(ActionActivity.class);
        solo.assertCurrentActivity("Wrong activity Action", ActionActivity.class);

        // Return dashboard
        btHome = (View) solo.getView(android.R.id.home);
        solo.clickOnView(btHome);
        solo.assertCurrentActivity("Wrong activity Dashboard", DashboardActivity.class);

        // Profile Activity
        View btProfile = (View) solo.getView(R.id.dashboard_button_profile);
        solo.clickOnView(btProfile);
        solo.waitForActivity(ProfileActivity.class);
        solo.assertCurrentActivity("Wrong activity Profile", ProfileActivity.class);

        // Return dashboard
        btHome = (View) solo.getView(android.R.id.home);
        solo.clickOnView(btHome);
        solo.assertCurrentActivity("Wrong activity Dashboard", DashboardActivity.class);

    }

    @Override
    protected void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    // public void testDisplayBlackBox() {
    // // solo.assertCurrentActivity("wrong activity", SplashScreenActivity.class);
    // // //Enter 10 in first editfield
    // // solo.enterText(0, "10");
    // // //Enter 20 in first editfield
    // // solo.enterText(1, "20");
    // // //Click on Multiply button
    // // solo.clickOnButton("Multiply");
    // // //Verify that resultant of 10 x 20
    // // assertTrue(solo.searchText("200"));
    // }
}
