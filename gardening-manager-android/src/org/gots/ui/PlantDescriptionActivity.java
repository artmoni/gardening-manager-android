package org.gots.ui;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import org.gots.R;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedImpl;
import org.gots.ui.fragment.AllotmentListFragment;
import org.gots.ui.fragment.LoginFragment;
import org.gots.ui.fragment.PlantDescriptionFragment;
import org.gots.ui.fragment.PlantResumeFragment;
import org.gots.ui.fragment.WorkflowTaskFragment;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 17/09/15.
 */
public class PlantDescriptionActivity extends BaseGotsActivity implements AllotmentListFragment.OnAllotmentSelected, WorkflowTaskFragment.OnWorkflowClickListener {
    public static final String GOTS_VENDORSEED_ID = "org.gots.seed.vendorid";

    BaseSeed mSeed = null;
    private PlantDescriptionFragment fragmentDescription;
    private PlantResumeFragment resumeFragment;

    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        mSeed = getSeed();
        return mSeed;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    protected BaseSeed getSeed() {
        String seedUUID = null;
        Uri data = getIntent().getData();
        if (data != null) {
            String scheme = data.getScheme(); // "http"
            String host = data.getHost(); // "my.gardening-manager.com"
            List<String> params = data.getPathSegments();
            if (params != null && params.size() > 0)
                seedUUID = params.get(params.size() - 2); // "status"
        }
        if (getIntent().getExtras() != null && getIntent().getExtras().getInt(GOTS_VENDORSEED_ID) != 0) {
            int seedId = getIntent().getExtras().getInt(GOTS_VENDORSEED_ID);
            mSeed = seedManager.getSeedById(seedId);
        } else if (seedUUID != null) {
            mSeed = seedManager.getSeedByUUID(seedUUID);
        }
        return mSeed;
    }


    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();

