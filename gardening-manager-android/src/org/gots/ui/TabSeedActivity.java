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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.PhotoAction;
import org.gots.action.bean.SowingAction;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.ads.GotsAdvertisement;
import org.gots.analytics.GotsAnalytics;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedImpl;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.view.SeedWidgetLong;
import org.gots.ui.AllotmentListFragment.OnAllotmentSelected;
import org.gots.ui.fragment.ActionsChoiceFragment;
import org.gots.ui.fragment.ActionsChoiceFragment.OnActionSelectedListener;
import org.gots.ui.fragment.WorkflowTaskFragment.OnWorkflowClickListener;
import org.gots.ui.fragment.ActionsListFragment;
import org.gots.ui.fragment.LoginDialogFragment;
import org.gots.ui.fragment.WorkflowTaskFragment;
import org.gots.utils.FileUtilities;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class TabSeedActivity extends TabActivity implements OnActionSelectedListener, OnAllotmentSelected,
        OnWorkflowClickListener {
    public static final String GOTS_VENDORSEED_ID = "org.gots.seed.vendorid";

    public static final String GOTS_GROWINGSEED_ID = "org.gots.seed.id";

    private static final int PICK_IMAGE = 0;

    protected static final String TAG = "TabSeedActivity";

    GrowingSeed mSeed = null;

    private String urlDescription;

    private File cameraPicture;

    private PhotoAction photoAction;

    private Gallery pictureGallery;

    private GotsPurchaseItem gotsPurchase;

    private Fragment fragmentListAction;

    private Fragment fragmentWebView;

    private SeedDescriptionFragment fragmentDescription;

    private ImageView buttonActions;

    private Fragment fragmentWorkflow;

    private MenuItem workflowMenuItem;

    // private TabsAdapter mTabsAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            String cameraFilename = savedInstanceState.getString("CAMERA_FILENAME");
            if (cameraFilename != null)
                cameraPicture = new File(cameraFilename);
        }

        gotsPurchase = new GotsPurchaseItem(this);
        GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
        GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

        setContentView(R.layout.seed_tab);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        pictureGallery = (Gallery) findViewById(R.id.idPictureGallery);
        buttonActions = (ImageView) findViewById(R.id.imageViewOverlyAction);

        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    protected void initView() {
        getSupportActionBar().setTitle(mSeed.getSpecie());

        if (mSeed.getDateSowing() != null) {
            TextView textDateSowing = (TextView) findViewById(R.id.idTextSowingDate);
            textDateSowing.setText(new SimpleDateFormat().format(mSeed.getDateSowing()));

            TextView textDateHarvest = (TextView) findViewById(R.id.idTextHarvestDate);
            if (mSeed.getDateHarvest().getTime() > 0)
                textDateHarvest.setText(new SimpleDateFormat().format(mSeed.getDateHarvest()));
            else {
                Calendar plannedHarvest = Calendar.getInstance();
                plannedHarvest.setTime(mSeed.getDateSowing());
                plannedHarvest.add(Calendar.DAY_OF_YEAR, mSeed.getDurationMin());
                textDateHarvest.setText(new SimpleDateFormat().format(plannedHarvest.getTime()));
                textDateHarvest.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween));
            }
        } else
            findViewById(R.id.idLayoutCulturePeriod).setVisibility(View.GONE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    protected void showOverlayFragment(Fragment actionsListFragment) {
        FragmentTransaction transactionCatalogue = getSupportFragmentManager().beginTransaction();
        transactionCatalogue.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        transactionCatalogue.addToBackStack(null);
        transactionCatalogue.add(R.id.idFragmentOverlay, actionsListFragment).commit();
        findViewById(R.id.idFragmentOverlay).setVisibility(View.VISIBLE);
    }

    protected void hideOverlayFragment() {

        findViewById(R.id.idFragmentOverlay).setVisibility(View.GONE);
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.idFragmentOverlay);
        if (f != null && f.isAdded()) {
            FragmentTransaction transactionCatalogue = getSupportFragmentManager().beginTransaction();
            transactionCatalogue.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transactionCatalogue.remove(f).commit();
            getSupportFragmentManager().popBackStack();
        }
    }

    protected void displayPictureGallery() {
        new AsyncTask<Void, Void, List<File>>() {
            @Override
            protected List<File> doInBackground(Void... params) {
                try {
                    List<File> imageFile = actionseedProvider.getPicture(mSeed);
                    return imageFile;
                } catch (GotsServerRestrictedException e) {
                    Log.w(TAG, e.getMessage());
                    return null;
                }
            }

            protected void onPostExecute(List<File> result) {
                if (result != null && result.size() > 0) {
                    pictureGallery.setSpacing(10);
                    pictureGallery.setAdapter(new GalleryImageAdapter(getApplicationContext(), result));
                } else
                    pictureGallery.setVisibility(View.GONE);
            };
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != Activity.RESULT_CANCELED)
            if (requestCode == PICK_IMAGE) {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        // GotsActionSeedManager.getInstance().initIfNew(getApplicationContext()).uploadPicture(mSeed,
                        // cameraPicture);
                        actionseedProvider.uploadPicture(mSeed, cameraPicture);
                        // photoAction.execute(mSeed);
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        displayPictureGallery();
                    };
                }.execute();

            }
        if (fragmentDescription != null)
            fragmentDescription.update();
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // workaround nullpointer exception bug
        try {
            super.onSaveInstanceState(outState);
            if (cameraPicture != null)
                outState.putString("CAMERA_FILENAME", cameraPicture.getAbsolutePath());
        } catch (Exception e) {
            Log.e(TAG, "onSaveInstanceState workaround nullpointer exception bug");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_seeddescription, menu);

        if (mSeed != null && mSeed.getGrowingSeedId() == 0) {
            menu.findItem(R.id.photo).setVisible(false);
            menu.findItem(R.id.delete).setVisible(false);
            workflowMenuItem = menu.findItem(R.id.workflow);
            if ("project".equals(mSeed.getState()))
                workflowMenuItem.setVisible(true);
            else
                workflowMenuItem.setVisible(false);
        } else {
            // if ("project".equals(mSeed.getState()))
            // else

        }
        // menu.findItem(R.id.workflow).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.help:
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);
            return true;
        case R.id.edit:
            Intent editIntent = new Intent(this, NewSeedActivity.class);
            editIntent.putExtra(NewSeedActivity.ORG_GOTS_SEEDID, mSeed.getSeedId());
            startActivity(editIntent);
            return true;
        case R.id.action_stock_add:
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    seedManager.addToStock(mSeed, getCurrentGarden());
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    runAsyncDataRetrieval();
                    super.onPostExecute(result);
                }
            }.execute();
            return true;

            // case R.id.sow:
            // Intent intent = new Intent(this, GardenActivity.class);
            // intent.putExtra(GardenActivity.SELECT_ALLOTMENT, true);
            // intent.putExtra(GardenActivity.VENDOR_SEED_ID, mSeed.getSeedId());
            // startActivity(intent);
            // return true;
        case R.id.download:
            new AsyncTask<Void, Integer, File>() {
                boolean licenceAvailable = false;

                IabHelper buyHelper;

                private ProgressDialog dialog;

                protected void onPreExecute() {
                    licenceAvailable = gotsPurchase.getFeatureExportPDF() ? true : gotsPurchase.isPremium();
                    dialog = ProgressDialog.show(TabSeedActivity.this, "",
                            getResources().getString(R.string.gots_loading), true);
                    dialog.setCanceledOnTouchOutside(true);
                };

                @Override
                protected File doInBackground(Void... params) {
                    if (licenceAvailable)
                        try {
                            GotsActionSeedProvider provider = GotsActionSeedManager.getInstance().initIfNew(
                                    getApplicationContext());
                            return provider.downloadHistory(mSeed);
                        } catch (GotsServerRestrictedException e) {
                            Log.w(TAG, e.getMessage());
                            licenceAvailable = false;
                            return null;
                        }
                    return null;
                }

                @Override
                protected void onPostExecute(File result) {
                    try {
                        dialog.dismiss();
                        dialog = null;
                    } catch (Exception e) {
                        // nothing
                    }
                    if (!gotsPrefs.isConnectedToServer()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TabSeedActivity.this);
                        builder.setMessage(getResources().getString(R.string.login_connect_restricted)).setCancelable(
                                false).setPositiveButton(getResources().getString(R.string.login_connect),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        LoginDialogFragment dialogFragment = new LoginDialogFragment();
                                        dialogFragment.show(getSupportFragmentManager(), "");
                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                    if (!licenceAvailable) {
                        FragmentManager fm = getSupportFragmentManager();
                        GotsBillingDialog editNameDialog = new GotsBillingDialog(
                                GotsPurchaseItem.SKU_FEATURE_PDFHISTORY);
                        editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);

                        editNameDialog.show(fm, "fragment_edit_name");
                    }
                    if (result != null) {
                        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                        pdfIntent.setDataAndType(Uri.fromFile(result), "application/pdf");
                        startActivity(pdfIntent);
                    }

                }
            }.execute();

            return true;
        case R.id.photo:
            photoAction = new PhotoAction(getApplicationContext());
            Date now = new Date();
            cameraPicture = new File(photoAction.getImageFile(now).getAbsolutePath());
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraPicture));
            // takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityForResult(takePictureIntent, PICK_IMAGE);

            return true;
        case R.id.delete:
            final DeleteAction deleteAction = new DeleteAction(this);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(this.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
                    "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            new AsyncTask<ActionOnSeed, Integer, Void>() {
                                @Override
                                protected Void doInBackground(ActionOnSeed... params) {
                                    ActionOnSeed actionItem = params[0];
                                    actionItem.execute(mSeed);
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(Void result) {
                                    Toast.makeText(getApplicationContext(), "action done", Toast.LENGTH_SHORT).show();
                                    TabSeedActivity.this.finish();
                                    super.onPostExecute(result);
                                }
                            }.execute(deleteAction);
                            sendBroadcast(new Intent(BroadCastMessages.GROWINGSEED_DISPLAYLIST));
                            dialog.dismiss();
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.show();
            return true;
        case R.id.workflow:
            AlertDialog.Builder builderWorkflow = new AlertDialog.Builder(this);
            builderWorkflow.setMessage(this.getResources().getString(R.string.workflow_launch_description)).setCancelable(
                    false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(
                                    getApplicationContext());
                            // BaseSeedInterface baseSeedInterface = (BaseSeedInterface) arg0.getItemAtPosition(arg2);
                            nuxeoWorkflowProvider.startWorkflowValidation(mSeed);
                            return null;
                        }

                        protected void onPostExecute(Void result) {
                            Toast.makeText(getApplicationContext(),
                                    "Your plant sheet has been sent to the moderator team", Toast.LENGTH_LONG).show();
                            runAsyncDataRetrieval();
                        };
                    }.execute();
                    dialog.dismiss();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builderWorkflow.show();

            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        String seedUUID = null;
        Uri data = getIntent().getData();
        if (data != null) {
            String scheme = data.getScheme(); // "http"
            String host = data.getHost(); // "my.gardening-manager.com"
            List<String> params = data.getPathSegments();
            if (params != null && params.size() > 0)
                seedUUID = params.get(params.size() - 2); // "status"
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().getInt(GOTS_GROWINGSEED_ID) != 0) {
            int seedId = getIntent().getExtras().getInt(GOTS_GROWINGSEED_ID);
            mSeed = GotsGrowingSeedManager.getInstance().initIfNew(this).getGrowingSeedById(seedId);
        } else if (getIntent().getExtras() != null && getIntent().getExtras().getInt(GOTS_VENDORSEED_ID) != 0) {
            int seedId = getIntent().getExtras().getInt(GOTS_VENDORSEED_ID);
            // GotsSeedProvider helper = new LocalSeedProvider(getApplicationContext());
            mSeed = (GrowingSeed) seedManager.getSeedById(seedId);
        } else if (seedUUID != null) {
            // GotsSeedProvider helper = new LocalSeedProvider(getApplicationContext());
            mSeed = (GrowingSeed) seedManager.getSeedByUUID(seedUUID);
        }

        if (mSeed == null)
            mSeed = new GrowingSeedImpl(); // DEFAULT SEED

        NuxeoWorkflowProvider workflowProvider = new NuxeoWorkflowProvider(getApplicationContext());
        Documents taskDocs = workflowProvider.getWorkflowOpenTasks(mSeed.getUUID(), true);
        if (taskDocs != null && taskDocs.size() == 0)
            return null;
        return taskDocs;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        initView();
        if (mSeed.getDateSowing() == null)
            buttonActions.setImageDrawable(getResources().getDrawable(R.drawable.action_sow));
        buttonActions.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mSeed.getDateSowing() != null)
                    showOverlayFragment(new ActionsChoiceFragment());
                else {
                    showOverlayFragment(new AllotmentListFragment());
                }
            }

        });

        if (mSeed.getGrowingSeedId() >= 0)
            displayPictureGallery();
        else
            pictureGallery.setVisibility(View.GONE);

        pictureGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                File f = (File) arg0.getItemAtPosition(position);
                File dest = new File(gotsPrefs.getGotsExternalFileDir(), f.getName());
                try {
                    FileUtilities.copy(f, dest);
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(dest), "image/*");
                    startActivity(intent);
                } catch (IOException e) {
                    Log.w(TAG, e.getMessage());
                }

            }
        });

        getIntent().putExtra(WorkflowTaskFragment.GOTS_DOC_ID, mSeed.getUUID());
        if (fragmentWorkflow == null) {
            fragmentWorkflow = new WorkflowTaskFragment();
            // FragmentManager fragmentManager = getSupportFragmentManager();
            // fragmentManager.beginTransaction().replace(R.id.frame_workflow, fragmentWorkflow).commit();
            addTab(fragmentWorkflow, "Validation");
            if (workflowMenuItem != null)
                workflowMenuItem.setVisible(false);
        }

        List<Fragment> fragments = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putInt(GOTS_GROWINGSEED_ID, mSeed.getSeedId());
        bundle.putInt("org.gots.growingseed.id", mSeed.getGrowingSeedId());

        // ********************** Tab actions **********************
        if (mSeed.getGrowingSeedId() > 0) {
            if (fragmentListAction == null) {
                fragmentListAction = Fragment.instantiate(getApplicationContext(), ActionsListFragment.class.getName(),
                        bundle);
                fragments.add(fragmentListAction);
                addTab(fragmentListAction, getResources().getString(R.string.seed_description_tabmenu_actions));
            }
        }

        // ********************** Seed description **********************
        if (fragmentDescription == null) {
            fragmentDescription = (SeedDescriptionFragment) Fragment.instantiate(getApplicationContext(),
                    SeedDescriptionFragment.class.getName(), bundle);
            fragments.add(fragmentDescription);
            addTab(fragmentDescription, getResources().getString(R.string.seed_description_tabmenu_detail));
        } else
            fragmentDescription.update();
        // ********************** Tab Wikipedia**********************
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            urlDescription = "http://" + Locale.getDefault().getLanguage() + ".wikipedia.org/wiki/" + mSeed.getSpecie();
            bundle.putString("org.gots.seed.url", urlDescription);
            if (fragmentWebView == null) {
                fragmentWebView = Fragment.instantiate(getApplicationContext(), WebViewActivity.class.getName(), bundle);
                fragments.add(fragmentWebView);
                addTab(fragmentWebView, getResources().getString(R.string.seed_description_tabmenu_wikipedia));
            }
        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onActionClick(final BaseAction actionInterface) {
        if (actionInterface instanceof ActionOnSeed) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    ((ActionOnSeed) actionInterface).execute(mSeed);
                    return null;
                }

                protected void onPostExecute(Void result) {
                    if (fragmentListAction != null) {
                        ((ActionsListFragment) fragmentListAction).update();
                    }
                };
            }.execute();
        }
        hideOverlayFragment();
    }

    @Override
    public void onActionLongClick(final BaseAction actionInterface) {
        // FragmentManager fm = getSupportFragmentManager();
        // DialogFragment scheduleDialog = new ScheduleActionFragment();
        // Bundle data = new Bundle();
        // data.putInt(GOTS_GROWINGSEED_ID, mSeed.getGrowingSeedId());
        // scheduleDialog.setArguments(data);
        // scheduleDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        // scheduleDialog.show(fm, "fragment_planning");
        if (actionInterface instanceof ActionOnSeed) {
            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    actionInterface.setDuration(7);
                    actionseedProvider.insertAction(mSeed, (ActionOnSeed) actionInterface);
                    return null;
                }

                protected void onPostExecute(Void result) {
                    if (fragmentListAction != null) {
                        ((ActionsListFragment) fragmentListAction).update();
                    }
                };
            }.execute();
        }
        hideOverlayFragment();

    }

    @Override
    public void onAllotmentClick(BaseAllotmentInterface allotmentInterface) {
        SowingAction action = new SowingAction(getApplicationContext());
        action.execute(allotmentInterface, (GrowingSeed) mSeed);
        hideOverlayFragment();
    }

    @Override
    public void onAllotmentLongClick(BaseAllotmentInterface allotmentInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGrowingSeedClick(View v, GrowingSeed growingSeedInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGrowingSeedLongClick(View v, GrowingSeed growingSeedInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface) {
        Toast.makeText(getApplicationContext(), "This feature is not currently supported in this case",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    protected ViewPager getViewPager() {
        return (ViewPager) findViewById(R.id.pager);
    }

    @Override
    public void onWorkflowFinished() {
        removeTab(fragmentWorkflow);
    }
}
