package org.gots.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.gots.seed.BaseSeed;

public class VendorCatalogueFragment extends CatalogueFragment {

    @Override
    protected List<BaseSeed> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        List<BaseSeed> catalogue = new ArrayList<>();
        if (filterValue != null)
            catalogue = seedProvider.getVendorSeedsByName(filterValue, false);
        else {
            catalogue = seedProvider.getVendorSeeds(force, page, pageSize);
            if (catalogue.size() == 0)
                catalogue = seedProvider.getVendorSeeds(true, page, pageSize);
        }
        return catalogue;
    }

}
