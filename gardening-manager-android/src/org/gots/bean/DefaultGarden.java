package org.gots.bean;

import org.gots.garden.GardenInterface;

import android.location.Address;

public class DefaultGarden extends Garden implements GardenInterface {

    public DefaultGarden(Address address) {
        if (address == null)
            setLocality("Sample locality");
        else {
            setLocality(address.getLocality());
        }
    }

    @Override
    public String getName() {
        return "My new Garden";
    }

}
