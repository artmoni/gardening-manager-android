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

import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;
import android.content.Intent;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractActionSeed extends AbstractAction implements SeedActionInterface {

    private int growingSeedId;

    private int actionSeedId;

    public AbstractActionSeed(Context context) {
        super(context);
    }

    public AbstractActionSeed(String name) {
        this.name = name;
    }

    @Override
    public int getGrowingSeedId() {
        return growingSeedId;
    }

    @Override
    public void setGrowingSeedId(int id) {
        growingSeedId = id;
    }

    @Override
    public int getActionSeedId() {
        return actionSeedId;
    }

    @Override
    public void setActionSeedId(int id) {
        actionSeedId = id;
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        setDateActionDone(Calendar.getInstance().getTime());

        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        tracker.trackEvent("Seed", getName(), seed.getSpecie(), 0);
        // tracker.dispatch();

        mContext.sendBroadcast(new Intent(BroadCastMessages.ACTION_EVENT));
        return 1;
    }
}
