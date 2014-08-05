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
package org.gots.action;

import java.util.Calendar;
import java.util.Date;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;
import android.content.Intent;

public abstract class AbstractActionGarden extends AbstractAction implements GardeningActionInterface {

    public AbstractActionGarden(Context context) {
        super(context);
    }

    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeedInterface seed) {
        setDateActionDone(Calendar.getInstance().getTime());
        mContext.sendBroadcast(new Intent(BroadCastMessages.ACTION_EVENT));

        return 1;
    }

}
