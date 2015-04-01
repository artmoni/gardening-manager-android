package org.gots.sensor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.sensor.local.LocalSensorSamplesProvider;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

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

    // WebView webViewTemperature;

    private ParrotLocation mSensorLocation;

    private WebView webViewLightning;

    private WebView webViewWater;

    private LineChart chartFertilizer;

    private LineChart chartTemperature;

    private LineChart chartWater;

    private LineChart chartLighting;

    public SensorChartFragment() {
        this.mSensorLocation = new ParrotLocation();
    }

    public SensorChartFragment(ParrotLocation locationIdentifier) {
        this.mSensorLocation = locationIdentifier;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_samples, container, false);
        // webViewTemperature = (WebView) view.findViewById(R.id.webViewTemperature);
        // webViewLightning = (WebView) view.findViewById(R.id.webViewLightning);
        // webViewWater = (WebView) view.findViewById(R.id.webViewWater);
        chartWater = (LineChart) view.findViewById(R.id.chartWater);
        chartLighting = (LineChart) view.findViewById(R.id.chartLighting);
        chartFertilizer = (LineChart) view.findViewById(R.id.chartFertilizer);
        chartTemperature = (LineChart) view.findViewById(R.id.chartTemperature);
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
                        mSensorLocation.getLocation_identifier());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_WEEK, -90);
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
                    drawFertilizerChart(samplesFertilizer);

                    // String chd = new String();
                    // String labelX = "0,";
                    // String labelY = "0,";
                    // double minX = 999, maxX = -999;
                    // double minY = 999, maxY = -999;
                    //
                    // for (ParrotSampleFertilizer fertilizer : samplesFertilizer) {
                    // double fertilizerLevel = fertilizer.getFertilizer_level() * 100;
                    // chd = chd.concat(String.valueOf(fertilizerLevel));
                    // chd = chd.concat(",");
                    // if (minY > fertilizerLevel)
                    // minY = fertilizerLevel;
                    // if (maxY < fertilizerLevel)
                    // maxY = fertilizerLevel;
                    //
                    // cal.setTime(fertilizer.getWatering_cycle_start_date_time_utc());
                    // if (minX > cal.get(Calendar.DAY_OF_MONTH))
                    // minX = cal.get(Calendar.DAY_OF_MONTH);
                    // if (maxX < cal.get(Calendar.DAY_OF_MONTH))
                    // maxX = cal.get(Calendar.DAY_OF_MONTH);
                    //
                    // // labelX= labelX.concat(cal.get(Calendar.DAY_OF_MONTH));
                    //
                    // }
                    // chd = chd.substring(0, chd.length() - 1);
                    // labelX = labelX.concat(minX + "," + maxX + ",1");
                    // labelY = labelY.concat(minY + "," + maxY);
                    //
                    // webViewFertilizer.loadUrl(chartURL(chd, labelX, labelY, webViewFertilizer.getWidth(),
                    // webViewFertilizer.getHeight()));
                }

                if (samplesTemperature != null && samplesTemperature.size() > 0) {
                    drawTemperatureChart(samplesTemperature);
                    drawLightingChart(samplesTemperature);
                    drawWaterChart(samplesTemperature);

                    // String chd = new String();
                    // String chxl = "0:|";
                    //
                    // double sumTemp = 0;
                    // double sumWater = 0;
                    // double sumLightning = 0;
                    // int nbValue = 0;
                    // Calendar firstDay = Calendar.getInstance();
                    // firstDay.setTime(samplesTemperature.get(0).getCapture_ts());
                    // int currentday = firstDay.get(Calendar.DAY_OF_MONTH);
                    //
                    // String chdWater = "";
                    // String chdLightning = "";
                    // for (ParrotSampleTemperature sampleTemp : samplesTemperature) {
                    // cal.setTime(sampleTemp.getCapture_ts());
                    //
                    // if (cal.get(Calendar.DAY_OF_MONTH) != currentday) {
                    // currentday = cal.get(Calendar.DAY_OF_MONTH);
                    // if (nbValue == 0)
                    // nbValue = 1;
                    // chd = chd.concat(String.valueOf(sumTemp / nbValue));
                    // chd = chd.concat(",");
                    // chdWater = chdWater.concat(String.valueOf(sumWater / nbValue));
                    // chdWater = chdWater.concat(",");
                    // chdLightning = chdLightning.concat(String.valueOf(sumLightning / nbValue));
                    // chdLightning = chdLightning.concat(",");
                    // chxl = chxl.concat(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
                    // chxl = chxl.concat("|");
                    // nbValue = 0;
                    // sumTemp = 0;
                    // sumWater = 0;
                    // sumLightning = 0;
                    // } else {
                    // nbValue++;
                    // sumTemp += sampleTemp.getAir_temperature_celsius();
                    // sumWater += sampleTemp.getVwc_percent();
                    // sumLightning += sampleTemp.getPar_umole_m2s();
                    // }
                    //
                    // }
                    // if (chd.length() > 1 && chdLightning.length() > 1 && chdWater.length() > 1 && chxl.length() > 1)
                    // {
                    // chd = chd.substring(0, chd.length() - 1);
                    // chdLightning = chdLightning.substring(0, chd.length() - 1);
                    // chdWater = chdWater.substring(0, chdWater.length() - 1);
                    // chxl = chxl.substring(0, chxl.length() - 1);
                    // webViewTemperature.loadUrl(chartURL(chd, "", "", webViewTemperature.getWidth(),
                    // webViewTemperature.getHeight()) + "&chxl=" + chxl);
                    // webViewWater.loadUrl(chartURL(chdWater, "", "", webViewTemperature.getWidth(),
                    // webViewTemperature.getHeight()) + "&chxl=" + chxl);
                    // webViewLightning.loadUrl(chartURL(chdLightning, "", "", webViewTemperature.getWidth(),
                    // webViewTemperature.getHeight()) + "&chxl=" + chxl);
                    // }
                }
                if (isAdded())
                    getActivity().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
            };
        }.execute();
    }

    protected void drawWaterChart(List<ParrotSampleTemperature> samplesTemperature) {
        ArrayList<Entry> valsVwc = new ArrayList<Entry>();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<String> xValsMonth = new ArrayList<String>();
        int currentMonth = -1;
        for (ParrotSampleTemperature temperature : samplesTemperature) {

            double vwcPercent = temperature.getVwc_percent();
            cal.setTime(temperature.getCapture_ts());
            if (currentMonth == -1 || cal.get(Calendar.MONTH) != currentMonth) {
                xValsMonth.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            }
            Entry entry = new Entry((float) vwcPercent, index++);
            xVals.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            valsVwc.add(entry);
        }
        LineDataSet setComp1 = new LineDataSet(valsVwc, mSensorLocation.getPlant_nickname());

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chartWater.setDescription("Soil Moisture");
        chartWater.setData(data);
        chartWater.invalidate();
    }

    // Photosynthetically Active Radiation = PAR
    protected void drawLightingChart(List<ParrotSampleTemperature> samplesTemperature) {
        ArrayList<Entry> valsParUmole = new ArrayList<Entry>();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<String> xValsMonth = new ArrayList<String>();
        int currentMonth = -1;
        for (ParrotSampleTemperature temperature : samplesTemperature) {

            double par_umole = temperature.getPar_umole_m2s();
            cal.setTime(temperature.getCapture_ts());
            if (currentMonth == -1 || cal.get(Calendar.MONTH) != currentMonth) {
                xValsMonth.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            }
            Entry entry = new Entry((float) par_umole, index++);
            xVals.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            valsParUmole.add(entry);
        }
        LineDataSet setComp1 = new LineDataSet(valsParUmole, mSensorLocation.getPlant_nickname());

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chartLighting.setDescription("Lighting");
        chartLighting.setData(data);
        chartLighting.invalidate();
    }

    protected void drawFertilizerChart(List<ParrotSampleFertilizer> samplesFertilizer) {
        ArrayList<Entry> valsFertilizer = new ArrayList<Entry>();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        ArrayList<String> xVals = new ArrayList<String>();
        for (ParrotSampleFertilizer fertilizer : samplesFertilizer) {
            double fertilizerlevel = fertilizer.getFertilizer_level() * 100;
            cal.setTime(fertilizer.getWatering_cycle_start_date_time_utc());
            Entry entry = new Entry((float) fertilizerlevel, index++);
            xVals.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));
            valsFertilizer.add(entry);
        }
        LineDataSet setComp1 = new LineDataSet(valsFertilizer, mSensorLocation.getPlant_nickname());

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chartFertilizer.setDescription("Fertilizer");
        chartFertilizer.setData(data);
        chartFertilizer.invalidate();
    }

    protected void drawTemperatureChart(List<ParrotSampleTemperature> samplesTemperature) {
        ArrayList<Entry> valsTemperature = new ArrayList<Entry>();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<String> xValsMonth = new ArrayList<String>();
        int currentMonth = -1;
        for (ParrotSampleTemperature temperature : samplesTemperature) {

            double tempCelcius = temperature.getAir_temperature_celsius();
            cal.setTime(temperature.getCapture_ts());
            if (currentMonth == -1 || cal.get(Calendar.MONTH) != currentMonth) {
                xValsMonth.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            }
            Entry entry = new Entry((float) tempCelcius, index++);
            xVals.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            valsTemperature.add(entry);
        }
        LineDataSet setComp1 = new LineDataSet(valsTemperature, mSensorLocation.getPlant_nickname());

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        chartTemperature.setDescription("Temperature");
        chartTemperature.setData(data);
        chartTemperature.invalidate();
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
