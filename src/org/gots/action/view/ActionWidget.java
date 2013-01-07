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
import org.gots.action.BaseActionInterface;
import org.gots.action.util.ActionState;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ActionWidget extends LinearLayout {
	private BaseActionInterface mAction;
	private Context mContext;
	// private OnActionItemClickListener mItemClickListener;

	private static final int[] STATE_OK = { R.attr.state_ok };
	private static final int[] STATE_WARNING = { R.attr.state_warning };
	private static final int[] STATE_CRITICAL = { R.attr.state_critical };
	private static final int[] STATE_UNDEFINED = { R.attr.state_undefined };

	public ActionWidget(Context context, BaseActionInterface action) {
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

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 4);
		if (isOk()) {
			mergeDrawableStates(drawableState, STATE_OK);
		} else if (isWarning()) {
			mergeDrawableStates(drawableState, STATE_WARNING);
		} else if (isCritical()) {
			mergeDrawableStates(drawableState, STATE_CRITICAL);
		} else
			mergeDrawableStates(drawableState, STATE_UNDEFINED);
		return drawableState;
	}

	private boolean isOk() {
		return mAction == null ? false : mAction.getState() == ActionState.NORMAL;
	}

	private boolean isWarning() {
		return mAction == null ? false : mAction.getState() == ActionState.WARNING;

	}

	private boolean isCritical() {
		return mAction == null ? false : mAction.getState() == ActionState.CRITICAL;
	}

	private void initView() {
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.action_widget, this);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);

		if (mAction == null)
			return;

		ImageView actionImage = (ImageView) findViewById(R.id.idSeedActionImage);

		int actionImageRessource = mContext.getResources().getIdentifier(
				"org.gots:drawable/action_" + mAction.getName(), null, null);

		if (actionImageRessource != 0) {
			Drawable drawable = mContext.getResources().getDrawable(actionImageRessource);
			actionImage.setImageDrawable(drawable);
		}
		setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector)); 	
		invalidate();
	}

	public void setAction(BaseActionInterface action) {
		this.mAction = action;

		refreshDrawableState();
		requestLayout();
		invalidate();
	}

	// private boolean match(int pixel) {
	// // There may be a better way to match, but I wanted to do a comparison
	// // ignoring
	// // transparency, so I couldn't just do a direct integer compare.
	// return pixel == Color.BLACK;
	// }

	// /**
	// * Listener for item click
	// *
	// */
	// public interface OnActionItemClickListener {
	// public abstract void onItemClick(ActionWidget source, BaseActionInterface
	// baseActionInterface);
	// }

	// /**
	// * Set listener for action item clicked.
	// *
	// * @param listener
	// * Listener
	// */
	// public void setOnActionItemClickListener(OnActionItemClickListener
	// listener) {
	// mItemClickListener = listener;
	// }

}
