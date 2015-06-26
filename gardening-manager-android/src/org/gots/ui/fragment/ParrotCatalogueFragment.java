package org.gots.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.parrot.ParrotSeedProvider;

public class ParrotCatalogueFragment extends CatalogueFragment {

    @Override
    protected List<BaseSeedInterface> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        ParrotSeedProvider parrotProvider = new ParrotSeedProvider(mContext);
        List<BaseSeedInterface> catalogue = new ArrayList<>();
        if (filterValue == null)
            catalogue.addAll(parrotProvider.getVendorSeeds(true, page, pageSize));
        else
            catalogue = parrotProvider.getVendorSeedsByName(filterValue.toString(), false);
        return catalogue;
    }

}
