/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager.BackStackEntry;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.gots.R;
import org.gots.action.bean.SowingAction;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.provider.AllotmentContentProvider;
import org.gots.seed.BaseSeed;
import org.gots.seed.GrowingSeed;
import org.gots.ui.fragment.AllotmentEditorFragment;
import org.gots.ui.fragment.AllotmentEditorFragment.OnAllotmentListener;
import org.gots.ui.fragment.AllotmentListFragment;
import org.gots.ui.fragment.AllotmentListFragment.OnAllotmentSelected;
import org.gots.ui.fragment.BaseGotsFragment;
import org.gots.ui.fragment.CatalogueFragment;
import org.gots.ui.fragment.CatalogueFragment.OnSeedSelected;
import org.gots.ui.fragment.GardenResumeFragment;

import java.util.ArrayList;
import java.util.List;

public class GardenActivity extends BaseGotsActivity implements OnAllotmentSelected, OnSeedSelected,
        OnAllotmentListener {
    Menu menu;
    private BaseAllotmentInterface currentAllotment;
    private AllotmentListFragment allotmentListFragment;
    private CatalogueFragment vendorListFragment;
    private AllotmentEditorFragment editorFragment;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {

        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_allotment_contextual, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
                case R.id.update_allotment:
                    // showDialogRenameAllotment(currentAllotment);
                    displayEditorFragment(currentAllotment);
                    break;
                case R.id.delete_allotment:
                    removeAllotment(currentAllotment);
                    break;
                default:
                    break;
            }
            // listAllotments.setItemChecked(-1, true);// clear selection in listview
            mode.finish();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitleBar(R.string.dashboard_garden_name);

        vendorListFragment = new CatalogueFragment();
        allotmentListFragment = new AllotmentListFragment();
        editorFragment = new AllotmentEditorFragment();

    }

    @Override
    protected String requireRefreshSyncAuthority() {
        return AllotmentContentProvider.AUTHORITY;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_garden, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    private void removeAllotment(final BaseAllotmentInterface selectedAllotment2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete").setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncTask<BaseAllotmentInterface, Integer, Void>() {

                            @Override
                            protected Void doInBackground(BaseAllotmentInterface... params) {
                                allotmentManager.removeAllotment(selectedAllotment2);
                                return null;
                            }

                            protected void onPostExecute(Void result) {
                                allotmentListFragment.update();
                            }

                            ;
                        }.execute();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onAllotmentClick(BaseAllotmentInterface allotment) {
        currentAllotment = allotment;

        displayPlantsFragment();
    }

    protected void displayPlantsFragment() {
        addContentLayout(vendorListFragment, null);
        // FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
        // transactionTutorial.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
        // transactionTutorial.addToBackStack(null);
        // transactionTutorial.replace(getContentLayout(), vendorListFragment).commitAllowingStateLoss();
    }

    protected void displaySeedActivity(GrowingSeed growingSeed) {
        final Intent i = new Intent(this, GrowingPlantDescriptionActivity.class);
        i.putExtra(GrowingPlantDescriptionActivity.GOTS_GROWINGSEED_ID, growingSeed.getId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        return allotmentManager.getMyAllotments(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseAllotmentInterface> allotments = (List<BaseAllotmentInterface>) data;
        if (getSupportFragmentManager().getBackStackEntryCount() == 0)
            if (allotments.size() > 0) {
                displayAllotmentsFragment();
                addResumeLayout(new GardenResumeFragment(), getIntent().getExtras());
            } else {
                displayEditorFragment(null);

            }
        super.onNuxeoDataRetrieved(data);
    }

    protected void displayAllotmentsFragment() {
        if (!allotmentListFragment.isAdded()) {
            addMainLayout(allotmentListFragment, null);
        } else
            allotmentListFragment.update();
    }

    protected void displayEditorFragment(BaseAllotmentInterface allotment) {
        editorFragment.setAllotment(allotment);
        if (!editorFragment.isAdded()) {
            addContentLayout(editorFragment, null);
        } else {
            editorFragment.update();
//            Toast.makeText(getApplicationContext(), "Only one editor at a time", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAllotmentLongClick(BaseAllotmentInterface allotment) {
        currentAllotment = allotment;

        // Start the CAB using the ActionMode.Callback defined above
        startSupportActionMode(mActionModeCallback);

    }

    @Override
    public void onGrowingSeedClick(View v, GrowingSeed growingSeedInterface) {
        displaySeedActivity(growingSeedInterface);
    }

    @Override
    public void onGrowingSeedLongClick(View v, GrowingSeed growingSeedInterface) {
//        QuickSeedActionBuilder actionBuilder = new QuickSeedActionBuilder(this, (GrowingSeedWidget) v, growingSeedInterface);
//        actionBuilder.show();
        startSupportActionMode(new GrowingPlantCallBack(this, growingSeedInterface, new PlantCallBack.OnPlantCallBackClicked() {
            @Override
            public void onPlantCallBackClicked() {
                displayAllotmentsFragment();
            }
        }));
    }

    @Override
    public void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface) {
        // QuickAllotmentActionBuilder actionsBuilder = new QuickAllotmentActionBuilder(v, allotmentInterface);
        // actionsBuilder.show();
        displayEditorFragment(allotmentInterface);
    }

    @Override
    public void onPlantCatalogueLongClick(CatalogueFragment fragment, BaseSeed seed) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPlantFiltered(String filterTitle) {
        showNotification(filterTitle, true);
    }

    @Override
    public void onPlantCatalogueClick(final BaseSeed seed) {
        if (vendorListFragment != null) {
            getSupportFragmentManager().popBackStack();
        }

        if (seed != null) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    SowingAction action = new SowingAction(getApplicationContext());
                    action.execute(currentAllotment, seed);
                    return null;
                }

                protected void onPostExecute(Void result) {
                    // allotmentListFragment.update();
//                    updateFragments();
                    updateFragments();
                    showNotification(currentAllotment.getName() + " +1 " + seed.getName(), false);
                }

                ;

            }.execute();

        }

    }

    @Override
    public void onAllotmentCreated(BaseAllotmentInterface allotment) {
        currentAllotment = allotment;
        // if (editorFragment.isAdded()) {
        // FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
        // transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        // transactionTutorial.remove(editorFragment).commitAllowingStateLoss();
        // }
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                allotmentManager.createAllotment(currentAllotment);
                return null;
            }

            protected void onPostExecute(Void result) {
//                updateFragments();
                runAsyncDataRetrieval();
            }

        }.execute();
        runAsyncDataRetrieval();
    }

    protected void updateFragments() {
        allotmentListFragment.update();
        for (int entry = 0; entry < getSupportFragmentManager().getBackStackEntryCount(); entry++) {
            final BackStackEntry backStackEntryAt = getSupportFragmentManager().getBackStackEntryAt(entry);
            Log.i(TAG, "Found fragment: " + backStackEntryAt.getId());
            if (backStackEntryAt instanceof BaseGotsFragment) {
                ((BaseGotsFragment) backStackEntryAt).update();
            }
        }
    }

    @Override
    public void onAllotmentSeedLongClicked(BaseAllotmentInterface allotment, GrowingSeed growingSeed) {
        startSupportActionMode(new GrowingPlantCallBack(this, growingSeed, new PlantCallBack.OnPlantCallBackClicked() {
            @Override
            public void onPlantCallBackClicked() {
                displayAllotmentsFragment();
            }
        }));
    }

    @Override
    public void onAllotmentAddPlantClicked(BaseGotsFragment fragment, BaseAllotmentInterface allotment) {
        currentAllotment = allotment;
        displayPlantsFragment();
    }

    @Override
    public void onAllotmentModified(BaseAllotmentInterface allotment) {
        currentAllotment = allotment;
        new AsyncTask<Void, Integer, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                allotmentManager.updateAllotment(currentAllotment);
                return null;
            }

            protected void onPostExecute(Void result) {
//                updateFragments();
//                runAsyncDataRetrieval();
                updateFragments();
            }


        }.execute();
    }

    @Override
    public void onAllotmentSeedClicked(BaseAllotmentInterface allotment, GrowingSeed seed) {
        currentAllotment = allotment;
        displaySeedActivity(seed);
    }

    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        FloatingItem floatingItem = new FloatingItem();
        floatingItem.setTitle(getResources().getString(R.string.allotment_button_add));
        floatingItem.setRessourceId(R.drawable.bt_add_allotment);
        floatingItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                displayEditorFragment(null);

            }
        });
        floatingItems.add(floatingItem);
        return floatingItems;
    }
}
