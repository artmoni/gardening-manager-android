package org.gots.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.AttributeSet;
import android.view.View;

import org.gots.seed.BaseSeed;
import org.gots.ui.fragment.CatalogueFragment;

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
}
