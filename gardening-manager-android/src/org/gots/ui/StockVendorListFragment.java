package org.gots.ui;

import java.util.ArrayList;
import java.util.List;

import org.gots.exception.GardenNotFoundException;
import org.gots.seed.BaseSeedInterface;

import android.util.Log;

public class StockVendorListFragment extends CatalogueFragment {

    @Override
    protected List<BaseSeedInterface> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        List<BaseSeedInterface> myStock = new ArrayList<>();
        try {
            myStock = seedProvider.getMyStock(gardenManager.getCurrentGarden(), force);
        } catch (GardenNotFoundException e) {
            Log.w(TAG, e.getMessage());
        }
        return myStock;
    }

}
