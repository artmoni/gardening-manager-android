package org.gots.layout;

/*
 * Copyright 2013 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gots.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class ProgressViewActionBar extends RelativeLayout implements AnimationListener {

    private final Animation mCycleFadeAnimation;
    public ProgressViewActionBar(Context context) {
        super(context);
        mCycleFadeAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        mCycleFadeAnimation.setAnimationListener(this);
        inflate(context, R.layout.actionbar_indeterminate_progress, null);
    }
    public ProgressViewActionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mCycleFadeAnimation = AnimationUtils.loadAnimation(context, R.anim.rotate);
        mCycleFadeAnimation.setAnimationListener(this);
        
    }

    public void animateBackground() {
        View animationBackground = getAnimationBackgroundView();
        if (null != animationBackground && animationBackground.getVisibility() != View.VISIBLE) {
            animationBackground.startAnimation(mCycleFadeAnimation);
            animationBackground.setVisibility(View.VISIBLE);
        }
    }

    public void stopAnimatingBackground() {
        View animationBackground = getAnimationBackgroundView();
        if (null != animationBackground && animationBackground.getVisibility() == View.VISIBLE) {
            animationBackground.setVisibility(View.GONE);
            animationBackground.clearAnimation();
        }
    }

    private View getAnimationBackgroundView() {
        return findViewById(R.id.imageViewRefresh);
    }

    public void onAnimationEnd(Animation animation) {
        View animationBackground = getAnimationBackgroundView();
        if (null != animationBackground && animationBackground.getVisibility() == View.VISIBLE) {
            animationBackground.startAnimation(animation);
        }
    }

    public void onAnimationRepeat(Animation animation) {
        // NO-OP
    }

    public void onAnimationStart(Animation animation) {
        // NO-OP
    }

}