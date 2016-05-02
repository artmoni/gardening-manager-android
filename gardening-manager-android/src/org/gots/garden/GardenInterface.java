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
package org.gots.garden;

import org.gots.bean.Address;

import java.util.Date;

public interface GardenInterface {

    public abstract String getAdminArea();

    public abstract void setAdminArea(String adminArea);

    public abstract String getCountryName();

    public abstract void setCountryName(String countryname);

    public abstract String getLocality();

    public abstract void setLocality(String locality);

    public abstract Address getAddress();

    public abstract void setAddress(Address address);

    // public abstract ArrayList<Allotment> getAllotments();

    // public abstract BaseAllotmentInterface getAllotment(String reference);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract double getGpsLongitude();

    public abstract void setGpsLongitude(double gpsLongitude);

    public abstract long getId();

    public abstract void setId(long id);

    public abstract Date getDateLastSynchro();

    public abstract void setDateLastSynchro(Date dateLastSynchro);

    public abstract double getGpsLatitude();

    public abstract void setGpsLatitude(double gpsLatitude);

    public abstract double getGpsAltitude();

    public abstract void setGpsAltitude(double gpsAltitude);

    public abstract String getUUID();

    public abstract void setUUID(String id);

    public abstract String getCountryCode();

    public abstract void setCountryCode(String countryCode);

    public abstract void setIncredibleEdible(Boolean boolean1);

    public abstract Boolean isIncredibleEdible();

    /**
     * @return forecast locality or default locality if null
     */
    public abstract String getLocalityForecast();

    public abstract void setLocalityForecast(String locality);

}
