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

import org.gots.action.AbstractActionSeed;
import org.gots.action.PermanentActionInterface;
import org.gots.exception.GardenNotFoundException;
import org.gots.seed.GrowingSeed;

import android.content.Context;

public class BuyingAction extends AbstractActionSeed implements PermanentActionInterface {

    public BuyingAction(Context context) {
        super(context);
        setName("buy");

    }


    @Override
    public int execute(GrowingSeed seed) {
        super.execute(seed);
        try {
            seedManager.addToStock(seed.getPlant(), gardenManager.getCurrentGarden());
        } catch (GardenNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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


}
