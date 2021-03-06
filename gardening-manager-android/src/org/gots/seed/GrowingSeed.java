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
package org.gots.seed;

import java.util.Date;

public interface GrowingSeed {

    public int getId();

    public void setId(int id);

    public BaseSeed getPlant();

    public void setPlant(BaseSeed plant);

    public Date getDateSowing();

    public void setDateSowing(Date dateSowing);

    public Date getDateLastWatering();

    public void setDateLastWatering(Date dateLastWatering);

    public Date getDateHarvest();

    public void setDateHarvest(Date dateHarvest);

    public String getUUID();

    public void setUUID(String id);

}
