package org.gots.ui;

import java.util.Calendar;
import java.util.List;

import org.gots.seed.BaseSeedInterface;

public class MonthlySeedListFragment extends CatalogueFragment {

    @Override
    protected List<BaseSeedInterface> onRetrieveNuxeoData(String filterValue, int page, int pageSize, boolean force) {
        return seedProvider.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

}
