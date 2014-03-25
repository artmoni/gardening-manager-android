package org.gots.sensor.parrot;

import com.google.gson.annotations.SerializedName;

public class ParrotSensor {

    @SerializedName("color")
    String color;

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

}
