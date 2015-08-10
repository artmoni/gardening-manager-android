package org.gots.weather.provider.forecast.io;

import com.google.gson.annotations.SerializedName;

import org.gots.weather.WeatherConditionInterface;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sfleury on 10/08/15.
 */
public class ForecastIOHandler implements WeatherConditionInterface {
    /*
                "apparentTemperatureMax": 71.42,
                "apparentTemperatureMaxTime": 1439244000,
                "apparentTemperatureMin": 59.26,
                "apparentTemperatureMinTime": 1439215200,
                "cloudCover": 0.63,
                "dewPoint": 57.32,
                "humidity": 0.78,
                "icon": "partly-cloudy-day",
                "moonPhase": 0.88,
                "ozone": 315.23,
                "precipIntensity": 0,
                "precipIntensityMax": 0,
                "precipProbability": 0,
                "pressure": 1013.06,
                "summary": "Mostly cloudy throughout the day.",
                "sunriseTime": 1439212921,
                "sunsetTime": 1439262648,
                "temperatureMax": 71.42,
                "temperatureMaxTime": 1439244000,
                "temperatureMin": 59.26,
                "temperatureMinTime": 1439215200,
                "time": 1439190000,
                "visibility": 7.74,
                "windBearing": 271,
                "windSpeed": 10.48
     */
    @SerializedName("apparentTemperature")
    private float apparentTemperature;

    @SerializedName("cloudCover")
    private float cloudCover;

    @SerializedName("dewPoint")
    private float dewPoint;

    @SerializedName("humidity")
    private float humidity;

    @SerializedName("icon")
    private String icon;

    @SerializedName("nearestStormBearing")
    private int nearestStormBearing;

    @SerializedName("nearestStormDistance")
    private int nearestStormDistance;

    @SerializedName("ozone")
    private float ozone;

    @SerializedName("precipIntensity")
    private float precipIntensity;

    @SerializedName("precipProbability")
    private float precipProbability;

    @SerializedName("pressure")
    private float pressure;

    @SerializedName("summary")
    private String summary;

    @SerializedName("temperatureMin")
    private float temperatureMin;

    @SerializedName("temperatureMax")
    private float temperatureMax;

    @SerializedName("time")
    private Date time;

    @SerializedName("visibility")
    private float visibility;

    @SerializedName("windBearing")
    private float windBearing;

    @SerializedName("windSpeed")
    private float windSpeed;

    private int id;
    private String uuid;

    @Override
    public int getDayofYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.DAY_OF_YEAR);
    }

    @Override
    public void setDayofYear(int dayofYear) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.set(Calendar.DAY_OF_YEAR, dayofYear);
        time = cal.getTime();
    }

    @Override
    public Float getTempCelciusMin() {
        return temperatureMin;
    }

    @Override
    public void setTempCelciusMin(Float temp) {
        temperatureMin = temp;
    }

    @Override
    public Float getTempCelciusMax() {
        return temperatureMax;
    }

    @Override
    public void setTempCelciusMax(Float temp) {
        temperatureMax = temp;
    }

    @Override
    public Float getTempFahrenheit() {
        return null;
    }

    @Override
    public void setTempFahrenheit(Float temp) {

    }

    @Override
    public String getSummary() {
        return summary;
    }

    @Override
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String getWindCondition() {
        return null;
    }

    @Override
    public void setWindCondition(String windCondition) {

    }

    @Override
    public Float getHumidity() {
        return humidity;
    }

    @Override
    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    @Override
    public String getIconURL() {
        return icon;
    }

    @Override
    public void setIconURL(String iconURL) {
        icon = iconURL;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setDate(Date date) {
        time = date;
    }

    @Override
    public Date getDate() {
        return time;
    }

    @Override
    public void setUUID(String id) {
        uuid = id;
    }

    @Override
    public String getUUID() {
        return uuid;
    }
}
