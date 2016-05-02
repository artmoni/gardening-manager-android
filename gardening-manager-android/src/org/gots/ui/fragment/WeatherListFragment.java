package org.gots.ui.fragment;

import android.util.Log;
import android.widget.SimpleAdapter;

import org.gots.R;
import org.gots.garden.GardenInterface;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.WeatherUtils;
import org.gots.weather.view.WeatherWidget;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * Created by sfleury on 07/01/16.
 */
public class WeatherListFragment extends BaseGotsListFragment {
    private WeatherWidget weatherWidget;

    private WeatherManager weatherManager;
    private int weatherResource;

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
        GardenInterface currentGarden = getCurrentGarden();
        if (weatherManager.fetchWeatherForecast(currentGarden) == WeatherManager.WEATHER_OK) {
            List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) weatherManager.getConditionSet(-20, 10);
            return conditions;
        }
        return super.retrieveNuxeoData();
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<WeatherConditionInterface> conditions = (List<WeatherConditionInterface>) data;
        List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

        String[] from = new String[]{"textViewWeather", "tempMin", "tempMax", "textViewWind", "weatherConditionDate", "weatherImage"};
        int[] to = new int[]{R.id.textViewWeather, R.id.tempMin, R.id.tempMax, R.id.textViewWind, R.id.weatherConditionDate, R.id.weatherImage};

        for (WeatherConditionInterface condition :
                conditions) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("textViewWeather", condition.getSummary());
            map.put("tempMin", String.valueOf(Math.round(condition.getTempCelciusMin())));
            map.put("tempMax", String.valueOf(Math.round(condition.getTempCelciusMax())));
            map.put("textViewWind", condition.getWindCondition());
            weatherResource = WeatherUtils.getWeatherResource(getActivity(), condition);
            map.put("weatherImage", Integer.toString(weatherResource));
            SimpleDateFormat sdf = new SimpleDateFormat("E d LLL hh:mm", Locale.getDefault());
            map.put("weatherConditionDate", sdf.format(condition.getDate()));

            fillMaps.add(map);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(), fillMaps, R.layout.weather_list_item, from, to);
        listView.setAdapter(simpleAdapter);
        Log.d(WeatherListFragment.class.getSimpleName(), "onNuxeoDataRetrieved");
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onListItemClicked(int i) {

    }

    @Override
    protected void doRefresh() {

    }

    @Override
    public void update() {

    }
}
