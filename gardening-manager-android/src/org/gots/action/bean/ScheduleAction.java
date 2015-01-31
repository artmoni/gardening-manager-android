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
package org.gots.action.bean;

import java.util.Calendar;
import java.util.Date;

import org.gots.action.AbstractActionSeed;
import org.gots.action.ActionOnSeed;
import org.gots.seed.GrowingSeed;

import android.content.Context;

public class ScheduleAction extends AbstractActionSeed implements ActionOnSeed {

    public ScheduleAction(Context mContext) {
        super(mContext);

        setName("schedule");
    }

    @Override
    public int execute(GrowingSeed seed) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(seed.getDateSowing());
        cal.add(Calendar.DAY_OF_YEAR, getDuration());
        setDateActionTodo(cal.getTime());
        return super.execute(seed);
    }

}
