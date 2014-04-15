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

import java.util.Date;

import org.gots.garden.GardenInterface;

public class Garden implements GardenInterface {
    private long id;

    private String name;

    private String description;

    private double gpsLatitude;

    private double gpsLongitude;

    private Address address = new Address();

    private Date dateLastSynchro;

    private double gpsAltitude;

    private String uuid;

    @Override
    public double getGpsLatitude() {
        return gpsLatitude;
    }

    @Override
    public void setGpsLatitude(double gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    @Override
    public double getGpsLongitude() {
        return gpsLongitude;
    }

    @Override
    public void setGpsLongitude(double gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Address getAddress() {
        return address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String getAdminArea() {
        return getAddress().getAdminArea();
    }

    @Override
    public String getCountryName() {
        return getAddress().getCountryName();
    }

    @Override
    public String getLocality() {
        return getAddress().getLocality();
    }

    @Override
    public void setAdminArea(String adminArea) {
        this.address.setAdminArea(adminArea);
    }

    @Override
    public void setCountryName(String countryName) {
        this.address.setCountryName(countryName);
    }

    @Override
    public void setLocality(String locality) {
        this.address.setLocality(locality);

    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public Date getDateLastSynchro() {
        return this.dateLastSynchro;
    }

    @Override
    public void setDateLastSynchro(Date dateLastSynchro) {
        this.dateLastSynchro = dateLastSynchro;
    }

    @Override
    public double getGpsAltitude() {

        return gpsAltitude;
    }

    @Override
    public void setGpsAltitude(double gpsAltitude) {
        this.gpsAltitude = gpsAltitude;

    }

    @Override
    public void setUUID(String id) {
        uuid = id;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public String toString() {
        String gardenString = "#" + getId() + " --" + getLocality() + " -- " + "UUID[" + getUUID() + "]\n";
        return gardenString;
    }
}
