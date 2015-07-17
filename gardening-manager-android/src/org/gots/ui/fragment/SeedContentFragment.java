package org.gots.ui.fragment;

import android.app.Activity;

import org.gots.analytics.GotsAnalytics;
import org.gots.seed.BaseSeedInterface;

/**
 * Created by sfleury on 10/07/15.
 */
public abstract class SeedContentFragment extends BaseGotsFragment {
    protected BaseSeedInterface mSeed;
    private OnSeedUpdated mCallBack;

    @Override
    public void onAttach(Activity activity) {
        if (getActivity() instanceof OnSeedUpdated)
            mCallBack = (OnSeedUpdated) getActivity();
        super.onAttach(activity);
    }

    public interface OnSeedUpdated {
        void onSeedUpdated(BaseSeedInterface seed);
    }

    public void setSeed(BaseSeedInterface seed) {
        this.mSeed = seed;
    }

    protected void notifyObservers() {
        if (mCallBack != null)
            mCallBack.onSeedUpdated(mSeed);
    }
}
