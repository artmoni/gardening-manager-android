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

import org.gots.action.AbstractActionSeed;
import org.gots.action.SeedActionInterface;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;

public class BeakeringAction extends AbstractActionSeed implements SeedActionInterface {

    public BeakeringAction(Context mContext) {
        super(mContext);
        setName("beak");
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        super.execute(seed);
        seed.getActionToDo().remove(this);
        seed.getActionDone().add(this);
        actionSeedManager.doAction(this, seed);
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
    public Object getData() {
        return super.getData();
    }

    @Override
    public void setData(Object data) {
        super.setData(data);
    }

}
