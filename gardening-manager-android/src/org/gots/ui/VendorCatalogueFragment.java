package org.gots.ui;

import java.util.ArrayList;
import java.util.List;

import org.gots.seed.BaseSeedInterface;

public class VendorCatalogueFragment extends CatalogueFragment {

    @Override
    protected List<BaseSeedInterface> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        List<BaseSeedInterface> catalogue = new ArrayList<>();
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
