package org.gots.sensor;

import java.util.List;

import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;

public interface GotsSensorSamplesProvider {

    public abstract List<ParrotSampleTemperature> getSamplesTemperature();

    public abstract void insertSampleTemperature(ParrotSampleTemperature parrotSampleTemperature);

    public abstract void insertSampleFertilizer(ParrotSampleFertilizer parrotSampleFertilizer);

    public abstract List<ParrotSampleFertilizer> getSamplesFertilizer();
}
