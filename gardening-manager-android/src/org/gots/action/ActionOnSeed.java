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
package org.gots.action;

import org.gots.seed.GrowingSeed;

public interface ActionOnSeed extends BaseAction {

    public abstract void setId(int id);

    public abstract int getActionSeedId();

    public abstract void setGrowingSeedId(int id);

    public abstract int getGrowingSeedId();

    public int execute(GrowingSeed seed);

    public abstract void setActionSeedId(int actionId);
}
