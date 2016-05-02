/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import org.gots.seed.BaseSeed;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class VendorSeedListAdapter extends SeedListAdapter {


    public VendorSeedListAdapter(Context context, List<BaseSeed> vendorSeeds) {
        super(context, vendorSeeds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = super.getView(position, convertView, parent);

        final BaseSeed currentSeed = getItem(position);

        Calendar sowTime = Calendar.getInstance();
        if (sowTime.get(Calendar.MONTH) > currentSeed.getDateSowingMin())
            sowTime.set(Calendar.YEAR, sowTime.get(Calendar.YEAR) + 1);
        sowTime.set(Calendar.MONTH, currentSeed.getDateSowingMin());

        Calendar harvestTime = new GregorianCalendar();
        harvestTime.setTime(sowTime.getTime());
        harvestTime.add(Calendar.DAY_OF_MONTH, currentSeed.getDurationMin());

        return vi;

    }
}
