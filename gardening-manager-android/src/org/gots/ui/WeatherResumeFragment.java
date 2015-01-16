package org.gots.ui;

import java.util.List;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.ui.fragment.BaseGotsFragment;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.provider.local.LocalWeatherProvider;
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

    private GotsGardenManager gardenManager;

    private GardenInterface currentGarden;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        return inflater.inflate(R.layout.weather_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected void onCurrentGardenChanged() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onWeatherChanged() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onActionChanged() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        weatherWidgetLayout = (LinearLayout) getView().findViewById(R.id.WeatherWidget);
        weatherWidgetLayout.removeAllViews();
        descriptionWeather = (TextView) getView().findViewById(R.id.textViewWeatherDescription);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        descriptionWeather.setText(currentGarden.getLocality());
        WeatherWidget weatherWidget = new WeatherWidget(getActivity(), WeatherView.FULL,
                (List<WeatherConditionInterface>) data);
        weatherWidgetLayout.addView(weatherWidget);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = gardenManager.getCurrentGarden();
        WeatherManager wm = new WeatherManager(getActivity());
        List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) wm.getConditionSet(2);
        return conditions;
    }

}
