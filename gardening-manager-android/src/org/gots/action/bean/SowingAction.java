/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.action.bean;

import android.content.Context;

import org.gots.action.AbstractGrowingAction;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.PermanentActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeed;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedImpl;

import java.util.Iterator;

public class SowingAction extends AbstractGrowingAction implements PermanentActionInterface {
    Context mContext;

    public SowingAction(Context context) {
        super(context);
        setName("sow");
        mContext = context;
    }

    @Override
    public void execute(BaseAllotmentInterface allotment, BaseSeed seed) {
        GrowingSeed growingSeed = new GrowingSeedImpl();
        growingSeed.setPlant(seed);
        growingSeed = growingSeedManager.plantingSeed(growingSeed, allotment);

        for (Iterator<BaseAction> iterator = growingSeed.getPlant().getActionToDo().iterator(); iterator.hasNext(); ) {

            BaseAction type1 = iterator.next();
            if (type1 != null) {
                BaseAction action = actionManager.getActionByName(type1.getName());
                actionSeedManager.insertAction(growingSeed, (ActionOnSeed) action);
            }
        }

    }


}
