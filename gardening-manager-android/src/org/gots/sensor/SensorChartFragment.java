package org.gots.sensor;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.sensor.local.LocalSensorSamplesProvider;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;

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

    private WebView webViewLightning;

    private WebView webViewWater;

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
        webViewLightning = (WebView) view.findViewById(R.id.webViewLightning);
        webViewWater = (WebView) view.findViewById(R.id.webViewWater);
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
                GotsSensorSamplesProvider samplesProvider = new LocalSensorSamplesProvider(getActivity(),
                        mLocationIdentifier);
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_WEEK, -30);
                samplesFertilizer = samplesProvider.getSamplesFertilizer(cal.getTime(), null);
                samplesTemperature = samplesProvider.getSamplesTemperature(cal.getTime(), null);

                // GotsSensorSamplesProvider parrotProvider = new ParrotSamplesProvider(getActivity(),
                // mLocationIdentifier);
                // List<ParrotSampleFertilizer> samplesFertilizer2 = parrotProvider.getSamplesFertilizer(cal.getTime(),
                // null);
                // List<ParrotSampleTemperature> samplesTemperature2 =
                // parrotProvider.getSamplesTemperature(cal.getTime(),
                // null);

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
                    String chxl = "0:|";

                    double sumTemp = 0;
                    double sumWater = 0;
                    double sumLightning = 0;
                    int nbValue = 0;
                    Calendar firstDay = Calendar.getInstance();
                    firstDay.setTime(samplesTemperature.get(0).getCapture_ts());
                    int currentday = firstDay.get(Calendar.DAY_OF_MONTH);

                    String chdWater = "";
                    String chdLightning = "";
                    for (ParrotSampleTemperature sampleTemp : samplesTemperature) {
                        cal.setTime(sampleTemp.getCapture_ts());

                        if (cal.get(Calendar.DAY_OF_MONTH) != currentday) {
                            currentday = cal.get(Calendar.DAY_OF_MONTH);
                            if (nbValue == 0)
                                nbValue = 1;
                            chd = chd.concat(String.valueOf(sumTemp / nbValue));
                            chd = chd.concat(",");
                            chdWater = chdWater.concat(String.valueOf(sumWater / nbValue));
                            chdWater = chdWater.concat(",");
                            chdLightning = chdLightning.concat(String.valueOf(sumLightning / nbValue));
                            chdLightning = chdLightning.concat(",");
                            chxl = chxl.concat(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                            chxl = chxl.concat("|");
                            nbValue = 0;
                            sumTemp = 0;
                            sumWater = 0;
                            sumLightning = 0;
                        } else {
                            nbValue++;
                            sumTemp += sampleTemp.getAir_temperature_celsius();
                            sumWater += sampleTemp.getVwc_percent();
                            sumLightning += sampleTemp.getPar_umole_m2s();
                        }

                    }
                    if (chd.length() > 1 && chdLightning.length() > 1 && chdWater.length() > 1 && chxl.length() > 1) {
                        chd = chd.substring(0, chd.length() - 1);
                        chdLightning = chdLightning.substring(0, chd.length() - 1);
                        chdWater = chdWater.substring(0, chdWater.length() - 1);
                        chxl = chxl.substring(0, chxl.length() - 1);
                        webViewTemperature.loadUrl(chartURL(chd, "", "", webViewTemperature.getWidth(),
                                webViewTemperature.getHeight()) + "&chxl=" + chxl);
                        webViewWater.loadUrl(chartURL(chdWater, "", "", webViewTemperature.getWidth(),
                                webViewTemperature.getHeight()) + "&chxl=" + chxl);
                        webViewLightning.loadUrl(chartURL(chdLightning, "", "", webViewTemperature.getWidth(),
                                webViewTemperature.getHeight()) + "&chxl=" + chxl);
                    }
                }
                if (isAdded())
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
