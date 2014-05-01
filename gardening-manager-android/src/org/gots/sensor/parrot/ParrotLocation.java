package org.gots.sensor.parrot;

import com.google.gson.annotations.SerializedName;

public class ParrotLocation {
    @SerializedName("avatar_url")
    String avatar_url;

    @SerializedName("description")
    String description;

    @SerializedName("display_order")
    String display_order;

    @SerializedName("first_sample_utc")
    String first_sample_utc;

    @SerializedName("ignore_fertilizer_alert")
    boolean ignore_fertilizer_alert;

    @SerializedName("ignore_light_alert")
    boolean ignore_light_alert;

    @SerializedName("ignore_moisture_alert")
    boolean ignore_moisture_alert;

    @SerializedName("ignore_temperature_alert")
    boolean ignore_temperature_alert;

    @SerializedName("in_pot")
    String in_pot;

    @SerializedName("is_indoor")
    String is_indoor;

    @SerializedName("last_sample_upload")
    String last_sample_upload;

    @SerializedName("last_sample_utc")
    String last_sample_utc;

    @SerializedName("latitude")
    String latitude;

    @SerializedName("location_identifier")
    String location_identifier;

    @SerializedName("location_name")
    String location_name;

    @SerializedName("longitude")
    String longitude;

    @SerializedName("plant_assigned_date")
    String plant_assigned_date;

    @SerializedName("plant_id")
    String plant_id;

    @SerializedName("plant_nickname")
    String plant_nickname;

    @SerializedName("sensor_serial")
    String sensor_serial;

    @SerializedName("total_sample_count")
    String total_sample_count;

}
