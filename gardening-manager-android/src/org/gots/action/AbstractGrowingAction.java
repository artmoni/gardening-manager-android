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

import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GrowingSeed;

import android.content.Context;
import android.content.Intent;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractGrowingAction extends AbstractAction implements GrowingActionInterface {

    public AbstractGrowingAction(Context context) {
        super(context);
    }

    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeed seed) {
        setDateActionDone(Calendar.getInstance().getTime());
        mContext.sendBroadcast(new Intent(BroadCastMessages.ACTION_EVENT));
        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        tracker.trackEvent("Seed", getName(), seed.getPlant().getSpecie(), 0);
        return 1;
    }

}
