package org.gots.ui;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Toast;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionManager;
import org.gots.action.bean.SowingAction;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.ui.fragment.ActionsDoneListFragment;
import org.gots.ui.fragment.AllotmentListFragment;
import org.gots.ui.fragment.SeedDescriptionFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 17/09/15.
 */
public class PlantDescriptionActivity extends BaseGotsActivity implements AllotmentListFragment.OnAllotmentSelected {
    public static final String GOTS_VENDORSEED_ID = "org.gots.seed.vendorid";

    public static final String GOTS_GROWINGSEED_ID = "org.gots.seed.id";

    BaseSeed mSeed = null;
    private SeedDescriptionFragment fragmentDescription;

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
            mSeed =  seedManager.getSeedById(seedId);
        } else if (seedUUID != null) {
            // GotsSeedProvider helper = new LocalSeedProvider(getApplicationContext());
            mSeed = (GrowingSeed) seedManager.getSeedByUUID(seedUUID);
        }
//        if (mSeed != null) {
//            NuxeoWorkflowProvider workflowProvider = new NuxeoWorkflowProvider(getApplicationContext());
//            taskDocs = workflowProvider.getWorkflowOpenTasks(mSeed.getUUID(), true);
//        }

        return mSeed;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        final Bundle bundle = new Bundle();
        bundle.putInt(SeedDescriptionFragment.GOTS_SEED_ID, mSeed.getSeedId());
//        bundle.putInt("org.gots.growingseed.id", mSeed.getGrowingSeedId());

        // ********************** Seed description **********************
        if (fragmentDescription == null) {
            fragmentDescription = new SeedDescriptionFragment();
            fragmentDescription.setOnDescriptionFragmentClicked(new SeedDescriptionFragment.OnDescriptionFragmentClicked() {
                @Override
                public void onInformationClick(String urlDescription) {
                    Bundle bundle = new Bundle();
                    bundle.putString(WebViewFragment.URL, urlDescription);
                    addContentLayout(new WebViewFragment(), bundle);
                }

                @Override
                public void onLogClick() {
                    addContentLayout(new ActionsDoneListFragment(), bundle);
                }
            });
            addMainLayout(fragmentDescription, bundle);
        } else
            fragmentDescription.update();

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(mSeed.getVariety());

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        if (!(mSeed instanceof GrowingSeed)) {

            FloatingItem floatingItem = new FloatingItem();
            floatingItem.setTitle(getResources().getString(R.string.action_sow));
            floatingItem.setRessourceId(R.drawable.action_sow);
            floatingItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
//                    showOverlayFragment(new AllotmentListFragment());
                    addContentLayout(new AllotmentListFragment(), null);
                }
            });
            floatingItems.add(floatingItem);
        } else {
            List<BaseAction> actionInterfaces = (List<BaseAction>) GotsActionManager.getInstance().initIfNew(
                    getApplicationContext()).getActions(false);

            for (final BaseAction baseActionInterface : actionInterfaces) {
                if (!(baseActionInterface instanceof ActionOnSeed))
                    continue;
                FloatingItem floatingItem = new FloatingItem();
                floatingItem.setTitle(baseActionInterface.getName());
                int actionImageRessource = getResources().getIdentifier(
                        "org.gots:drawable/action_" + baseActionInterface.getName(), null, null);
                floatingItem.setRessourceId(actionImageRessource);
                floatingItem.setOnLongClickListener(new View.OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                baseActionInterface.setDuration(7);
                                if (mSeed instanceof GrowingSeed)
                                    actionseedProvider.insertAction(((GrowingSeed) mSeed), (ActionOnSeed) baseActionInterface);
                                return null;
                            }

                            protected void onPostExecute(Void result) {
//                                if (fragmentListAction != null) {
//                                    ((ActionsDoneListFragment) fragmentListAction).update();
//                                }
                            }

                            ;
                        }.execute();
                        return true;
                    }
                });
                floatingItem.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... params) {
                                if (mSeed instanceof GrowingSeed)
                                    actionseedProvider.doAction((ActionOnSeed) baseActionInterface, ((GrowingSeed) mSeed));
                                return null;
                            }

                            protected void onPostExecute(Void result) {
//                                if (fragmentListAction != null) {
//                                    ((ActionsDoneListFragment) fragmentListAction).update();
//                                }
                            }

                            ;
                        }.execute();
                    }
                });
                floatingItems.add(floatingItem);
            }
        }
        return floatingItems;
    }

    @Override
    public void onAllotmentClick(BaseAllotmentInterface allotmentInterface) {
        SowingAction action = new SowingAction(getApplicationContext());
        action.execute(allotmentInterface, (GrowingSeed) mSeed);
        getSupportFragmentManager().popBackStack();
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
}
