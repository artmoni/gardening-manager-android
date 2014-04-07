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

public class PlanningSowAdapter extends PlanningAdapter {
    BaseSeedInterface mSeed;

    public PlanningSowAdapter(BaseSeedInterface seed) {
        mSeed = seed;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonthWidget monthWidget = new MonthWidget(parent.getContext());
        monthWidget.setMonthText(getItem(position));
        // monthWidget.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.selector_planning));

        if (mSeed != null) {

            int sowingMin = mSeed.getDateSowingMin() - 1;
            int sowingMax = mSeed.getDateSowingMax() - 1;

            if (position >= sowingMin && position <= sowingMax || (position <= sowingMax && sowingMax < sowingMin)
                    || (position >= sowingMin && sowingMax < sowingMin))
                monthWidget.setSowingPeriode(true);

        }
        return monthWidget;
    }
}
