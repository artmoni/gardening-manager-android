package org.gots.weather.provider.previmeteo;

import java.util.Date;

import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.exception.UnknownWeatherException;

public interface WeatherProvider {

    public abstract WeatherConditionInterface getCondition(Date requestedDay) throws UnknownWeatherException;
   
    public abstract WeatherConditionInterface updateCondition(WeatherConditionInterface condition);

    public abstract WeatherConditionInterface insertCondition(WeatherConditionInterface weatherCondition);

    public abstract short fetchWeatherForecast(String forecastLocality);

}