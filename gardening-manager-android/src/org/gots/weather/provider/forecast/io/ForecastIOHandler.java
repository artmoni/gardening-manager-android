package org.gots.weather.provider.forecast.io;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by sfleury on 10/08/15.
 */
public class ForecastIOHandler {
    /*
    "apparentTemperature": 59.74,
            "cloudCover": 0.55,
            "dewPoint": 55.56,
            "humidity": 0.86,
            "icon": "partly-cloudy-day",
            "nearestStormBearing": 59,
            "nearestStormDistance": 32,
            "ozone": 317.89,
            "precipIntensity": 0,
            "precipProbability": 0,
            "pressure": 1013.3,
            "summary": "Partly Cloudy",
            "temperature": 59.74,
            "time": 1439216688,
            "visibility": 5.77,
            "windBearing": 260,
            "windSpeed": 10.28
     */
    @SerializedName("apparentTemperature")
    float apparentTemperature;

    @SerializedName("cloudCover")
    float cloudCover;

    @SerializedName("dewPoint")
    float dewPoint;

    @SerializedName("humidity")
    float humidity;

    @SerializedName("icon")
    String icon;

    @SerializedName("nearestStormBearing")
    int nearestStormBearing;

    @SerializedName("nearestStormDistance")
    int nearestStormDistance;

    @SerializedName("ozone")
    float ozone;

    @SerializedName("precipIntensity")
    float precipIntensity;

    @SerializedName("precipProbability")
    float precipProbability;

    @SerializedName("pressure")
    float pressure;

    @SerializedName("summary")
    String summary;

    @SerializedName("temperature")
    float temperature;

    @SerializedName("time")
    Date time;

    @SerializedName("visibility")
    float visibility;

    @SerializedName("windBearing")
    float windBearing;

    @SerializedName("windSpeed")
    float windSpeed;

}
