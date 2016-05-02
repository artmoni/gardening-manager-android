package org.gots.weather.provider.previmeteo;

import org.gots.garden.GardenInterface;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.exception.UnknownWeatherException;

import java.util.Date;

public interface WeatherProvider {

    public abstract long getNbConditionsHistory();

    public abstract WeatherConditionInterface getCondition(Date requestedDay) throws UnknownWeatherException;

    public abstract WeatherConditionInterface updateCondition(WeatherConditionInterface condition);

    public abstract WeatherConditionInterface insertCondition(WeatherConditionInterface weatherCondition);

    public abstract short fetchWeatherForecast(GardenInterface gardenInterface);

}