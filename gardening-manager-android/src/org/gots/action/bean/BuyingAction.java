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
import org.gots.action.PermanentActionInterface;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;

public class BuyingAction extends AbstractActionSeed implements PermanentActionInterface {

    public BuyingAction(Context context) {
        super(context);
        setName("buy");

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
    public void setId(int id) {
        super.setId(id);
    }

    @Override
    public int getId() {

        return super.getId();
    }

    @Override
    public int getState() {
        return super.getState();
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        // super.execute(seed);
        seedManager.addToStock(seed, gardenManager.getCurrentGarden());
        // if (GotsPreferences.getInstance().isConnectedToServer()) {
        // GotsSeedProvider provider = new NuxeoSeedProvider(mContext);
        // provider.addToStock(seed, GardenManager.getInstance().getCurrentGarden());
        // } else {
        // seed.setNbSachet(seed.getNbSachet() + 1);
        // VendorSeedDBHelper helper = new VendorSeedDBHelper(mContext);
        // helper.updateSeed(seed);
        // }
        return 0;
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
