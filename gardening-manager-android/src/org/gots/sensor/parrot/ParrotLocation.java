package org.gots.sensor.parrot;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/*{
 "avatar_url": "5f3b23de1810eeae7fc9a7dbf276729a733860e008fad176e82a8fc5b7cc7b52e868ef4512801f1fcd549637cf42a2cbad4e87002217ad2eae7f537078492baa424611060",
 "display_order": 1,
 "hardiness_zone": "3b",
 "heat_zone": 4,
 "ignore_fertilizer_alert": true,
 "ignore_light_alert": false,
 "ignore_moisture_alert": false,
 "ignore_temperature_alert": false,
 "images": [
 {
 "image_identifier": "5f3b23de1810eeae7fc9a7dbf276729a733860e008fad176e82a8fc5b7cc7b52e868ef4512801f1fcd549637cf42a2cbad4e87002217ad2eae7f537078492baa424611060",
 "location_identifier": "SPHDmUzfqR3747575460",
 "url": "https://s3.amazonaws.com/dev-user-images/15606/SPHDmUzfqR3747575460/5f3b23de1810eeae7fc9a7dbf276729a733860e008fad176e82a8fc5b7cc7b52e868ef4512801f1fcd549637cf42a2cbad4e87002217ad2eae7f537078492baa424611060"
 },
 {
 "image_identifier": "ef5c478d01962476b3ce08c3f1cd36e85f0a2f14a6e0b07547b96077267cf86ca5d55ccf3999a9a1172df1f0cbf4ede2f7cafa2a58eb97dd88410f30e8421e55417010181",
 "location_identifier": "SPHDmUzfqR3747575460",
 "url": "https://s3.amazonaws.com/dev-user-images/15606/SPHDmUzfqR3747575460/ef5c478d01962476b3ce08c3f1cd36e85f0a2f14a6e0b07547b96077267cf86ca5d55ccf3999a9a1172df1f0cbf4ede2f7cafa2a58eb97dd88410f30e8421e55417010181"
 }
 ],
 "in_pot": true,
 "is_indoor": true,
 "latitude": 48.9010040788435,
 "location_identifier": "SPHDmUzfqR3747575460",
 "longitude": 2.23968090489715,
 "plant_assigned_date": "2014-03-20T12:02:59Z",
 "plant_id": 7464,
 "plant_nickname": "Bananier dsi",
 "sensor_serial": "9003B70000E7AD7F"
 }
 */

public class ParrotLocation {
    @SerializedName("avatar_url")
    // String avatar_url;
    public String avatar_url;

    @SerializedName("images")
    public Images[] images;
    @SerializedName("description")
    String description;
    @SerializedName("display_order")
    int display_order;
    @SerializedName("first_sample_utc")
    String first_sample_utc;
    @SerializedName("hardiness_zone")
    String hardiness_zone;
    @SerializedName("heat_zone")
    int heat_zone;
    @SerializedName("ignore_fertilizer_alert")
    boolean ignore_fertilizer_alert;
    @SerializedName("ignore_light_alert")
    boolean ignore_light_alert;
    @SerializedName("ignore_moisture_alert")
    boolean ignore_moisture_alert;
    @SerializedName("ignore_temperature_alert")
    boolean ignore_temperature_alert;
    @SerializedName("in_pot")
    boolean in_pot;
    @SerializedName("is_indoor")
    boolean is_indoor;
    @SerializedName("latitude")
    Float latitude;

    // @SerializedName("last_sample_upload")
    // String last_sample_upload;
    //
    // @SerializedName("last_sample_utc")
    // String last_sample_utc;
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

    public String getFirst_sample_utc() {
        return first_sample_utc;
    }

    public void setFirst_sample_utc(String first_sample_utc) {
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

    public boolean getIn_pot() {
        return in_pot;
    }

    public void setIn_pot(boolean in_pot) {
        this.in_pot = in_pot;
    }

    public boolean getIs_indoor() {
        return is_indoor;
    }

    public void setIs_indoor(boolean is_indoor) {
        this.is_indoor = is_indoor;
    }

    public Float getLatitude() {
        return latitude;
    }

    // public String getLast_sample_upload() {
    // return last_sample_upload;
    // }
    //
    // public void setLast_sample_upload(String last_sample_upload) {
    // this.last_sample_upload = last_sample_upload;
    // }
    //
    // public String getLast_sample_utc() {
    // return last_sample_utc;
    // }
    //
    // public void setLast_sample_utc(String last_sample_utc) {
    // this.last_sample_utc = last_sample_utc;
    // }

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

    public String getHardiness_zone() {
        return hardiness_zone;
    }

    public void setHardiness_zone(String hardiness_zone) {
        this.hardiness_zone = hardiness_zone;
    }

    public int getHeat_zone() {
        return heat_zone;
    }

    public void setHeat_zone(int heat_zone) {
        this.heat_zone = heat_zone;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("#");
        builder.append(getLocation_identifier());
        builder.append("(");
        builder.append(getPlant_nickname());
        builder.append(")");
        return builder.toString();
    }

    public Images[] getImages() {
        return images;
    }

    public void setImages(Images[] images) {
        this.images = images;
    }

    public class Images {

        @SerializedName("image_identifier")
        public String image_identifier;

        @SerializedName("location_identifier")
        public String location_identifier;

        @SerializedName("url")
        public String url;

        public Images() {
        }
    }
}
