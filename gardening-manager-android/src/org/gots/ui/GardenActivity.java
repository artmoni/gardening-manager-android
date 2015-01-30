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

import java.util.List;

import org.gots.R;
import org.gots.action.GardeningActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.SowingAction;
import org.gots.ads.GotsAdvertisement;
import org.gots.allotment.view.QuickAllotmentActionBuilder;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.provider.AllotmentContentProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.view.QuickSeedActionBuilder;
import org.gots.seed.view.SeedWidget;
import org.gots.ui.AllotmentListFragment.OnAllotmentSelected;
import org.gots.ui.VendorListFragment.OnSeedSelected;
import org.gots.ui.fragment.AllotmentEditorFragment;
import org.gots.ui.fragment.AllotmentEditorFragment.OnAllotmentListener;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class GardenActivity extends BaseGotsActivity implements OnAllotmentSelected, OnSeedSelected,
        OnAllotmentListener {

    public static final String SELECT_ALLOTMENT = "allotment.select";

    public static final String VENDOR_SEED_ID = "allotment.vendorseedid";

    protected static final String TAG = "MyMainGarden";

    private BaseAllotmentInterface currentAllotment;

    private AllotmentListFragment allotmentListFragment;

    private VendorListFragment vendorListFragment;

    private AllotmentEditorFragment editorFragment;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_allotments_name);

        // GardenManager gm =GardenManager.getInstance();

        setContentView(R.layout.garden);

        // listAllotments.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.help_hut_2));
        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

        // setProgressAction(new Intent(this, AllotmentNotificationService.class));
        vendorListFragment = new VendorListFragment();
        allotmentListFragment = new AllotmentListFragment();
        editorFragment = new AllotmentEditorFragment();
    }

    @Override
    protected void onRefresh(String AUTHORITY) {
        super.onRefresh(AllotmentContentProvider.AUTHORITY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_garden, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.new_allotment:

            displayEditorFragment(null);
            return true;

        case R.id.help:
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);

            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogRenameAllotment(final BaseAllotmentInterface allotmentInterface) {

        final EditText userinput = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(userinput).setTitle("Allotment's name");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, int id) {
                new AsyncTask<Void, Integer, Void>() {
                    protected void onPreExecute() {
                        allotmentInterface.setName(userinput.getText().toString());
                    };

                    @Override
                    protected Void doInBackground(Void... params) {
                        allotmentManager.updateAllotment(allotmentInterface);
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        // lsa.notifyDataSetChanged();
                        dialog.cancel();
                    };

                }.execute();
            }
        }).setNegativeButton(this.getResources().getString(R.string.button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        // AlertDialog dialog = builder.create();
        builder.setCancelable(true);
        builder.show();

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

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
    public void onSeedClick(BaseSeedInterface seed) {
        if (vendorListFragment != null) {
            FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
            transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transactionTutorial.remove(vendorListFragment).commit();
        }

        if (seed != null) {
            SowingAction action = new SowingAction(getApplicationContext());
            action.execute(currentAllotment, (GrowingSeedInterface) seed);
            sendBroadcast(new Intent(BroadCastMessages.ALLOTMENT_EVENT));
        }

    }

    @Override
    public void onAllotmentClick(BaseAllotmentInterface allotment) {
        currentAllotment = allotment;

        FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
        transactionTutorial.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
        transactionTutorial.addToBackStack(null);
        transactionTutorial.add(R.id.idFragmentAllotmentList, vendorListFragment).commit();
    }

    @Override
    public void onAllotmentLongClick(BaseAllotmentInterface allotment) {
        currentAllotment = allotment;

        // Start the CAB using the ActionMode.Callback defined above
        startSupportActionMode(mActionModeCallback);

    }

    @Override
    public void onGrowingSeedClick(View v, GrowingSeedInterface growingSeedInterface) {
        final Intent i = new Intent(this, TabSeedActivity.class);
        i.putExtra("org.gots.seed.id", growingSeedInterface.getGrowingSeedId());
        i.putExtra("org.gots.seed.url", growingSeedInterface.getUrlDescription());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onGrowingSeedLongClick(View v, GrowingSeedInterface growingSeedInterface) {
        QuickSeedActionBuilder actionBuilder = new QuickSeedActionBuilder(this, (SeedWidget) v, growingSeedInterface);
        actionBuilder.show();
    }

    @Override
    public void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface) {
//        QuickAllotmentActionBuilder actionsBuilder = new QuickAllotmentActionBuilder(v, allotmentInterface);
//        actionsBuilder.show();
        displayEditorFragment(allotmentInterface);
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
        if (allotments.size() > 0) {

            FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
            transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transactionTutorial.replace(R.id.idFragmentAllotmentList, allotmentListFragment).commit();
        } else {
            displayEditorFragment(null);

        }
        super.onNuxeoDataRetrieved(data);
    }

    protected void displayEditorFragment(BaseAllotmentInterface allotment) {
        if (!editorFragment.isAdded()) {
            FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
            transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transactionTutorial.addToBackStack(null);
            editorFragment.setAllotment(allotment);
            transactionTutorial.add(R.id.idFragmentAllotmentList, editorFragment).commit();
        } else
            Toast.makeText(getApplicationContext(), "Only one editor at a time", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllotmentCreated(BaseAllotmentInterface allotment) {
        if (editorFragment.isAdded()) {
            FragmentTransaction transactionTutorial = getSupportFragmentManager().beginTransaction();
            transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transactionTutorial.addToBackStack(null);
            transactionTutorial.remove(editorFragment).commit();
        }
        allotmentListFragment.update();
    }
}
