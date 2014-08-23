package org.gots.weather.provider.nuxeo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.provider.local.LocalWeatherProvider;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoWeatherProvider extends LocalWeatherProvider {
    private static final String WEATHER_FOLDER = "Weather";

    private static final String TAG = "NuxeoWeatherProvider";

    // Document weatherRootFolder = null;
    GardenInterface currentGarden;

    public NuxeoWeatherProvider(Context mContext, GardenInterface currentGarden) {
        super(mContext);
        NuxeoManager.getInstance().initIfNew(mContext);
        this.currentGarden = currentGarden;
        // Session session = getNuxeoClient().getSession();
        // DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        // weatherRootFolder = getWeatherRootFolder(documentMgr, currentGarden);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().initIfNew(mContext).getNuxeoClient();
    }

    private Document getWeatherRootFolder(GardenInterface currentGarden) {
        Document gardenFolder = null;
        Document weatherRootFolder = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
            gardenFolder = documentMgr.getDocument(new IdRef(currentGarden.getUUID()));
            weatherRootFolder = documentMgr.getChild(gardenFolder, WEATHER_FOLDER);
        } catch (Exception e) {
            Log.e(TAG, "getWeatherRootFolder " + e.getMessage());
            weatherRootFolder = createWeatherRootFolder(currentGarden);
        }
        return weatherRootFolder;
    }

    private Document createWeatherRootFolder(GardenInterface currentGarden) {
        Document gardenFolder = null;
        Document weatherRootFolder = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
            gardenFolder = documentMgr.getDocument(new IdRef(currentGarden.getUUID()));
            PropertyMap props = new PropertyMap();
            props.set("dc:title", WEATHER_FOLDER);
            weatherRootFolder = documentMgr.createDocument(gardenFolder, "WeatherFolder", WEATHER_FOLDER, props);
        } catch (Exception e) {
            Log.e(TAG, "createWeatherRootFolder " + e.getMessage());
        }
        return weatherRootFolder;
    }

    @Override
    public WeatherConditionInterface insertCondition(WeatherConditionInterface weatherCondition) {

        weatherCondition = insertNuxeoWeather(weatherCondition);

        return super.insertCondition(weatherCondition);
    }

    protected WeatherConditionInterface insertNuxeoWeather(WeatherConditionInterface weatherCondition) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", weatherCondition.getSummary());
            properties.set("weathercondition:temperature_min", String.valueOf(weatherCondition.getTempCelciusMin()));
            properties.set("weathercondition:temperature_max", String.valueOf(weatherCondition.getTempCelciusMax()));
            properties.set("weathercondition:humidity", String.valueOf(weatherCondition.getHumidity()));
            properties.set("weathercondition:icon", String.valueOf(weatherCondition.getIconURL()));
            properties.set("weathercondition:time", weatherCondition.getDate());
            properties.set("weathercondition:time_dayofyear", String.valueOf(weatherCondition.getDayofYear()));
            properties.set("weathercondition:summary", weatherCondition.getSummary());

            Document weatherConditionDoc = documentMgr.createDocument(getWeatherRootFolder(currentGarden),
                    "WeatherCondition", weatherCondition.getSummary(), properties);
            weatherCondition.setUUID(weatherConditionDoc.getId());
        } catch (Exception e) {
            Log.e(TAG, "insertNuxeoWeather " + e.getMessage(), e);
        }
        return weatherCondition;
    }

    // @Override
    // public WeatherConditionInterface getWeatherByDayofyear(int dayofyear) {
    // WeatherConditionInterface condition = null;
    // try {
    // Session session = getNuxeoClient().getSession();
    // DocumentManager service = session.getAdapter(DocumentManager.class);
    //
    // byte cacheParam = CacheBehavior.STORE;
    // boolean force = true;
    // boolean refresh = force;
    // if (refresh) {
    // cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
    // refresh = false;
    // }
    // // Document actionDoc = service.getchi
    // Documents docs = service.query(
    // "SELECT * FROM WeatherCondition WHERE ecm:currentLifeCycleState != \"deleted\" And ecm:parentId = \""
    // + weatherRootFolder.getId() + "\" AND weathercondition:time_dayofyear = \"" + dayofyear
    // + "\"", null, new String[] { "dc:modified DESC" }, "*", 0, 50, cacheParam);
    //
    // if (docs.size() > 0) {
    // condition = convert(docs.get(0));
    // condition = super.updateWeather(condition);
    // }
    // } catch (Exception e) {
    // Log.e(TAG, "getWeatherByDayofyear (" + dayofyear + ")" + e.getMessage(), e);
    // condition = super.getWeatherByDayofyear(dayofyear);
    // if (condition != null && condition.getSummary() != null)
    // insertNuxeoWeather(condition);
    // }
    // return condition;
    // }

    @Override
    public WeatherConditionInterface getCondition(Date requestedDay) {
        WeatherConditionInterface condition = null;
        Calendar weatherDate = Calendar.getInstance();
        weatherDate.setTime(requestedDay);

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean force = true;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            // Document actionDoc = service.getchi
            Documents docs = service.query(
                    "SELECT * FROM WeatherCondition WHERE ecm:currentLifeCycleState != \"deleted\" And ecm:parentId = \""
                            + getWeatherRootFolder(currentGarden).getId()
                            + "\" AND weathercondition:time_dayofyear = \"" + weatherDate.get(Calendar.DAY_OF_YEAR)
                            + "\" AND weathercondition:time_year = \"" + weatherDate.get(Calendar.YEAR) + "\"", null,
                    new String[] { "dc:modified DESC" }, "*", 0, 50, cacheParam);

            if (docs.size() > 0) {
                condition = convert(docs.get(0));
                condition = super.updateCondition(condition, weatherDate.getTime());
            }
        } catch (Exception e) {
            Log.e(TAG, "getWeatherByDayofyear (" + requestedDay.toString() + ")" + e.getMessage(), e);
            condition = super.getCondition(requestedDay);
            if (condition != null && condition.getSummary() != null)
                insertNuxeoWeather(condition);
        }
        return condition;
    }

    public List<WeatherConditionInterface> getAllWeatherForecast() {

        List<WeatherConditionInterface> allConditions = new ArrayList<WeatherConditionInterface>();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean force = true;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            // Document actionDoc = service.getchi
            Documents docs = service.query(
                    "SELECT * FROM WeatherCondition WHERE ecm:currentLifeCycleState != \"deleted\" And ecm:parentId = \""
                            + getWeatherRootFolder(currentGarden).getId() + "\"", null,
                    new String[] { "dc:modified DESC" }, "*", 0, 50, cacheParam);
            for (Document documentWeather : docs) {

                WeatherConditionInterface condition = convert(documentWeather);
                allConditions.add(condition);
            }
        } catch (Exception e) {
            Log.e(TAG, "getAllWeatherForecast " + e.getMessage(), e);

        }
        return allConditions;
    }

    private WeatherConditionInterface convert(Document document) {
        WeatherConditionInterface condition = new WeatherCondition();
        condition.setDate(document.getDate("weathercondition:time"));
        condition.setHumidity(Float.parseFloat(document.getString("weathercondition:humidity")));
        condition.setTempCelciusMin(Float.parseFloat(document.getString("weathercondition:temperature_min")));
        condition.setTempCelciusMax(Float.parseFloat(document.getString("weathercondition:temperature_max")));
        condition.setIconURL(document.getString("weathercondition:icon"));
        condition.setDayofYear(Integer.parseInt(document.getString("weathercondition:time_dayofyear")));
        condition.setSummary(document.getString("weathercondition:summary"));
        return condition;
    }
}
