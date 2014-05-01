package org.gots.sensor.parrot;

import com.google.gson.annotations.SerializedName;

public class ParrotStatus {
    @SerializedName("done_action_timedate_utc")
    String done_action_timedate_utc;

    @SerializedName("instruction_key")
    String instruction_key;

    @SerializedName("next_analysis_timedate_utc")
    String next_analysis_timedate_utc;

    @SerializedName("predicted_action_timedate_utc")
    String predicted_action_timedate_utc;

    @SerializedName("status_key")
    String status_key;

}
