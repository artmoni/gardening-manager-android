package org.gots.sensor.parrot;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.gots.authentication.provider.parrot.ParrotAuthentication;
import org.gots.sensor.GotsSensorSamplesProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ParrotSamplesProvider implements GotsSensorSamplesProvider {

    private ParrotAuthentication authentication;

    private String TAG = this.getClass().getSimpleName();

    private String filterCriteria = "";

    private String locationId;

    public ParrotSamplesProvider(Context context, String locationId) {
        // super(context);
        authentication = ParrotAuthentication.getInstance(context);
        this.locationId = locationId;
    }

    @Override
    public List<ParrotSampleFertilizer> getSamplesFertilizer(Date from, Date to) {
        String api_1_03_sample = "/sensor_data/v2/sample/location/" + locationId;
        List<ParrotSampleFertilizer> sensorSampleFertilizers = new ArrayList<ParrotSampleFertilizer>();
        JSONObject json = null;

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        if (from != null) {
            String fromUTC = new SimpleDateFormat().format(from);
            params.add(new BasicNameValuePair("from_datetime_utc", fromUTC));
        }
        if (to != null) {
            String toUTC = new SimpleDateFormat().format(to);
            params.add(new BasicNameValuePair("to_datetime_utc", toUTC));
        }
        try {
            json = (JSONObject) authentication.getJSON(api_1_03_sample, params);
            JSONArray jsonFertilizer = json.getJSONArray("fertilizer");
            Gson gson = new Gson();
            for (int i = 0; i < jsonFertilizer.length(); i++) {
                ParrotSampleFertilizer location = gson.fromJson(jsonFertilizer.getString(i),
                        ParrotSampleFertilizer.class);
                sensorSampleFertilizers.add(location);
            }
        } catch (JSONException e) {
            /*
             * {"server_identifier":"1.1.11(p0) December 30th 2014",
             * "user_data_version":59,
             * "errors":[{"error_code":9048,"error_message":"The requested date span is too large"}]}
             */

            if (json != null)
                try {
                    JSONArray error = json.getJSONArray("errors");
                    if (error.length() > 0) {
                        Log.e(TAG, error.getJSONObject(0).getString("error_message"));

                    }
                } catch (JSONException e2) {
                    Log.e(TAG, e2.getMessage(), e2);
                }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sensorSampleFertilizers;
    }

    @Override
    public List<ParrotSampleTemperature> getSamplesTemperature(Date from, Date to) {
        // last_sample_utc = datetime.datetime.strptime(last_sample_utc_string,'%Y-%m-%d %H:%M:%S UTC')
        // from_datetime_utc = (last_sample_utc - datetime.timedelta(days=7)).strftime("%Y-%m-%dT%H:%M:%SZ")
        // to_datetime_utc = last_sample_utc.strftime("%Y-%m-%dT%H:%M:%SZ")
        // url = '/sensor_data/v2/sample/location/' + location_identifier + '?from_datetime_utc=' + from_datetime_utc +
        // '&to_datetime_utc=' + to_datetime_utc
        String api_1_03_sample = "/sensor_data/v2/sample/location/" + locationId;
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        if (from != null) {
            String fromUTC = new SimpleDateFormat().format(from);
            params.add(new BasicNameValuePair("from_datetime_utc", fromUTC));
        }
        if (to != null) {
            String toUTC = new SimpleDateFormat().format(to);
            params.add(new BasicNameValuePair("to_datetime_utc", toUTC));
        }

        List<ParrotSampleTemperature> sensorSampleTemperature = new ArrayList<ParrotSampleTemperature>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_1_03_sample, params);
            if (json.has("samples")) {
                JSONArray jsonFertilizer = json.getJSONArray("samples");
                Gson gson = new Gson();
                for (int i = 0; i < jsonFertilizer.length(); i++) {
                    ParrotSampleTemperature location = gson.fromJson(jsonFertilizer.getString(i),
                            ParrotSampleTemperature.class);
                    sensorSampleTemperature.add(location);
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return sensorSampleTemperature;
    }

    @Override
    public long insertSampleFertilizer(ParrotSampleFertilizer parrotSampleFertilizer) {
        return -1;
    }

    @Override
    public void insertSampleTemperature(ParrotSampleTemperature parrotSampleTemperature) {
    }
}
