package org.gots.sensor.parrot;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.SerializedName;

/*
 * {
 "fertilizer_level": 0.0, 
 "id": 281365, 
 "watering_cycle_end_date_time_utc": "2014-03-28T11:55:00Z", 
 "watering_cycle_start_date_time_utc": "2014-03-28T10:55:00Z"
 }, 
 */
public class ParrotSampleFertilizer {
    @SerializedName("id")
    int id;

    @SerializedName("fertilizer_level")
    double fertilizer_level;

    @SerializedName("watering_cycle_end_date_time_utc")
    Date watering_cycle_end_date_time_utc;

    @SerializedName("watering_cycle_start_date_time_utc")
    Date watering_cycle_start_date_time_utc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getFertilizer_level() {
        return fertilizer_level;
    }

    public void setFertilizer_level(double fertilizer_level) {
        this.fertilizer_level = fertilizer_level;
    }

    public Date getWatering_cycle_end_date_time_utc() {
        return watering_cycle_end_date_time_utc;
    }

    public void setWatering_cycle_end_date_time_utc(Date watering_cycle_end_date_time_utc) {
        this.watering_cycle_end_date_time_utc = watering_cycle_end_date_time_utc;
    }

    public Date getWatering_cycle_start_date_time_utc() {
        return watering_cycle_start_date_time_utc;
    }

    public void setWatering_cycle_start_date_time_utc(Date watering_cycle_start_date_time_utc) {
        this.watering_cycle_start_date_time_utc = watering_cycle_start_date_time_utc;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getId());
        builder.append("-");
        builder.append("fertilizer=");
        builder.append(getFertilizer_level());
        builder.append("[");
        builder.append(new SimpleDateFormat().format(getWatering_cycle_start_date_time_utc()));
        builder.append("]");
        return builder.toString();
    }

}
