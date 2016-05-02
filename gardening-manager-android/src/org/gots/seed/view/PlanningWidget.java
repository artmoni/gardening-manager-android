/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.seed.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import org.gots.seed.adapter.PlanningSowAdapter;

import java.util.ArrayList;

public class PlanningWidget extends GridView {

    public PlanningWidget(Context context) {

        super(context);
    }

    public PlanningWidget(Context context, AttributeSet attr) {

        super(context, attr);
        PlanningSowAdapter planningSowAdapter = new PlanningSowAdapter(null);
        setAdapter(planningSowAdapter);
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
}
