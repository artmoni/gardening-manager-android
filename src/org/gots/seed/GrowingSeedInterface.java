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
package org.gots.seed;

import java.util.Date;

public interface GrowingSeedInterface extends BaseSeedInterface {

	public abstract void setGrowingSeedId(int id);

	public abstract int getGrowingSeedId();

	public abstract Date getDateSowing();

	public abstract void setDateSowing(Date dateSowing);

	public abstract Date getDateLastWatering();

	public abstract void setDateLastWatering(Date dateLastWatering);

}
