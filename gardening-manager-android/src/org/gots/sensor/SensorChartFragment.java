package org.gots.sensor;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.sensor.local.LocalSensorSamplesProvider;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class SensorChartFragment extends Fragment {

    private static final String TAG = "SensorChartFragment";

    WebView webViewTemperature;

    WebView webViewFertilizer;

    private String mLocationIdentifier;

    public SensorChartFragment() {
        this.mLocationIdentifier = "";
    }

    public SensorChartFragment(String locationIdentifier) {
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
        new AsyncTask<Void, Void, Void>() {

            List<ParrotSampleFertilizer> samplesFertilizer = null;

            List<ParrotSampleTemperature> samplesTemperature = null;

            protected void onPreExecute() {
                getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
            };

            @Override
            protected Void doInBackground(Void... params) {
                // GotsSensorProvider sensorProvider = new ParrotSensorProvider(getActivity());
                // GotsSensorProvider sensorProvider = new ParrotSensorProvider(getActivity());
                // List<ParrotLocation> locations = sensorProvider.getLocations();
                // sensorProvider.getStatus();
                GotsSensorSamplesProvider samplesProvider = new LocalSensorSamplesProvider(getActivity(), mLocationIdentifier);
                samplesFertilizer = samplesProvider.getSamplesFertilizer();
                samplesTemperature = samplesProvider.getSamplesTemperature();
                return null;
            }
            protected void onPostExecute(Void result) {
                Calendar cal = Calendar.getInstance();

                if (samplesFertilizer != null && samplesFertilizer.size() > 0) {
                    String chd = new String();
                    String labelX = "0,";
                    String labelY = "0,";
                    double minX = 999, maxX = -999;
                    double minY = 999, maxY = -999;

                    for (ParrotSampleFertilizer fertilizer : samplesFertilizer) {
                        double fertilizerLevel = fertilizer.getFertilizer_level() * 100;
                        chd = chd.concat(String.valueOf(fertilizerLevel));
                        chd = chd.concat(",");
                        if (minY > fertilizerLevel)
                            minY = fertilizerLevel;
                        if (maxY < fertilizerLevel)
                            maxY = fertilizerLevel;

                        cal.setTime(fertilizer.getWatering_cycle_start_date_time_utc());
                        if (minX > cal.get(Calendar.DAY_OF_MONTH))
                            minX = cal.get(Calendar.DAY_OF_MONTH);
                        if (maxX < cal.get(Calendar.DAY_OF_MONTH))
                            maxX = cal.get(Calendar.DAY_OF_MONTH);

                        // labelX= labelX.concat(cal.get(Calendar.DAY_OF_MONTH));

                    }
                    chd = chd.substring(0, chd.length() - 1);
                    labelX = labelX.concat(minX + "," + maxX + ",1");
                    labelY = labelY.concat(minY + "," + maxY);

                    webViewFertilizer.loadUrl(chartURL(chd, labelX, labelY, webViewFertilizer.getWidth(),
                            webViewFertilizer.getHeight()));
                }

                if (samplesTemperature != null && samplesTemperature.size() > 0) {
                    String chd = new String();
                    int i = 0;
                    for (ParrotSampleTemperature sampleTemp : samplesTemperature) {
                        chd = chd.concat(String.valueOf(sampleTemp.getAir_temperature_celsius()));
                        chd = chd.concat(",");
                        cal.setTime(sampleTemp.getCapture_ts());
                        if (i++ >= 50)
                            break;
                    }
                    chd = chd.substring(0, chd.length() - 1);
                    webViewTemperature.loadUrl(chartURL(chd, "", "", webViewTemperature.getWidth(),
                            webViewTemperature.getHeight()));
                }
                getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
            };
        }.execute();
    }

    private String chartURL(String chd, String x, String y, int width, int height) {
        // String url = "http://chart.apis.google.com/chart?cht=lc&chs=250x100&chd=t:" + serieTempMin + "|" +
        // serieTempMax
        // + "&chxt=x,y&chxr=0," + min.get(Calendar.DAY_OF_MONTH) + "," + max.get(Calendar.DAY_OF_MONTH)
        // + ",1|1,-50,50&chds=-50,50&chco=009999,B65635";

        String url = "http://chart.apis.google.com/chart?cht=lc";

        url = url.concat("&chd=t:" + chd);
        // if (width>1000){
        // width=800;
        // height=300;
        // }
        height = 200;
        width = 400;
        url = url.concat("&chs=" + width + "x" + height);
        url = url.concat("&chco=432D07");
        url = url.concat("&chds=a");
        url = url.concat("&chxt=x,y");
        url = url.concat("&chxr=" + x + "|" + y);
        Log.d(TAG, url);
        return url;
    }
}
