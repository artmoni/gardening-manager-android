package org.gots.ui.fragment;

import org.gots.seed.BaseSeedInterface;

/**
 * Created by sfleury on 10/07/15.
 */
public abstract class SeedContentFragment extends BaseGotsFragment {
    protected BaseSeedInterface mSeed;

    public void setSeed(BaseSeedInterface seed){
        this.mSeed=seed;
    }
}
