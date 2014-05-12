package org.gots.sensor.parrot;

import java.util.Date;

import com.google.gson.annotations.SerializedName;

/*
 "air_temperature_celsius": 25.428606875, 
 "capture_ts": "2014-05-05T11:05:00Z", 
 "par_umole_m2s": 1.03332040824322, 
 "vwc_percent": 34.4455270436737
 */
public class ParrotSampleTemperature {
    @SerializedName("capture_ts")
    Date capture_ts;

    @SerializedName("air_temperature_celsius")
    double air_temperature_celsius;

    @SerializedName("par_umole_m2s")
    double par_umole_m2s;

    @SerializedName("vwc_percent")
    double vwc_percent;

    public Date getCapture_ts() {
        return capture_ts;
    }

    public void setCapture_ts(Date capture_ts) {
        this.capture_ts = capture_ts;
    }

    public double getAir_temperature_celsius() {
        return air_temperature_celsius;
    }

    public void setAir_temperature_celsius(double air_temperature_celsius) {
        this.air_temperature_celsius = air_temperature_celsius;
    }

    public double getPar_umole_m2s() {
        return par_umole_m2s;
    }

    public void setPar_umole_m2s(double par_umole_m2s) {
        this.par_umole_m2s = par_umole_m2s;
    }

    public double getVwc_percent() {
        return vwc_percent;
    }

    public void setVwc_percent(double vwc_percent) {
        this.vwc_percent = vwc_percent;
    }

}
