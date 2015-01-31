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
import org.gots.action.ActionOnSeed;
import org.gots.exception.GardenNotFoundException;
import org.gots.seed.GrowingSeed;

import android.content.Context;

public class ReduceQuantityAction extends AbstractActionSeed implements ActionOnSeed {

    public ReduceQuantityAction(Context mContext) {
        super(mContext);

        setName("reducequantity");
    }

    @Override
    public int execute(GrowingSeed seed) {

        if (seed.getNbSachet() <= 0)
            return 0;
        super.execute(seed);
//        seed.setNbSachet(seed.getNbSachet() - 1);
//        VendorSeedDBHelper helper = new VendorSeedDBHelper(getContext());
//        helper.updateSeed(seed);
        
        try {
            seedManager.removeToStock(seed, gardenManager.getCurrentGarden());
        } catch (GardenNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 0;
    }


}
