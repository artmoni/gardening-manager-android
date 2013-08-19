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
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;

import android.content.Context;

public class ReduceQuantityAction extends AbstractActionSeed implements SeedActionInterface {

    public ReduceQuantityAction(Context mContext) {
        super(mContext);

        setName("reducequantity");
    }

    @Override
    public int execute(GrowingSeedInterface seed) {

        if (seed.getNbSachet() <= 0)
            return 0;
        super.execute(seed);
        seed.setNbSachet(seed.getNbSachet() - 1);
        VendorSeedDBHelper helper = VendorSeedDBHelper.getInstance(getContext());
        helper.updateSeed(seed);
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

    public void setData(Object data) {
    }

    public Object getData() {
        return null;
    }

}
