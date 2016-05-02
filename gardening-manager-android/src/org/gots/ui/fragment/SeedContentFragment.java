package org.gots.ui.fragment;

import android.app.Activity;

import org.gots.seed.BaseSeed;

/**
 * Created by sfleury on 10/07/15.
 */
public abstract class SeedContentFragment extends BaseGotsFragment {
    protected BaseSeed mSeed;
    private OnSeedUpdated mCallBack;

    @Override
    public void onAttach(Activity activity) {
        if (getActivity() instanceof OnSeedUpdated)
            mCallBack = (OnSeedUpdated) getActivity();
        super.onAttach(activity);
    }

    public void setSeed(BaseSeed seed) {
        this.mSeed = seed;
    }

    protected void notifyObservers() {
        if (mCallBack != null)
            mCallBack.onSeedUpdated(mSeed);
    }

    public interface OnSeedUpdated {
        void onSeedUpdated(BaseSeed seed);
    }
}
