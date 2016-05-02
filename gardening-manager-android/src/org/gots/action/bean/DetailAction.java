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

import org.gots.action.AbstractActionSeed;
import org.gots.action.ActionOnSeed;
import org.gots.action.GrowingActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeed;
import org.gots.seed.GrowingSeed;

public class DetailAction extends AbstractActionSeed implements PermanentActionInterface, ActionOnSeed, GrowingActionInterface {

    public DetailAction(Context context) {
        super(context);
        setName("detail");
    }

    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeed seed) {
        // TODO Auto-generated method stub
        return 0;
    }


    @Override
    public void execute(BaseAllotmentInterface allotment, BaseSeed seed) {

    }
}
