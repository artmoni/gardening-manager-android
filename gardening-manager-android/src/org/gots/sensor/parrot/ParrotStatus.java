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

    public String getDone_action_timedate_utc() {
        return done_action_timedate_utc;
    }

    public void setDone_action_timedate_utc(String done_action_timedate_utc) {
        this.done_action_timedate_utc = done_action_timedate_utc;
    }

    public String getInstruction_key() {
        return instruction_key;
    }

    public void setInstruction_key(String instruction_key) {
        this.instruction_key = instruction_key;
    }

    public String getNext_analysis_timedate_utc() {
        return next_analysis_timedate_utc;
    }

    public void setNext_analysis_timedate_utc(String next_analysis_timedate_utc) {
        this.next_analysis_timedate_utc = next_analysis_timedate_utc;
    }

    public String getPredicted_action_timedate_utc() {
        return predicted_action_timedate_utc;
    }

    public void setPredicted_action_timedate_utc(String predicted_action_timedate_utc) {
        this.predicted_action_timedate_utc = predicted_action_timedate_utc;
    }

    public String getStatus_key() {
        return status_key;
    }

    public void setStatus_key(String status_key) {
        this.status_key = status_key;
    }

}
