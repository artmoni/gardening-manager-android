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

import org.gots.action.AbstractActionSeed;
import org.gots.action.SeedActionInterface;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;

public class CuttingAction extends AbstractActionSeed implements SeedActionInterface {

    public CuttingAction(Context context) {
        super(context);
        setName("cut");
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        super.execute(seed);

        setDateActionDone(Calendar.getInstance().getTime());
        seed.getActionToDo().remove(this);
        seed.getActionDone().add(this);
        LocalActionSeedProvider asdh = new LocalActionSeedProvider(getContext());
        asdh.doAction(this, seed);
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

    public void setData(Object data) {
    }

    public Object getData() {
        return null;
    }
}
