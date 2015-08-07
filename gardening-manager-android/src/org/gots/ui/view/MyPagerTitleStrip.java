package org.gots.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

/**
 * Created by sfleury on 07/08/15.
 */
public class MyPagerTitleStrip extends android.support.v4.view.PagerTitleStrip {

    public MyPagerTitleStrip(Context context) {
        super(context);


    }

    public MyPagerTitleStrip(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "alpha_echo.ttf");
            for (int i = 0; i < this.getChildCount(); i++) {
                if (this.getChildAt(i) instanceof TextView) {
                    ((TextView) this.getChildAt(i)).setTypeface(tf);
                }
            }

        }
    }
}
