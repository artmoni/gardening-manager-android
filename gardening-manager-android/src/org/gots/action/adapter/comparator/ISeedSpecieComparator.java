package org.gots.action.adapter.comparator;

import java.util.Comparator;

import org.gots.seed.BaseSeed;
import org.gots.seed.SeedUtil;

import android.content.Context;

public class ISeedSpecieComparator implements Comparator<BaseSeed> {

    private Context mcontext;

    public ISeedSpecieComparator(Context context) {
        this.mcontext = context;
    }

    @Override
    public int compare(BaseSeed obj1, BaseSeed obj2) {
        int result = 0;
        if (obj1 != null && obj2 != null && obj1.getSpecie() != null && obj2.getSpecie() != null) {
            result = SeedUtil.translateSpecie(mcontext, obj1).compareTo(SeedUtil.translateSpecie(mcontext, obj2));
        }
        return result;
    }
}