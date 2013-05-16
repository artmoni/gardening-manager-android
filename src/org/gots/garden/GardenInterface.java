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
package org.gots.garden;

import java.util.Date;

import org.gots.bean.Address;

public interface GardenInterface {

	public abstract String getAdminArea();

	public abstract void setAdminArea(String adminArea);

	public abstract String getCountryName();

	public abstract void setCountryName(String countryname);

	public abstract String getLocality();

	public abstract void setLocality(String locality);

	public abstract void setAddress(Address address);

	public abstract Address getAddress();

	// public abstract ArrayList<Allotment> getAllotments();

	// public abstract BaseAllotmentInterface getAllotment(String reference);

	public abstract void setDescription(String description);

	public abstract String getDescription();

	public abstract void setName(String name);

	public abstract String getName();

	public abstract void setGpsLongitude(double gpsLongitude);

	public abstract double getGpsLongitude();

	public abstract void setGpsLatitude(double gpsLatitude);

	public abstract void setId(long id);

	public abstract long getId();

	public abstract void setDateLastSynchro(Date dateLastSynchro);

	public abstract Date getDateLastSynchro();

	public abstract double getGpsLatitude();

	public abstract double getGpsAltitude();

	public abstract void setGpsAltitude(double gpsAltitude);

	public abstract void setUUID(String id);

	public abstract String getUUID();

}
