package org.gots.ui.fragment;

import java.util.List;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.ui.ProfileActivity;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class WeatherResumeFragment extends BaseGotsFragment {

    private Button buttonWeatherLocality;

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
        buttonWeatherLocality = (Button) view.findViewById(R.id.buttonWeatherLocality);
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
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        buttonWeatherLocality.setText(currentGarden.getLocality());
        buttonWeatherLocality.setBackgroundColor(getResources().getColor(R.color.action_ok_color));
        weatherWidget.setWeatherConditions((List<WeatherConditionInterface>) data);
        weatherWidgetLayout.removeAllViews();
        weatherWidgetLayout.addView(weatherWidget);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        currentGarden = getCurrentGarden();
        if (weatherManager.fetchWeatherForecast(currentGarden.getAddress()) == LocalWeatherProvider.WEATHER_OK) {
            List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) weatherManager.getConditionSet(2);
            return conditions;
        }
        return null;
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        buttonWeatherLocality.setBackgroundColor(getResources().getColor(R.color.action_error_color));
        weatherWidgetLayout.removeAllViews();
        buttonWeatherLocality.setText("Not available for this city");
        super.onNuxeoDataRetrieveFailed();
    }
}
