package org.gots.sensor.parrot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R.string;
import org.gots.authentication.ParrotAuthentication;
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.sensor.GotsSensorProvider;
import org.gots.sensor.GotsSensorSamplesProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.util.Log;

public class ParrotSamplesProvider implements GotsSensorSamplesProvider {

    private ParrotAuthentication authentication;

    private String TAG = this.getClass().getSimpleName();

    private String filterCriteria = "";

    private String locationId;

    public ParrotSamplesProvider(Context context,String locationId) {
        // super(context);
        authentication = ParrotAuthentication.getInstance(context);
        this.locationId = locationId;
    }

   
    @Override
    public List<ParrotSampleFertilizer> getSamplesFertilizer() {
        String api_1_03_sample = "/sensor_data/v2/sample/location/" + locationId;
        List<ParrotSampleFertilizer> sensorSampleFertilizers = new ArrayList<ParrotSampleFertilizer>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_1_03_sample);
            JSONArray jsonFertilizer = json.getJSONArray("fertilizer");
            Gson gson = new Gson();
            for (int i = 0; i < jsonFertilizer.length(); i++) {
                ParrotSampleFertilizer location = gson.fromJson(jsonFertilizer.getString(i),
                        ParrotSampleFertilizer.class);
                sensorSampleFertilizers.add(location);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sensorSampleFertilizers;
    }

    @Override
    public List<ParrotSampleTemperature> getSamplesTemperature() {
        String api_1_03_sample = "/sensor_data/v2/sample/location/" + locationId;
        List<ParrotSampleTemperature> sensorSampleTemperature = new ArrayList<ParrotSampleTemperature>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_1_03_sample);
            JSONArray jsonFertilizer = json.getJSONArray("samples");
            Gson gson = new Gson();
            for (int i = 0; i < jsonFertilizer.length(); i++) {
                ParrotSampleTemperature location = gson.fromJson(jsonFertilizer.getString(i),
                        ParrotSampleTemperature.class);
                sensorSampleTemperature.add(location);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sensorSampleTemperature;
    }

    @Override
    public void insertSampleFertilizer(ParrotSampleFertilizer parrotSampleFertilizer) {
    }

    @Override
    public void insertSampleTemperature(ParrotSampleTemperature parrotSampleTemperature) {
    }
}
