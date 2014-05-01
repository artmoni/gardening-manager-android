package org.gots.sensor.parrot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R.string;
import org.gots.authentication.ParrotAuthentication;
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;

public class ParrotSensorProvider extends LocalSeedProvider {

    private ParrotAuthentication authentication;

    private String TAG = this.getClass().getSimpleName();

    private String filterCriteria = "";

    public ParrotSensorProvider(Context context) {
        super(context);

    }

    private void getToken() {
        authentication = ParrotAuthentication.getInstance(mContext);
        authentication.getToken();
    }

    public void setSearchCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }
 
    public List<ParrotSensor> getSensors() {
        getToken();
        String api_1_25_sync = "/sensor_data/v2/sync?include_s3_urls=1";
        List<ParrotSensor> sensors = new ArrayList<ParrotSensor>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_1_25_sync);
            JSONArray jsonSensors = json.getJSONArray("sensors");
            Gson gson = new Gson();
            for (int i = 0; i < jsonSensors.length(); i++) {
                ParrotSensor sensor = gson.fromJson(jsonSensors.getString(i), ParrotSensor.class);
                sensors.add(sensor);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sensors;
    }

    public List<ParrotLocationsStatus> getStatus() {
        getToken();
        String api_1_28_status = "/sensor_data/v1/garden_locations_status";
        List<ParrotLocationsStatus> locationsStatuses = new ArrayList<ParrotLocationsStatus>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_1_28_status);
            JSONArray jsonlocations = json.getJSONArray("locations");
            Gson gson = new Gson();
            for (int i = 0; i < jsonlocations.length(); i++) {
                ParrotLocationsStatus locationStatus = gson.fromJson(jsonlocations.getString(i), ParrotLocationsStatus.class);
                locationsStatuses.add(locationStatus);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return locationsStatuses;
    }
    
    public List<ParrotLocation> getLocations() {
        getToken();
        String api_1_25_status = "/sensor_data/v2/sync?include_s3_urls=1";
        List<ParrotLocation> sensorLocations = new ArrayList<ParrotLocation>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_1_25_status);
            JSONArray jsonlocations = json.getJSONArray("locations");
            Gson gson = new Gson();
            for (int i = 0; i < jsonlocations.length(); i++) {
                ParrotLocation location = gson.fromJson(jsonlocations.getString(i), ParrotLocation.class);
                sensorLocations.add(location);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sensorLocations;
    }
}
