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

import java.util.Iterator;
import java.util.List;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;
import android.os.AsyncTask;

public class DeleteAction extends AbstractActionSeed implements PermanentActionInterface, SeedActionInterface,
        GardeningActionInterface {

    public DeleteAction(Context context) {
        super(context);
        setName("delete");
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        super.execute(seed);

        // GrowingSeedDBHelper helper = new GrowingSeedDBHelper(getContext());
        // helper.deleteGrowingSeed(seed);
        // seedManager.removeGrowingSeed(seed);
        new AsyncTask<GrowingSeedInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(GrowingSeedInterface... params) {
                growingSeedManager.deleteGrowingSeed(params[0]);
                return null;
            }

        }.execute(seed);
        return 1;

    }

    
    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeedInterface seed) {
        super.execute(seed);
        new AsyncTask<BaseAllotmentInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(BaseAllotmentInterface... params) {
                List<GrowingSeedInterface> listseeds = growingSeedManager.getGrowingSeedsByAllotment(params[0], false);
                for (Iterator<GrowingSeedInterface> iterator = listseeds.iterator(); iterator.hasNext();) {
                    GrowingSeedInterface baseSeedInterface = iterator.next();
                    DeleteAction.this.execute(baseSeedInterface);
                }

                allotmentProvider.removeAllotment(params[0]);
                return null;
            }

            protected void onPostExecute(Void result) {
                // mContext.sendBroadcast(new Intent(BroadCastMessages.GROWINGSEED_DISPLAYLIST));
            };
        }.execute(allotment);

        return 0;
    }

    @Override
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public int getId() {
        return super.getId();
    }


    @Override
    public Object getData() {
        return super.getData();
    }

    @Override
    public void setData(Object data) {
        super.setData(data);
    }

}
