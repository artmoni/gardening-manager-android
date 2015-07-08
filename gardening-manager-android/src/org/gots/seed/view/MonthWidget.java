/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.seed.view;

import org.gots.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MonthWidget extends LinearLayout {

    private Context mContext;
    private String monthText = "J";
    private Boolean isSowingPeriod = false;
    private Boolean isHarvestPeriod = false;
//	private boolean isEditable = false;

    // private LinearLayout child;

    public MonthWidget(Context context) {

        super(context);
        this.mContext = context;
        initView();
    }

    public MonthWidget(Context context, AttributeSet attr) {

        super(context, attr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.month_widget, this);
    }

    public void setMonthText(String month) {
        monthText = month;
    }

    public void setSowingPeriode(boolean isSowingPeriod) {
        this.isSowingPeriod = isSowingPeriod;
    }

    public void setHarvestPeriode(boolean isHarvestPeriod) {
        this.isHarvestPeriod = isHarvestPeriod;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        TextView sowingIndicator = (TextView) findViewById(R.id.idSowingPeriod);

        sowingIndicator.setText(monthText);

        if (isSowingPeriod) {
            sowingIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.selector_planning));
            setSelected(true);
        }

        if (isHarvestPeriod) {
            sowingIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(
                    R.drawable.selector_planning_harvest));
            setSelected(true);
        }

    }
}
