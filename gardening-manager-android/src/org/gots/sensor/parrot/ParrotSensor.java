package org.gots.sensor.parrot;

import org.gots.R;

import com.google.gson.annotations.SerializedName;

public class ParrotSensor {

    @SerializedName("color")
    int color;

    @SerializedName("current_history_index")
    int current_history_index;

    @SerializedName("firmware_version")
    String firmware_version;

    @SerializedName("nickname")
    String nickname;

    @SerializedName("sensor_serial")
    String sensor_serial;

    @SerializedName("total_uploaded_samples")
    int total_uploaded_samples;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getCurrent_history_index() {
        return current_history_index;
    }

    public void setCurrent_history_index(int current_history_index) {
        this.current_history_index = current_history_index;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSensor_serial() {
        return sensor_serial;
    }

    public void setSensor_serial(String sensor_serial) {
        this.sensor_serial = sensor_serial;
    }

    public int getTotal_uploaded_samples() {
        return total_uploaded_samples;
    }

    public void setTotal_uploaded_samples(int total_uploaded_samples) {
        this.total_uploaded_samples = total_uploaded_samples;
    }

    public int getResourceDrawable() {
        switch (getColor()) {
        case 0:
            return R.drawable.flowerpower_brown;
        case 1:
            return R.drawable.flowerpower_blue;
        case 2:
            return R.drawable.flowerpower_green;
        case 3:
            return R.drawable.flowerpower_brown;
        case 4:
            return R.drawable.flowerpower_brown;
        default:
            return R.drawable.flowerpower_brown;

        }
    }

}
