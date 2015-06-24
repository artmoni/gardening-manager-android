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

import java.util.List;

import org.gots.seed.GrowingSeed;

public interface BaseAllotmentInterface {

	public abstract List<GrowingSeed> getSeeds();

	public abstract void setSeeds(List<GrowingSeed> seeds);

	public abstract String getName();

	public abstract void setName(String name);

	public abstract String getDescription();

	public abstract void setDescription(String description);
	
	public abstract int getId();

	public abstract void setId(int id);

    public abstract void setUUID(String id);

    public abstract String getUUID();

    public abstract void setImagePath(String imagePath);

    public abstract String getImagePath();
	
		

}
