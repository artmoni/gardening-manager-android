package org.gots.ui.fragment;

import java.util.List;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherResumeFragment extends BaseGotsFragment {

    private TextView descriptionWeather;

    private LinearLayout weatherWidgetLayout;

    private GardenInterface currentGarden;

    private WeatherManager weatherManager;

    private WeatherWidget weatherWidget;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        weatherManager = new WeatherManager(getActivity());
        weatherWidget = new WeatherWidget(getActivity(), WeatherView.FULL, null);
        return inflater.inflate(R.layout.weather_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        weatherWidgetLayout = (LinearLayout) view.findViewById(R.id.WeatherWidget);
        descriptionWeather = (TextView) view.findViewById(R.id.textViewWeatherDescription);
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
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        descriptionWeather.setText(currentGarden.getLocality());
        weatherWidget.setWeatherConditions((List<WeatherConditionInterface>) data);
        weatherWidgetLayout.removeAllViews();
        weatherWidgetLayout.addView(weatherWidget);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = getCurrentGarden();
        List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) weatherManager.getConditionSet(2);
        return conditions;
    }

}
