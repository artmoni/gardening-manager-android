package org.gots.sensor;

import java.util.List;

import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSensor;

public interface GotsSensorProvider {

   

    public abstract List<ParrotLocation> getLocations();

    public abstract List<ParrotLocationsStatus> getStatus();

    public abstract List<ParrotSensor> getSensors();

}
