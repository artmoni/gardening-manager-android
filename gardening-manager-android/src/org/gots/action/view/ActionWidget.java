/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.action.view;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.action.util.ActionState;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActionWidget extends LinearLayout {
    private BaseAction mAction;

    private Context mContext;

    // private OnActionItemClickListener mItemClickListener;

    private int state = ActionState.NORMAL;

    private static final int[] STATE_OK = { R.attr.state_ok };

    private static final int[] STATE_WARNING = { R.attr.state_warning };

    private static final int[] STATE_CRITICAL = { R.attr.state_critical };

    private static final int[] STATE_UNDEFINED = { R.attr.state_undefined };

    private ImageView actionImage;

    public ActionWidget(Context context, AttributeSet set, int defstyle) {
        super(context, set, defstyle);
        mContext = context;
        initView();
    }

    public ActionWidget(Context context, BaseAction action) {
        super(context);
        mContext = context;
        initView();
        setAction(action);
    }

    public ActionWidget(Context context, AttributeSet set) {
        super(context, set);
        mContext = context;
        initView();
    }

    public ActionWidget(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 4);
        if (isOk()) {
            mergeDrawableStates(drawableState, STATE_OK);
        } else if (isWarning()) {
            mergeDrawableStates(drawableState, STATE_WARNING);
        } else if (isCritical()) {
            mergeDrawableStates(drawableState, STATE_CRITICAL);
        } else if (isUndefined())
            mergeDrawableStates(drawableState, STATE_UNDEFINED);
        return drawableState;
    }

    private boolean isOk() {
        return state == ActionState.NORMAL;
    }

    private boolean isUndefined() {
        return state == ActionState.UNDEFINED;
    }

    private boolean isWarning() {
        return state == ActionState.WARNING;

    }

    private boolean isCritical() {
        return state == ActionState.CRITICAL;
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.action_widget, this);
        actionImage = (ImageView) findViewById(R.id.idSeedActionImage);

    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (mAction == null)
            return;

        int actionImageRessource = mContext.getResources().getIdentifier(
                "org.gots:drawable/action_" + mAction.getName(), null, null);

        if (actionImageRessource != 0) {
            setActionImage(actionImageRessource);
        }
        // int sdk = android.os.Build.VERSION.SDK_INT;
        // if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
        // setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));
        // } else {
        // setBackground(mContext.getResources().getDrawable(R.drawable.action_selector));
        // }
        // invalidate();
    }

    public void setAction(BaseAction action) {
        this.mAction = action;
    }

    public void setState(int state) {
        this.state = state;
        refreshDrawableState();
    }

    public void setActionImage(int res) {
        final Drawable drawable = getResources().getDrawable(res);
        if (drawable != null)
            actionImage.setImageDrawable(drawable);
    }
}
