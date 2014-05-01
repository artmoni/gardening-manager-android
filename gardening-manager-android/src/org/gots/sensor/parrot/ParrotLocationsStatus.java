package org.gots.sensor.parrot;

import com.google.gson.annotations.SerializedName;

public class ParrotLocationsStatus {

    @SerializedName("location_identifier")
    String location_identifier;

    @SerializedName("global_validity_timedate_utc")
    String global_validity_timedate_utc;

    @SerializedName("last_processed_upload_timedate_utc")
    String last_processed_upload_timedate_utc;

    @SerializedName("air_temperature")
    ParrotStatus air_temperature;

    @SerializedName("fertilizer")
    ParrotStatus fertilizer;

    @SerializedName("light")
    ParrotStatus light;

    @SerializedName("soil_moisture")
    ParrotStatus soil_moisture;

}
