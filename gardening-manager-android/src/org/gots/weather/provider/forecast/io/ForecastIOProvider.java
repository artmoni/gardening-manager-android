package org.gots.weather.provider.forecast.io;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.gots.garden.GardenInterface;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.exception.UnknownWeatherException;
import org.gots.weather.provider.WeatherCache;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sfleury on 10/08/15.
 * Refer to https://developer.forecast.io/docs/v2
 */
public class ForecastIOProvider extends LocalWeatherProvider {

    private static final String URL_UNITS = "units=si";
    private static final String URL_FORECAST = "http://services.gardening-manager.com/forecast/";
    private static final String URL_EXCLUDE = "exclude=minutly,hourly,flags";

    public ForecastIOProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public short fetchWeatherForecast(GardenInterface gardenInterface) {
        WeatherCache weatherCache = new WeatherCache(mContext);
        try {
            URL url = new URL(URL_FORECAST + gardenInterface.getGpsLatitude() + "," + gardenInterface.getGpsLongitude() + "?" + URL_UNITS + "&" + URL_EXCLUDE);
            InputStream forecast = weatherCache.getCacheByURL(url);
            BufferedReader streamReader = new BufferedReader(new InputStreamReader(forecast, "UTF-8"));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while ((inputStr = streamReader.readLine()) != null)
                responseStrBuilder.append(inputStr);
            JSONObject json = new JSONObject(responseStrBuilder.toString());
            JsonDeserializer<Date> deser = new JsonDeserializer<Date>() {
                @Override
                public Date deserialize(JsonElement json, Type typeOfT,
                                        JsonDeserializationContext context) throws JsonParseException {
                    return json == null ? null : new Date(json.getAsJsonPrimitive().getAsLong() * 1000); //convert seconds to milliseconds
                }
            };
            Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, deser).create();

            ForecastIOHandler currentForecast = gson.fromJson(json.getJSONObject("currently").toString(), ForecastIOHandler.class);
            JSONObject daily = json.getJSONObject("daily");
            JSONArray days = daily.getJSONArray("data");
            for (int i = 0; i < days.length(); i++) {
                ForecastIOHandler weatherForecast = gson.fromJson(days.getJSONObject(i).toString(), ForecastIOHandler.class);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Log.d(ForecastIOProvider.class.getSimpleName(), weatherForecast.getSummary() + " " + dateFormat.format(weatherForecast.getDate()));
                try {
                    WeatherConditionInterface condition = super.getCondition(weatherForecast.getDate());
                    weatherForecast.setId(condition.getId());
                    super.updateCondition(weatherForecast);
                } catch (UnknownWeatherException e) {
                    super.insertCondition(weatherForecast);

                }
            }
            return WEATHER_OK;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return WEATHER_ERROR_UNKNOWN;
    }


}
