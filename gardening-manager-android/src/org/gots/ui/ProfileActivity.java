/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.garden.view.OnProfileEventListener;
import org.gots.provider.GardenContentProvider;
import org.gots.ui.BaseGotsActivity.GardenListener;
import org.gots.ui.fragment.GardenResumeFragment;
import org.gots.ui.fragment.ProfileEditorFragment;
import org.gots.ui.fragment.ProfileListFragment;
import org.gots.ui.fragment.ProfileMapFragment;

import com.getbase.floatingactionbutton.FloatingActionButton;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;

public class ProfileActivity extends BaseGotsActivity implements OnProfileEventListener, GardenListener {

    private GardenInterface currentGarden;

    private ProfileMapFragment mapFragment;

    private List<GardenInterface> allGardens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_profile_name);

        try {
            runAsyncDataRetrieval();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        FragmentTransaction transactionMap = getSupportFragmentManager().beginTransaction();
        transactionMap.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);

        mapFragment = new ProfileMapFragment();
        transactionMap.replace(R.id.IdGardenProfileList, mapFragment).commit();

        openContentResumeFragment();
    }

    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        FloatingItem floatingItem = new FloatingItem();
        floatingItem.setTitle(getResources().getString(R.string.profile_menu_add));
        floatingItem.setRessourceId(R.drawable.bt_add_garden);
        floatingItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (findViewById(R.id.bottom) != null) {
                    ((SlidingDrawer) findViewById(R.id.bottom)).open();
                }
                openContentFragment(getCurrentGarden(), false);
            }
        });
        floatingItems.add(floatingItem);
        return floatingItems;
    }

    public BroadcastReceiver gardenBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                runAsyncDataRetrieval();
            }
        }
    };

    protected boolean requireAsyncDataRetrieval() {
        return true;
    };

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = getCurrentGarden();
        return gardenManager.getMyGardens(true);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object myGardens) {
        this.allGardens = (List<GardenInterface>) myGardens;
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (currentGarden.getGpsLatitude() == 0 || currentGarden.getGpsLongitude() == 0) {
            // Fragment fragment = new ProfileCreationFragment();
            // FragmentTransaction transactionCatalogue = fragmentManager.beginTransaction();
            // transactionCatalogue.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
            // transactionCatalogue.replace(R.id.IdGardenProfileList, fragment).commit();
            Toast.makeText(getApplicationContext(), "Long press to localize your garden", Toast.LENGTH_LONG).show();
        } else {
            // ProfileCreationFragment creationFragment = (ProfileCreationFragment)
            // getSupportFragmentManager().findFragmentById(
            // R.id.IdGardenProfileContent);

            if (findViewById(R.id.IdGardenProfileContent) != null) {

                ProfileEditorFragment fragment = (ProfileEditorFragment) getSupportFragmentManager().findFragmentById(
                        R.id.IdGardenProfileContent);
                if (fragment == null) {
                    fragment = new ProfileEditorFragment();
                    Bundle options = new Bundle();
                    options.putInt("option", ProfileEditorFragment.OPTION_EDIT);
                    fragment.setArguments(options);
                    FragmentTransaction transactionCatalogue = fragmentManager.beginTransaction();
                    transactionCatalogue.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out);
                    transactionCatalogue.replace(R.id.IdGardenProfileContent, fragment).commitAllowingStateLoss();
                } else
                    fragment.update();
                // creationFragment.getArguments().putInt("option", ProfileCreationFragment.OPTION_EDIT);
                // creationFragment.updateGarden(getCurrentGarden());
            }

        }
        super.onNuxeoDataRetrieved(myGardens);
    }

    @Override
    protected void onResume() {
        registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(gardenBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {

        case R.id.help:
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);
            return true;

        case R.id.new_garden:

            openContentFragment(getCurrentGarden(), false);
            return true;
        case R.id.edit_garden:
            openContentFragment(getCurrentGarden(), true);
            return true;
        case R.id.delete_allotment:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Delete a garden");
            builder.setMessage("Are you sure to delete the garden " + currentGarden.getName());

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    new AsyncTask<Void, Void, GardenInterface>() {
                        @Override
                        protected GardenInterface doInBackground(Void... params) {
                            for (int i = allGardens.size() - 1; i >= 0; i--) {
                                GardenInterface garden = allGardens.get(i);
                                if (garden.getId() != getCurrentGarden().getId()) {
                                    gardenManager.removeGarden(getCurrentGarden());
                                    gardenManager.setCurrentGarden(garden);
                                    return garden;
                                }
                            }
                            return null;
                        }

                        protected void onPostExecute(GardenInterface result) {
                            if (result != null) {
                                closeContentFragment();
                                mapFragment.update();
                            }
                            // sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));
                            else
                                Toast.makeText(getApplicationContext(), "Last garden cannot be deleted",
                                        Toast.LENGTH_LONG).show();
                        };
                    }.execute();

                    dialog.dismiss();
                }

            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();

            // profileAdapter.notifyDataSetChanged();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected String requireRefreshSyncAuthority() {
        return GardenContentProvider.AUTHORITY;
    }

    @Override
    public void onProfileSelected(GardenInterface garden) {
        gardenManager.setCurrentGarden(garden);
        openContentFragment(garden, true);
        openContentResumeFragment();
        mapFragment.update();
    }

    @Override
    public void onProfileEdited(GardenInterface garden) {
        // openContentFragment(garden, true);
        gardenManager.updateCurrentGarden(garden);

    }

    protected void openContentFragment(GardenInterface garden, boolean editable) {
        Fragment creationFragment = getSupportFragmentManager().findFragmentById(R.id.IdGardenProfileContent);
        Bundle options = new Bundle();
        if (editable) {
            options.putInt("option", ProfileEditorFragment.OPTION_EDIT);
        }

        FragmentTransaction transactionCatalogue = getSupportFragmentManager().beginTransaction();
        transactionCatalogue.setCustomAnimations(R.anim.abc_fade_in, R.anim.push_right_out);
        // transactionCatalogue.addToBackStack(null);

        if (findViewById(R.id.IdGardenProfileContent) != null) {
            if (!editable) {
                creationFragment = new ProfileEditorFragment();
                creationFragment.setArguments(options);
                creationFragment.setRetainInstance(false);
                transactionCatalogue.add(R.id.IdGardenProfileContent, creationFragment).commit();
            } else if (creationFragment instanceof ProfileEditorFragment)
                ((ProfileEditorFragment) creationFragment).updateGarden(garden);
        } else {
            creationFragment = new ProfileEditorFragment();
            creationFragment.setArguments(options);
            transactionCatalogue.add(R.id.IdGardenProfileList, creationFragment).commit();
        }
    }

    @Override
    public void onProfileCreated(GardenInterface garden) {
        closeContentFragment();
        mapFragment.update();
    }

    protected void closeContentFragment() {
        Fragment creationFragment = getSupportFragmentManager().findFragmentById(R.id.IdGardenProfileContent);
        if (creationFragment == null)
            creationFragment = getSupportFragmentManager().findFragmentById(R.id.IdGardenProfileList);
        if (creationFragment != null && creationFragment instanceof ProfileEditorFragment)
            getSupportFragmentManager().beginTransaction().remove(creationFragment).commit();
        runAsyncDataRetrieval();
    }

    @Override
    public void onCurrentGardenChanged(GardenInterface garden) {
        openContentResumeFragment();
        Log.i(TAG, "garden has changed :" + garden);
    }

    protected void openContentResumeFragment() {
        if (findViewById(R.id.IdGardenProfileResume) == null)
            return;
        GardenResumeFragment gardenResumeFragment = (GardenResumeFragment) getSupportFragmentManager().findFragmentById(
                R.id.IdGardenProfileResume);
        if (gardenResumeFragment == null) {
            gardenResumeFragment = new GardenResumeFragment();
            FragmentTransaction transactionCatalogue = getSupportFragmentManager().beginTransaction();
            transactionCatalogue.setCustomAnimations(R.anim.abc_fade_in, R.anim.push_right_out);
            transactionCatalogue.replace(R.id.IdGardenProfileResume, gardenResumeFragment).commit();
        } else
            gardenResumeFragment.update();
    }
}
