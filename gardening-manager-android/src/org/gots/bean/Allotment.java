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
package org.gots.bean;

import java.io.Serializable;

import org.gots.seed.IActionSeedAlert;

public class Allotment extends BaseAllotment implements Serializable, IActionSeedAlert {

    private static final long serialVersionUID = 1L;

    @Override
    public boolean onActionAlert() {
        // for (Iterator<BaseSeedInterface> iterator = getSeeds().values().iterator(); iterator.hasNext();) {
        // BaseSeedInterface currentSeed = iterator.next();
        // if (currentSeed.onActionAlert())
        // return true;
        // }
        return false;
    }

    @Override
    public boolean onActionWarning() {
        // for (Iterator<BaseSeedInterface> iterator = getSeeds().values().iterator(); iterator.hasNext();) {
        // BaseSeedInterface currentSeed = iterator.next();
        // if (currentSeed.onActionWarning())
        // return true;
        // }
        return false;
    }

}
