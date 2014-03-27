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
package org.gots.seed.adapter;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.view.MonthWidget;

import android.view.View;
import android.view.ViewGroup;

public class PlanningHarvestAdapter extends PlanningAdapter {
    BaseSeedInterface mSeed;

    public PlanningHarvestAdapter(BaseSeedInterface seed) {
        mSeed = seed;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonthWidget monthWidget = new MonthWidget(parent.getContext());
        monthWidget.setMonthText(getItem(position));
        // monthWidget.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.selector_planning_harvest));
        if (mSeed != null) {

            int harvestMin = (mSeed.getDateSowingMin() - 1 + mSeed.getDurationMin() / 30) % 12;
            int harvestMax = (mSeed.getDateSowingMax() - 1 + mSeed.getDurationMax() / 30) % 12;

            // [0][1][2][3][min][5][6][position][max][9][10][11]
            if ((position >= harvestMin && position <= harvestMax)
            // [0][position][max][3][4][5][6][7][min][9][10][11]
                    || ((position <= harvestMax) && (harvestMax < harvestMin))
                    // [0][1][max][3][4][5][6][7][min][9][position][11]
                    || (position >= harvestMin) && (harvestMax < harvestMin))

                monthWidget.setHarvestPeriode(true);
        }
        return monthWidget;
    }
}
