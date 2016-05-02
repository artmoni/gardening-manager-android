package org.gots.sensor;

import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;

import java.util.Date;
import java.util.List;

public interface GotsSensorSamplesProvider {

    public abstract List<ParrotSampleTemperature> getSamplesTemperature(Date from, Date to);

    public abstract void insertSampleTemperature(ParrotSampleTemperature parrotSampleTemperature);

    public abstract long insertSampleFertilizer(ParrotSampleFertilizer parrotSampleFertilizer);

    public abstract List<ParrotSampleFertilizer> getSamplesFertilizer(Date from, Date to);
}
