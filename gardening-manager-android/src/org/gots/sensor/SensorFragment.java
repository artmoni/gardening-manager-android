package org.gots.sensor;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class SensorFragment extends Fragment {

    WebView webViewTemperature;

    WebView webViewFertilizer;

    private String mLocationIdentifier;

    public SensorFragment() {
        this.mLocationIdentifier = "";
    }

    public SensorFragment(String locationIdentifier) {
        this.mLocationIdentifier = locationIdentifier;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_samples, container, false);
        webViewTemperature = (WebView) view.findViewById(R.id.webViewTemperature);
        webViewFertilizer = (WebView) view.findViewById(R.id.webViewFertilizer);
        return view;
    }

    @Override
    public void onResume() {
        update();
        super.onResume();
    }

    private void update() {
        new AsyncTask<Void, Void, List<ParrotLocation>>() {

            List<ParrotSampleFertilizer> samplesFertilizer = null;

            List<ParrotSampleTemperature> samplesTemperature = null;

            @Override
            protected List<ParrotLocation> doInBackground(Void... params) {
                ParrotSensorProvider sensorProvider = new ParrotSensorProvider(getActivity());
                List<ParrotLocation> locations = sensorProvider.getLocations();
                sensorProvider.getStatus();
                samplesFertilizer = sensorProvider.getSamples(mLocationIdentifier);
                samplesTemperature = sensorProvider.getSamples2(mLocationIdentifier);

                return locations;
            }

            protected void onPostExecute(List<ParrotLocation> result) {

                if (samplesFertilizer != null && samplesFertilizer.size() > 0) {
                    String chd = new String();
                    for (ParrotSampleFertilizer fertilizer : samplesFertilizer) {
                        chd = chd.concat(String.valueOf(fertilizer.getFertilizer_level() * 100));
                        chd = chd.concat(",");
                    }
                    chd = chd.substring(0, chd.length() - 1);
                    webViewFertilizer.loadUrl(chartURL(chd));
                }
                if (samplesTemperature != null&& samplesTemperature.size() > 0) {
                    String chd = new String();
                    int i = 0;
                    for (ParrotSampleTemperature sampleTemp : samplesTemperature) {
                        chd = chd.concat(String.valueOf(sampleTemp.getAir_temperature_celsius()));
                        chd = chd.concat(",");
                        
                        if (i++ >= 50)
                            break;
                    }
                    chd = chd.substring(0, chd.length() - 1);
                    webViewTemperature.loadUrl(chartURL(chd));
                }
            };
        }.execute();
    }

    private String chartURL(String chd) {
        // String url = "http://chart.apis.google.com/chart?cht=lc&chs=250x100&chd=t:" + serieTempMin + "|" +
        // serieTempMax
        // + "&chxt=x,y&chxr=0," + min.get(Calendar.DAY_OF_MONTH) + "," + max.get(Calendar.DAY_OF_MONTH)
        // + ",1|1,-50,50&chds=-50,50&chco=009999,B65635";
        String url = "http://chart.apis.google.com/chart?cht=lc&chs=250x100&chd=t:" + chd;
        url = url.concat("&chco=432D07");
        url = url.concat("&chds=a");
        return url;
    }

}
