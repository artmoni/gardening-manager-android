package org.gots.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.View;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.ui.fragment.CatalogueFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 20/09/15.
 */
public class CatalogueActivity extends BaseGotsActivity implements CatalogueFragment.OnSeedSelected{
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
//        startSupportActionMode(new MyCallBack(seed));
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
}