        FloatingItem floatingItem = new FloatingItem();
        floatingItem.setTitle(getResources().getString(R.string.action_sow));
        floatingItem.setRessourceId(R.drawable.action_sow);
        floatingItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addContentLayout(new AllotmentListFragment(), null);
            }
        });
        floatingItems.add(floatingItem);
        return floatingItems;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {

        // ********************** Seed description **********************
        if (fragmentDescription == null) {
            fragmentDescription = new PlantDescriptionFragment();
            PlantResumeFragment resumeFragment = new PlantResumeFragment();
            resumeFragment.setOnDescriptionFragmentClicked(new PlantResumeFragment.OnDescriptionFragmentClicked() {
                @Override
                public void onInformationClick(BaseSeed seed, String urlDescription) {
                    Bundle bundle = new Bundle();
                    bundle.putString(WebViewFragment.URL, urlDescription);
                    addContentLayout(new WebViewFragment(), bundle);
                }

                @Override
                public void onAuthenticationNeeded() {
                    addContentLayout(new LoginFragment(), null);
                }

//                @Override
//                public void onLogClick() {
//                    addContentLayout(new ActionsDoneListFragment(), getIntent().getExtras());
//                }
            });
            addResumeLayout(resumeFragment, getIntent().getExtras());
            addMainLayout(fragmentDescription, getIntent().getExtras());
        } else
            fragmentDescription.update();

        getSupportActionBar().setTitle(mSeed.getVariety());

        testWorkflow();
        super.onNuxeoDataRetrieved(data);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            startSupportActionMode(new PlantCallBack(this, mSeed, new PlantCallBack.OnPlantCallBackClicked() {
                @Override
                public void onPlantCallBackClicked() {
                    if (fragmentDescription != null)
                        fragmentDescription.update();
                }
            }));
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private void testWorkflow() {
        new AsyncTask<Void, Void, Documents>() {
            @Override
            protected Documents doInBackground(Void... params) {
                NuxeoWorkflowProvider workflowProvider = new NuxeoWorkflowProvider(getApplicationContext());
                return workflowProvider.getWorkflowOpenTasks(mSeed.getUUID(), true);
            }

            @Override
            protected void onPostExecute(Documents taskDocs) {

                if (taskDocs != null && taskDocs.size() > 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString(WorkflowTaskFragment.GOTS_DOC_ID, mSeed.getUUID());
                    addContentLayout(new WorkflowTaskFragment(), bundle);
                }
                super.onPostExecute(taskDocs);
            }
        }.execute();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_seeddescription, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        Intent i;
//        switch (item.getItemId()) {
//            case R.id.edit:
//                Intent editIntent = new Intent(this, NewSeedActivity.class);
//                editIntent.putExtra(NewSeedActivity.ORG_GOTS_SEEDID, mSeed.getSeedId());
//                startActivity(editIntent);
//                return true;
//            case R.id.action_stock_add:
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... params) {
//                        seedManager.addToStock(mSeed, getCurrentGarden());
//                        return null;
//                    }
//
//                    @Override
//                    protected void onPostExecute(Void result) {
//                        showNotification(mSeed.getName() + " added to stock", false);
//                        runAsyncDataRetrieval();
//                        super.onPostExecute(result);
//                    }
//                }.execute();
//                return true;
//
//            case R.id.delete:
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage(this.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
//                        "OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                new AsyncTask<Void, Integer, Void>() {
//                                    @Override
//                                    protected Void doInBackground(Void... params) {
//                                        seedManager.deleteSeed(mSeed);
//                                        return null;
//                                    }
//
//                                    @Override
//                                    protected void onPostExecute(Void result) {
//                                        showNotification(mSeed.getName() + " has been deleted", false);
//                                        super.onPostExecute(result);
//                                    }
//                                }.execute();
//                                dialog.dismiss();
//                            }
//                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//                return true;
////            case R.id.workflow:
////                AlertDialog.Builder builderWorkflow = new AlertDialog.Builder(this);
////                builderWorkflow.setMessage(this.getResources().getString(R.string.workflow_launch_description)).setCancelable(
////                        false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        new AsyncTask<Void, Void, Void>() {
////                            @Override
////                            protected Void doInBackground(Void... params) {
////                                NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(
////                                        getApplicationContext());
////                                // BaseSeed baseSeedInterface = (BaseSeed) arg0.getItemAtPosition(arg2);
////                                Session session = getNuxeoClient().getSession();
////                                DocumentManager service = session.getAdapter(DocumentManager.class);
////                                try {
////                                    Document docSeed = service.getDocument(mSeed.getPlant().getUUID());
////                                    nuxeoWorkflowProvider.startWorkflowValidation(docSeed);
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
////                                return null;
////                            }
////
////                            protected void onPostExecute(Void result) {
////                                Toast.makeText(getApplicationContext(),
////                                        "Your plant sheet has been sent to the moderator team", Toast.LENGTH_LONG).show();
////                                runAsyncDataRetrieval();
////                            }
////
////                            ;
////                        }.execute();
////                        dialog.dismiss();
////                    }
////                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
////                    public void onClick(DialogInterface dialog, int id) {
////                        dialog.cancel();
////                    }
////                });
////                builderWorkflow.show();
////
////                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public void onAllotmentClick(final BaseAllotmentInterface allotmentInterface) {
        new AsyncTask<Void, Void, GrowingSeed>() {
            @Override
            protected GrowingSeed doInBackground(Void... params) {
                GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getApplicationContext());
                GrowingSeed growingSeed = new GrowingSeedImpl();
                growingSeed.setPlant(mSeed);
                return growingSeedManager.plantingSeed(growingSeed, allotmentInterface);
            }

            @Override
            protected void onPostExecute(GrowingSeed growingSeed) {
                getSupportFragmentManager().popBackStack();
                showNotification(mSeed.getVariety() + " added to allotment " + allotmentInterface.getName(), false);
            }
        }.execute();
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
    public void onWorkflowFinished() {
        getSupportFragmentManager().popBackStack();
        requireAsyncDataRetrieval();
    }
}
