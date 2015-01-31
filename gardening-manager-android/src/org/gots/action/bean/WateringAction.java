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
import java.util.Iterator;
import java.util.List;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.ActionOnSeed;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;

import android.content.Context;
import android.os.AsyncTask;

public class WateringAction extends AbstractActionSeed implements ActionOnSeed, GardeningActionInterface {

    public WateringAction(Context context) {
        super(context);
        setName("water");
    }

    @Override
    public int execute(GrowingSeed seed) {
        seed.setDateLastWatering(Calendar.getInstance().getTime());
        
        return super.execute(seed);
    }


    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeed seed) {
        new AsyncTask<BaseAllotmentInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(BaseAllotmentInterface... params) {
                List<GrowingSeed> listseeds = growingSeedManager.getGrowingSeedsByAllotment(params[0], false);
                for (Iterator<GrowingSeed> iterator = listseeds.iterator(); iterator.hasNext();) {
                    GrowingSeed baseSeedInterface = iterator.next();
                    WateringAction.this.execute(baseSeedInterface);

                }

                return null;
            }

            protected void onPostExecute(Void result) {
                // mContext.sendBroadcast(new Intent(BroadCastMessages.GROWINGSEED_DISPLAYLIST));
            };
        }.execute(allotment);

        return 0;
    }

}
