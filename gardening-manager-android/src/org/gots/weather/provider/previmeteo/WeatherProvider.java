package org.gots.weather.provider.previmeteo;

import java.util.Date;

import org.gots.weather.WeatherConditionInterface;

public interface WeatherProvider {

    public abstract WeatherConditionInterface getCondition(Date requestedDay);
   
    public abstract WeatherConditionInterface updateCondition(WeatherConditionInterface condition, Date day);

}