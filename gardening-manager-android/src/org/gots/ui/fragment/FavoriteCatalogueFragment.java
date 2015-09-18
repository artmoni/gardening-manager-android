package org.gots.ui.fragment;

import java.util.List;

import org.gots.seed.BaseSeed;

public class FavoriteCatalogueFragment extends CatalogueFragment {
    @Override
    protected List<BaseSeed> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        return seedProvider.getMyFavorites();
    }
}
