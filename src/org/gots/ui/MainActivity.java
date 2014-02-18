package org.gots.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.gots.R;
import org.gots.ui.menudrawer.NavDrawerListAdapter;
import org.gots.ui.menudrawer.NavMenuItem;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    ArrayList<NavMenuItem> navDrawerItems = new ArrayList<NavMenuItem>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // mPlanetTitles = getResources().getStringArray(R.array.planets_array);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        navDrawerItems.add(new NavMenuItem(getResources().getString(R.string.dashboard_hut_name),
                R.drawable.dashboard_button_hut_selector, "0"));
        navDrawerItems.add(new NavMenuItem(getResources().getString(R.string.dashboard_profile_name),
                R.drawable.dashboard_button_profile_selector, "0"));
        navDrawerItems.add(new NavMenuItem(getResources().getString(R.string.dashboard_actions_name),
                R.drawable.dashboard_button_action_selector, "0"));
        navDrawerItems.add(new NavMenuItem(getResources().getString(R.string.dashboard_allotments_name),
                R.drawable.dashboard_button_allotment_selector, "0"));
        navDrawerItems.get(0).setCounterVisible(true);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new NavDrawerListAdapter(getApplicationContext(), navDrawerItems));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    /** Swaps fragments in the main content view */
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        Fragment fragment = new VendorListActivity();
        Bundle args = new Bundle();
        args.putInt("org.gots.seed.id", position);
        fragment.setArguments(args);

        // Insert the fragment by replacing any existing fragment
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(navDrawerItems.get(position).getTitle());
        mDrawerLayout.closeDrawer(mDrawerList);
    }
}
