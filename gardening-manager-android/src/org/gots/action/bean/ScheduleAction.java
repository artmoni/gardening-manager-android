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

import org.gots.action.AbstractActionSeed;
import org.gots.action.ActionOnSeed;
import org.gots.seed.BaseSeed;

import android.content.Context;

public class ScheduleAction extends AbstractActionSeed implements ActionOnSeed {

    public ScheduleAction(Context mContext) {
        super(mContext);

        setName("schedule");
    }

}
