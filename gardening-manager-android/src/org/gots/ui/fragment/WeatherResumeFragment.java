package org.gots.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.ui.ProfileActivity;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

public class WeatherResumeFragment extends BaseGotsFragment {

    private Button buttonWeatherLocality;

    private LinearLayout weatherWidgetLayout;

    private GardenInterface currentGarden;

    private WeatherManager weatherManager;

    private WeatherWidget weatherWidget;

    private TextView textError;

    private LineChart tempChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        weatherWidget = new WeatherWidget(getActivity(), WeatherView.FULL, null);
        return inflater.inflate(R.layout.weather_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        weatherWidgetLayout = (LinearLayout) view.findViewById(R.id.WeatherWidget);
        tempChart = (LineChart) getView().findViewById(R.id.idChartTemperature);
        buttonWeatherLocality = (Button) view.findViewById(R.id.buttonWeatherLocality);
        textError = (TextView) view.findViewById(R.id.textViewWeatherError);
        buttonWeatherLocality.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        weatherManager = new WeatherManager(getActivity());

        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = getCurrentGarden();
        if (weatherManager.fetchWeatherForecast(currentGarden.getLocalityForecast()) == LocalWeatherProvider.WEATHER_OK) {
            List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) weatherManager.getConditionSet(2);
            return conditions;
        }

        return null;
    }

    private void displayWeatherChart() {
        new AsyncTask<Void, Void, List<WeatherConditionInterface>>() {

            @Override
            protected List<WeatherConditionInterface> doInBackground(Void... params) {
                List<WeatherConditionInterface> conditions = new ArrayList<>();
                for (long i = -weatherManager.getNbConditionsHistory(); i <= 0; i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.DAY_OF_YEAR, (int) i);
                    conditions.add(weatherManager.getCondition(cal.getTime()));
                }
                return conditions;
            }

            protected void onPostExecute(List<WeatherConditionInterface> result) {
                drawTemperatureChart(result);
                tempChart.setVisibility(View.VISIBLE);

            };
        }.execute();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (isAdded()) {
            buttonWeatherLocality.setText(currentGarden.getLocalityForecast());
            buttonWeatherLocality.setBackgroundColor(getResources().getColor(R.color.action_ok_color));
            weatherWidget.setWeatherConditions((List<WeatherConditionInterface>) data);
            weatherWidgetLayout.removeAllViews();
            weatherWidgetLayout.addView(weatherWidget);
            weatherWidget.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (tempChart.getVisibility() == View.VISIBLE)
                        tempChart.setVisibility(View.GONE);
                    else
                        displayWeatherChart();
                }
            });
            textError.setVisibility(View.GONE);
        }
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        if (isAdded()) {
            buttonWeatherLocality.setBackgroundColor(getResources().getColor(R.color.action_error_color));
            weatherWidgetLayout.removeAllViews();
            buttonWeatherLocality.setText(getResources().getString(R.string.menu_configure));
            textError.setVisibility(View.VISIBLE);
        }
        super.onNuxeoDataRetrieveFailed();
    }

    protected void drawTemperatureChart(List<WeatherConditionInterface> weatherConditions) {
        ArrayList<Entry> valsTemperatureMin = new ArrayList<Entry>();
        ArrayList<Entry> valsTemperatureMax = new ArrayList<Entry>();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<String> xValsMonth = new ArrayList<String>();
        int currentMonth = -1;
        for (WeatherConditionInterface temperature : weatherConditions) {

            double tempCelciusMin = temperature.getTempCelciusMin();
            double tempCelciusMax = temperature.getTempCelciusMax();
            if (tempCelciusMax < tempCelciusMin)
                tempCelciusMax = tempCelciusMin;
            cal.setTime(temperature.getDate());
            if (currentMonth == -1 || cal.get(Calendar.MONTH) != currentMonth) {
                xValsMonth.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            }
            Entry entryMin = new Entry((float) tempCelciusMin, index);
            Entry entryMax = new Entry((float) tempCelciusMax, index);
            xVals.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            valsTemperatureMin.add(entryMin);
            valsTemperatureMax.add(entryMax);
            index++;
        }
        LineDataSet setComp1 = new LineDataSet(valsTemperatureMin, "min");
        setComp1.setColor(getResources().getColor(R.color.blue));
        setComp1.setDrawCubic(true);
        LineDataSet setComp2 = new LineDataSet(valsTemperatureMax, "max");
        setComp2.setColor(getResources().getColor(R.color.action_error_color));
        setComp2.setDrawCubic(true);

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);

        LineData data = new LineData(xVals, dataSets);
        tempChart.setDescription("Temperature");
        tempChart.setData(data);
        tempChart.animateXY(2000, 2000);
        tempChart.invalidate();
    }
}
