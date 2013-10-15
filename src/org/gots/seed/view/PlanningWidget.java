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
package org.gots.seed.view;

import java.util.ArrayList;

import org.gots.seed.adapter.PlanningSowAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;

public class PlanningWidget extends GridView {

    private Context mContext;


//    private Boolean isSowingPeriod = false;
//
//    private Boolean isHarvestPeriod = false;
//
//    private boolean isEditable;

    // private PlanningSowAdapter planningSowAdapter;

    // private LinearLayout child;

    public PlanningWidget(Context context) {

        super(context);
        this.mContext = context;
        initView();
    }

    public PlanningWidget(Context context, AttributeSet attr) {

        super(context, attr);
        this.mContext = context;
        PlanningSowAdapter planningSowAdapter = new PlanningSowAdapter(null);
        setAdapter(planningSowAdapter);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // inflater.inflate(R.layout.planning_widget, this, false);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (getAdapter() != null)
//            setNumColumns(getAdapter().getCount());

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

//
//    public void setSowingPeriode(boolean isSowingPeriod) {
//        this.isSowingPeriod = isSowingPeriod;
//    }
//
//    public void setHarvestPeriode(boolean isHarvestPeriod) {
//        this.isHarvestPeriod = isHarvestPeriod;
//    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
//        if (isEditable)
//            for (int i = 0; i < getChildCount(); i++) {
//                if (MonthWidget.class.isInstance(getChildAt(i))) {
//                    MonthWidget monthWidget = (MonthWidget) getChildAt(i);
//                    monthWidget.setEditable(isEditable);
//                }
//            }
        
        // MonthWidget monthWidget = new MonthWidget(getContext());
        // monthWidget.setMonthText("T");
        // TextView sowingIndicator = (TextView)
        // findViewById(R.id.idSowingPeriod);
        // // TextView harvestIndicator = (TextView)
        // // findViewById(R.id.idHarvestPeriod);
        //
        // sowingIndicator.setText(monthText);
        // // harvestIndicator.setText(monthText);
        //
        // if (isSowingPeriod) {
        // sowingIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_planning_sow));
        // }

        // if (isHarvestPeriod) {
        // harvestIndicator.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_planning_harvest));
        // }

    }

    public ArrayList<Integer> getSelectedMonth() {
        ArrayList<Integer> selectedMonth = new ArrayList<Integer>();
        for (int i = 0; i < getChildCount(); i++) {
            if (getChildAt(i).isSelected()) {
                selectedMonth.add(i);
            }
        }
        return selectedMonth;
    }

//  public void setEditable(boolean editable) {
//  this.isEditable = editable;
//  invalidate();
//
//}
//    public void setEditable(boolean editable) {
//        this.isEditable = editable;
//        invalidate();
//
//    }

}
