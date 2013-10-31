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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;

import android.content.Context;

public class WateringAction extends AbstractActionSeed implements SeedActionInterface, GardeningActionInterface {

    public WateringAction(Context context) {
        super(context);
        setName("water");
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        super.execute(seed);

        seed.setDateLastWatering(Calendar.getInstance().getTime());

        seed.getActionToDo().remove(this);
        seed.getActionDone().add(this);

        actionSeedManager.doAction(this, seed);

        return 0;
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

        LocalGrowingSeedProvider helper = new LocalGrowingSeedProvider(getContext());
        ArrayList<GrowingSeedInterface> listseeds = helper.getSeedsByAllotment(allotment.getName());
        for (Iterator<GrowingSeedInterface> iterator = listseeds.iterator(); iterator.hasNext();) {
            GrowingSeedInterface baseSeedInterface = iterator.next();
            execute(baseSeedInterface);

        }

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
