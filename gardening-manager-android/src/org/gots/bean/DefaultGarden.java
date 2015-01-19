package org.gots.bean;

import org.gots.garden.GardenInterface;

import android.location.Address;

public class DefaultGarden extends Garden implements GardenInterface {

    public DefaultGarden(Address address) {
        if (address == null){
            setLocality("Sample locality");
            setName("My vegetable garden");
        }
        else {
            setLocality(address.getLocality());
            setName(address.getLocality());
            setGpsLatitude(address.getLatitude());
            setGpsLongitude(address.getLongitude());
        }
    }


}
