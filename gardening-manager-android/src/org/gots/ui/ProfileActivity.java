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
package org.gots.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import org.gots.R;
import org.gots.bean.DefaultGarden;
import org.gots.garden.GardenInterface;
import org.gots.garden.view.OnProfileEventListener;
import org.gots.provider.GardenContentProvider;
import org.gots.ui.BaseGotsActivity.GardenListener;
import org.gots.ui.fragment.BaseGotsFragment;
import org.gots.ui.fragment.LoginFragment;
import org.gots.ui.fragment.ProfileEditorFragment;
import org.gots.ui.fragment.ProfileMapFragment;
import org.gots.ui.fragment.ProfileResumeFragment;
import org.gots.ui.fragment.WeatherListFragment;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends BaseGotsActivity implements OnProfileEventListener, GardenListener {

    private GardenInterface currentGarden;

//    private ProfileMapFragment mapFragment;

    //    private List<GardenInterface> allGardens;
    private ProfileEditorFragment fragment;
    private ProfileResumeFragment resumeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitleBar(R.string.dashboard_profile_name);

        Bundle bundle = new Bundle();
        bundle.putInt(ProfileEditorFragment.PROFILE_EDITOR_MODE, ProfileEditorFragment.OPTION_EDIT);
        resumeFragment = new ProfileResumeFragment();
        addResumeLayout(resumeFragment, bundle);

        addMainLayout(new WeatherListFragment(), bundle);

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
                new AsyncTask<Void, Void, GardenInterface>() {
                    @Override
                    protected GardenInterface doInBackground(Void... params) {
                        return gardenManager.addGarden(new DefaultGarden(getApplicationContext(), null));
                    }

                    @Override
                    protected void onPostExecute(GardenInterface gardenInterface) {
                        if (gardenInterface != null)
                            showNotification("New garden created", false);
                        resumeFragment.update();
                        Bundle options = new Bundle();
                        options.putInt(ProfileEditorFragment.PROFILE_EDITOR_MODE, ProfileEditorFragment.OPTION_EDIT);
                        addMainLayout(new ProfileEditorFragment(), options);
                        super.onPostExecute(gardenInterface);
                    }
                }.execute();
            }
        });
        floatingItems.add(floatingItem);
        return floatingItems;
    }

//    public BroadcastReceiver gardenBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
//                //runAsyncDataRetrieval();
//            }
//        }
//    };

    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    ;

    @Override
    protected Object retrieveNuxeoData() throws Exception {
//        currentGarden = getCurrentGarden();
        return getCurrentGarden();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object myGarden) {
//        this.allGardens = (List<GardenInterface>) myGardens;
        currentGarden = (GardenInterface) myGarden;
        if (currentGarden.getGpsLatitude() == 0 || currentGarden.getGpsLongitude() == 0) {
//            Toast.makeText(getApplicationContext(), "Long press to localize your garden", Toast.LENGTH_LONG).show();
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//            builder.setTitle(getResources().getString(R.string.dialog_garden_localize));
//            builder.setMessage(getResources().getString(R.string.dialog_garden_localize_description));
//
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                }
//
//            });
//
//            AlertDialog alert = builder.create();
//            alert.show();
        } else {

            if (fragment != null)
                fragment.update();

        }
        super.onNuxeoDataRetrieved(myGarden);
    }

    @Override
    protected void onResume() {
//        registerReceiver(gardenBroadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));
        super.onResume();
    }

    @Override
    protected void onPause() {
//        unregisterReceiver(gardenBroadcastReceiver);
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
            case R.id.edit_garden:
                openContentFragment(getCurrentGarden(), true);
                return true;
            case R.id.delete_garden:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Delete a garden");
                builder.setMessage("Are you sure to delete the garden " + currentGarden.getName());

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, GardenInterface>() {
                            @Override
                            protected GardenInterface doInBackground(Void... params) {
                                List<GardenInterface> allGardens = gardenManager.getMyGardens(false);
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
                                    getSupportFragmentManager().popBackStack();
//                                    mapFragment.update();
                                }
                                // sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));
                                else
                                    showNotification("Last garden cannot be deleted", false);
                            }

                            ;
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
            case R.id.map:
                addContentLayout(new ProfileMapFragment(), null);
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
    public void onProfileSelected(final GardenInterface garden) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                gardenManager.setCurrentGarden(garden);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                resumeFragment.update();
                super.onPostExecute(aVoid);
            }
        }.execute();
    }

    @Override
    public void onProfileEdited(final BaseGotsFragment fragment, final GardenInterface garden) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                gardenManager.updateCurrentGarden(garden);
                gardenManager.setCurrentGarden(garden);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                resumeFragment.update();
                if (fragment instanceof ProfileEditorFragment) //TODO launch weather fragment here on main layout
                    addMainLayout(new ProfileMapFragment(), getIntent().getExtras());
                super.onPostExecute(aVoid);
            }
        }.execute();


    }

    protected void openContentFragment(GardenInterface garden, boolean editable) {
        // Fragment creationFragment = getSupportFragmentManager().findFragmentById(getContentLayout());
        Bundle options = new Bundle();
        if (editable) {
            options.putInt(ProfileEditorFragment.PROFILE_EDITOR_MODE, ProfileEditorFragment.OPTION_EDIT);
        }
        if (fragment == null) {
            fragment = new ProfileEditorFragment();
            addMainLayout(fragment, options);
        }
        fragment.update();
    }

    @Override
    public void onProfileCreated(GardenInterface garden) {
//        closeContentFragment();
        getSupportFragmentManager().popBackStack();
        gardenManager.setCurrentGarden(garden);
//        mapFragment.update();
    }

    @Override
    public void onCurrentGardenChanged(GardenInterface garden) {
//        openContentResumeFragment();
        Log.i(TAG, "garden has changed :" + garden);
    }

//    protected void openContentResumeFragment() {
//        // if (findViewById(R.id.IdGardenProfileResume) == null)
//        // return;
//        // GardenResumeFragment gardenResumeFragment = (GardenResumeFragment)
//        // getSupportFragmentManager().findFragmentById(
//        // R.id.IdGardenProfileResume);
//        // if (gardenResumeFragment == null) {
//        // gardenResumeFragment = new GardenResumeFragment();
//        // FragmentTransaction transactionCatalogue = getSupportFragmentManager().beginTransaction();
//        // transactionCatalogue.setCustomAnimations(R.anim.abc_fade_in, R.anim.push_right_out);
//        // transactionCatalogue.replace(R.id.IdGardenProfileResume, gardenResumeFragment).commit();
//        // } else
//        // gardenResumeFragment.update();
//    }
}
