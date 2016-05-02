package org.gots.bean;

import android.content.Context;
import android.location.Address;
import android.util.Log;

import org.gots.R;
import org.gots.garden.GardenInterface;

public class DefaultGarden extends Garden implements GardenInterface {

    public DefaultGarden(Context context, Address address) {
        if (address != null) {
            setLocality("Sample locality");
            setName(context.getResources().getString(R.string.garden_default_name));

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
