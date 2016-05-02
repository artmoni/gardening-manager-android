package org.gots.sensor;

import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSensor;

import java.util.List;

public interface GotsSensorProvider {


    public abstract List<ParrotLocation> getLocations();

    public abstract List<ParrotLocationsStatus> getStatus();

    public abstract List<ParrotSensor> getSensors();

}
