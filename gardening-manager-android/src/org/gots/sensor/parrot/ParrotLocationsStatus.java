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

    public String getLocation_identifier() {
        return location_identifier;
    }

    public void setLocation_identifier(String location_identifier) {
        this.location_identifier = location_identifier;
    }

    public String getGlobal_validity_timedate_utc() {
        return global_validity_timedate_utc;
    }

    public void setGlobal_validity_timedate_utc(String global_validity_timedate_utc) {
        this.global_validity_timedate_utc = global_validity_timedate_utc;
    }

    public String getLast_processed_upload_timedate_utc() {
        return last_processed_upload_timedate_utc;
    }

    public void setLast_processed_upload_timedate_utc(String last_processed_upload_timedate_utc) {
        this.last_processed_upload_timedate_utc = last_processed_upload_timedate_utc;
    }

    public ParrotStatus getAir_temperature() {
        return air_temperature;
    }

    public void setAir_temperature(ParrotStatus air_temperature) {
        this.air_temperature = air_temperature;
    }

    public ParrotStatus getFertilizer() {
        return fertilizer;
    }

    public void setFertilizer(ParrotStatus fertilizer) {
        this.fertilizer = fertilizer;
    }

    public ParrotStatus getLight() {
        return light;
    }

    public void setLight(ParrotStatus light) {
        this.light = light;
    }

    public ParrotStatus getSoil_moisture() {
        return soil_moisture;
    }

    public void setSoil_moisture(ParrotStatus soil_moisture) {
        this.soil_moisture = soil_moisture;
    }

}
