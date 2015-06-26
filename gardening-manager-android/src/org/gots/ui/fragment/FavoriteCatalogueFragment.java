package org.gots.ui.fragment;

import java.util.List;

import org.gots.seed.BaseSeedInterface;

public class FavoriteCatalogueFragment extends CatalogueFragment {
    @Override
    protected List<BaseSeedInterface> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        return seedProvider.getMyFavorites();
    }
}
