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
import java.util.Iterator;

import org.gots.action.AbstractActionGarden;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GardeningActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;

import android.content.Context;

public class SowingAction extends AbstractActionGarden implements PermanentActionInterface, GardeningActionInterface {
    Context mContext;

    public SowingAction(Context context) {
        super(context);
        setName("sow");
        mContext = context;
    }


    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeed seed) {
        super.execute(allotment, seed);

        seed.setDateSowing(Calendar.getInstance().getTime());
        seed.setUUID(null);
        seed = growingSeedManager.plantingSeed(seed, allotment);

//        actionSeedManager.insertAction(seed, this);
        // asdh.doAction(this, seed);

        for (Iterator<BaseAction> iterator = seed.getActionToDo().iterator(); iterator.hasNext();) {

            BaseAction type1 = iterator.next();
            if (type1 != null) {
                BaseAction action = actionManager.getActionByName(type1.getName());
                actionSeedManager.insertAction(seed, (ActionOnSeed)action);
            }
        }
        
        // tracker.dispatch();
        return 0;
    }


}
