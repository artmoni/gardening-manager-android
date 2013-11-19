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

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;
import android.content.Intent;
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
        growingSeedManager.deleteGrowingSeed(seed);
        return 1;

    }

    public void setDateActionDone(Date dateActionDone) {
        super.setDateActionDone(dateActionDone);
    }

    public Date getDateActionDone() {
        return super.getDateActionDone();
    }

    public void setDuration(int duration) {
        super.setDuration(duration);
    }

    public int getDuration() {
        return super.getDuration();
    }

    public void setDescription(String description) {
        super.setDescription(description);
    }

    public String getDescription() {
        return super.getDescription();
    }

    public void setName(String name) {
        super.setName(name);
    }

    public String getName() {
        return super.getName();
    }

    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeedInterface seed) {
        new AsyncTask<BaseAllotmentInterface, Integer, Void>() {
            @Override
            protected Void doInBackground(BaseAllotmentInterface... params) {
                List<GrowingSeedInterface> listseeds = growingSeedManager.getGrowingSeedsByAllotment(params[0]);
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

    public void setData(Object data) {
    }

    public Object getData() {
        return null;
    }

}
