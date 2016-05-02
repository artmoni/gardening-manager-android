package org.gots.sensor.parrot;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class ParrotLocationsStatus {

    private static final Object STATUS_CRITICAL = "status_critical";
    @SerializedName("location_identifier")
    String location_identifier;
    ;
    @SerializedName("global_validity_timedate_utc")
    Date global_validity_timedate_utc;
    @SerializedName("last_processed_upload_timedate_utc")
    Date last_processed_upload_timedate_utc;
    @SerializedName("air_temperature")
    ParrotStatus air_temperature;
    @SerializedName("fertilizer")
    ParrotStatus fertilizer;
    @SerializedName("light")
    ParrotStatus light;
    @SerializedName("soil_moisture")
    ParrotStatus soil_moisture;
    private String STATUS_WARNING = "status_warning";

    public String getLocation_identifier() {
        return location_identifier;
    }

    public void setLocation_identifier(String location_identifier) {
        this.location_identifier = location_identifier;
    }

    public Date getGlobal_validity_timedate_utc() {
        return global_validity_timedate_utc;
    }

    public void setGlobal_validity_timedate_utc(Date global_validity_timedate_utc) {
        this.global_validity_timedate_utc = global_validity_timedate_utc;
    }

    public Date getLast_processed_upload_timedate_utc() {
        return last_processed_upload_timedate_utc;
    }

    public void setLast_processed_upload_timedate_utc(Date last_processed_upload_timedate_utc) {
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

    public boolean isWarning() {

        return (getSoil_moisture() != null && STATUS_WARNING.equals(getSoil_moisture().getStatus_key())
                || (getAir_temperature() != null && STATUS_WARNING.equals(getAir_temperature().getStatus_key()))
                || (getFertilizer() != null && STATUS_WARNING.equals(getFertilizer().getStatus_key())) || (getLight() != null && STATUS_WARNING.equals(getLight().getStatus_key())));
    }

    public boolean isCritical() {

        return (getSoil_moisture() != null && STATUS_CRITICAL.equals(getSoil_moisture().getStatus_key())
                || (getAir_temperature() != null && STATUS_CRITICAL.equals(getAir_temperature().getStatus_key()))
                || (getFertilizer() != null && STATUS_CRITICAL.equals(getFertilizer().getStatus_key())) || (getLight() != null && STATUS_CRITICAL.equals(getLight().getStatus_key())));
    }
}
