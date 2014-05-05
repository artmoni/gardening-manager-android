package org.gots.sensor.parrot;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

public class ParrotLocation {
    @SerializedName("avatar_url")
    String avatar_url;

    @SerializedName("description")
    String description;

    @SerializedName("display_order")
    int display_order;

    @SerializedName("first_sample_utc")
    Date first_sample_utc;

    @SerializedName("ignore_fertilizer_alert")
    boolean ignore_fertilizer_alert;

    @SerializedName("ignore_light_alert")
    boolean ignore_light_alert;

    @SerializedName("ignore_moisture_alert")
    boolean ignore_moisture_alert;

    @SerializedName("ignore_temperature_alert")
    boolean ignore_temperature_alert;

    @SerializedName("in_pot")
    int in_pot;

    @SerializedName("is_indoor")
    int is_indoor;

    @SerializedName("last_sample_upload")
    Date last_sample_upload;

    @SerializedName("last_sample_utc")
    Date last_sample_utc;

    @SerializedName("latitude")
    Float latitude;
    
    @SerializedName("longitude")
    Float longitude;

    @SerializedName("location_identifier")
    String location_identifier;

    @SerializedName("location_name")
    String location_name;

    @SerializedName("plant_assigned_date")
    Date plant_assigned_date;

    @SerializedName("plant_id")
    String plant_id;

    @SerializedName("plant_nickname")
    String plant_nickname;

    @SerializedName("sensor_serial")
    String sensor_serial;

    @SerializedName("total_sample_count")
    int total_sample_count;

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
    }

    public Date getFirst_sample_utc() {
        return first_sample_utc;
    }

    public void setFirst_sample_utc(Date first_sample_utc) {
        this.first_sample_utc = first_sample_utc;
    }

    public boolean isIgnore_fertilizer_alert() {
        return ignore_fertilizer_alert;
    }

    public void setIgnore_fertilizer_alert(boolean ignore_fertilizer_alert) {
        this.ignore_fertilizer_alert = ignore_fertilizer_alert;
    }

    public boolean isIgnore_light_alert() {
        return ignore_light_alert;
    }

    public void setIgnore_light_alert(boolean ignore_light_alert) {
        this.ignore_light_alert = ignore_light_alert;
    }

    public boolean isIgnore_moisture_alert() {
        return ignore_moisture_alert;
    }

    public void setIgnore_moisture_alert(boolean ignore_moisture_alert) {
        this.ignore_moisture_alert = ignore_moisture_alert;
    }

    public boolean isIgnore_temperature_alert() {
        return ignore_temperature_alert;
    }

    public void setIgnore_temperature_alert(boolean ignore_temperature_alert) {
        this.ignore_temperature_alert = ignore_temperature_alert;
    }

    public int getIn_pot() {
        return in_pot;
    }

    public void setIn_pot(int in_pot) {
        this.in_pot = in_pot;
    }

    public int getIs_indoor() {
        return is_indoor;
    }

    public void setIs_indoor(int is_indoor) {
        this.is_indoor = is_indoor;
    }

    public Date getLast_sample_upload() {
        return last_sample_upload;
    }

    public void setLast_sample_upload(Date last_sample_upload) {
        this.last_sample_upload = last_sample_upload;
    }

    public Date getLast_sample_utc() {
        return last_sample_utc;
    }

    public void setLast_sample_utc(Date last_sample_utc) {
        this.last_sample_utc = last_sample_utc;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(Float latitude) {
        this.latitude = latitude;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public String getLocation_identifier() {
        return location_identifier;
    }

    public void setLocation_identifier(String location_identifier) {
        this.location_identifier = location_identifier;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public Date getPlant_assigned_date() {
        return plant_assigned_date;
    }

    public void setPlant_assigned_date(Date plant_assigned_date) {
        this.plant_assigned_date = plant_assigned_date;
    }

    public String getPlant_id() {
        return plant_id;
    }

    public void setPlant_id(String plant_id) {
        this.plant_id = plant_id;
    }

    public String getPlant_nickname() {
        return plant_nickname;
    }

    public void setPlant_nickname(String plant_nickname) {
        this.plant_nickname = plant_nickname;
    }

    public String getSensor_serial() {
        return sensor_serial;
    }

    public void setSensor_serial(String sensor_serial) {
        this.sensor_serial = sensor_serial;
    }

    public int getTotal_sample_count() {
        return total_sample_count;
    }

    public void setTotal_sample_count(int total_sample_count) {
        this.total_sample_count = total_sample_count;
    }

}
