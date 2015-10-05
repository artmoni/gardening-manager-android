package org.gots.ui;

import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.integration.android.IntentIntegrator;

import org.gots.R;
import org.gots.provider.SeedsContentProvider;
import org.gots.seed.BaseSeed;
import org.gots.ui.fragment.CatalogueFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 20/09/15.
 */
public class CatalogueActivity extends BaseGotsActivity implements CatalogueFragment.OnSeedSelected {


    private CatalogueFragment contentFragment;

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
        if (contentFragment == null) {
            contentFragment = new CatalogueFragment();
            addMainLayout(contentFragment, null);
        }
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
        startSupportActionMode(new PlantCallBack(this, seed, new PlantCallBack.OnPlantCallBackClicked() {
            @Override
            public void onPlantCallBackClicked() {
                if (contentFragment!=null)
                    contentFragment.update();
            }
        }));
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
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_catalogue, menu);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
