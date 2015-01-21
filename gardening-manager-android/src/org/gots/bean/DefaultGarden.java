package org.gots.bean;

import org.gots.garden.GardenInterface;

import android.location.Address;
import android.util.Log;

public class DefaultGarden extends Garden implements GardenInterface {

    public DefaultGarden(Address address) {
        if (address != null) {
            setLocality("Sample locality");
            setName("My vegetable garden");

            try {
                setGpsLatitude(Double.valueOf(address.getLatitude()));
                setGpsLongitude(address.getLongitude());
                setLocality(address.getLocality());
                setName(address.getLocality());
            } catch (Exception e) {
                Log.d(DefaultGarden.class.getSimpleName(), "Error setting default garden address");
            }
        }
    }

}
