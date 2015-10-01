package org.gots.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import org.gots.R;
import org.gots.provider.SeedsContentProvider;
import org.gots.seed.BaseSeed;
import org.gots.seed.SeedUtil;
import org.gots.ui.fragment.CatalogueFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 20/09/15.
 */
public class CatalogueActivity extends BaseGotsActivity implements CatalogueFragment.OnSeedSelected {


    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected String requireRefreshSyncAuthority() {
        return SeedsContentProvider.AUTHORITY;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        addMainLayout(new CatalogueFragment(), null);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onPlantCatalogueClick(BaseSeed seed) {
        Intent i = new Intent(getApplicationContext(), PlantDescriptionActivity.class);
        i.putExtra(PlantDescriptionActivity.GOTS_VENDORSEED_ID, seed.getSeedId());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    @Override
    public void onPlantCatalogueLongClick(CatalogueFragment vendorListFragment, BaseSeed seed) {
        startSupportActionMode(new MyCallBack(seed, vendorListFragment));
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        FloatingItem floatingItem = new FloatingItem();
        floatingItem.setTitle(getResources().getString(R.string.seed_action_add_catalogue));
        floatingItem.setRessourceId(R.drawable.bt_add_seed);
        floatingItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), NewSeedActivity.class);
                startActivity(i);
            }
        });
        floatingItems.add(floatingItem);

        FloatingItem recognitionItem = new FloatingItem();
        recognitionItem.setTitle(getResources().getString(R.string.plant_recognition));
        recognitionItem.setRessourceId(R.drawable.ic_menu_recognition);
        recognitionItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecognitionActivity.class);
                startActivity(i);
            }
        });
        floatingItems.add(recognitionItem);
        return floatingItems;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_catalogue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        Intent i;
        switch (item.getItemId()) {
            case R.id.new_seed_barcode:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPlantFiltered(String filterTitle) {
        showNotification(filterTitle, true);
    }

     /*
     * CallBACK on long press
     */

    private final class MyCallBack implements ActionMode.Callback {

        private final CatalogueFragment currentFragment;
        private BaseSeed currentSeed;

        private MyCallBack(BaseSeed seedInterface, CatalogueFragment catalogueFragment) {
            currentSeed = seedInterface;
            currentFragment = catalogueFragment;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            if (currentSeed.getNbSachet() == 0)
                menu.findItem(R.id.action_stock_reduce).setVisible(false);

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_hut_contextual, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {

                case R.id.action_stock_add:
                    new AsyncTask<Void, Integer, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            seedManager.addToStock(currentSeed, getCurrentGarden());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            showNotification(SeedUtil.translateSpecie(getApplicationContext(), currentSeed) + " +1 " + getResources().getString(R.string.seed_action_stock_description), false);
                            if (currentFragment != null) currentFragment.update();

                            super.onPostExecute(result);
                        }
                    }.execute();
                    break;

                case R.id.action_stock_reduce:
                    new AsyncTask<Void, Integer, Void>() {

                        @Override
                        protected Void doInBackground(Void... params) {
                            seedManager.removeToStock(currentSeed, getCurrentGarden());
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            showNotification(SeedUtil.translateSpecie(getApplicationContext(), currentSeed) + " -1 " + getResources().getString(R.string.seed_action_stock_description), false);
                            if (currentFragment != null) currentFragment.update();

                            super.onPostExecute(result);
                        }
                    }.execute();
                    break;
                default:
                    break;
            }


            mode.finish();
            return true;
        }
    }
}
