package org.gots.ui.fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.xml.soap.Text;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.ui.ProfileActivity;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        tempChart = (LineChart) getView().findViewById(R.id.idChartTemperature);

        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = getCurrentGarden();
//        if (weatherManager.fetchWeatherForecast(currentGarden.getLocalityForecast()) == LocalWeatherProvider.WEATHER_OK) {
//            List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) weatherManager.getConditionSet(2);
//            return conditions;
//        }
        List<WeatherConditionInterface> conditions =new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, -i);
            conditions.add( weatherManager.getCondition(cal.getTime()));
        }
        return conditions;
       //return null;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
//        if (isAdded()) {
//            buttonWeatherLocality.setText(currentGarden.getLocalityForecast());
//            buttonWeatherLocality.setBackgroundColor(getResources().getColor(R.color.action_ok_color));
//            weatherWidget.setWeatherConditions((List<WeatherConditionInterface>) data);
//            weatherWidgetLayout.removeAllViews();
//            weatherWidgetLayout.addView(weatherWidget);
//            textError.setVisibility(View.GONE);
//        }
        drawTemperatureChart((List<WeatherConditionInterface>) data);
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
        ArrayList<Entry> valsTemperature = new ArrayList<Entry>();
        Calendar cal = Calendar.getInstance();
        int index = 0;
        ArrayList<String> xVals = new ArrayList<String>();
        ArrayList<String> xValsMonth = new ArrayList<String>();
        int currentMonth = -1;
        for (WeatherConditionInterface temperature : weatherConditions) {

            double tempCelcius = temperature.getTempCelciusMin();
            cal.setTime(temperature.getDate());
            if (currentMonth == -1 || cal.get(Calendar.MONTH) != currentMonth) {
                xValsMonth.add(cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));
            }
            Entry entry = new Entry((float) tempCelcius, index++);
            xVals.add(String.valueOf(cal.get(Calendar.DAY_OF_MONTH)));

            valsTemperature.add(entry);
        }
        LineDataSet setComp1 = new LineDataSet(valsTemperature, "min");

        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(setComp1);

        LineData data = new LineData(xVals, dataSets);
        tempChart.setDescription("Temperature");
        tempChart.setData(data);
        tempChart.invalidate();
    }
}
