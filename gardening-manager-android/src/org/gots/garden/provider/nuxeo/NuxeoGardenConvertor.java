package org.gots.garden.provider.nuxeo;

import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoGardenConvertor {

    public static GardenInterface convert(Document gardenWorkspace) {
        GardenInterface garden = new Garden();

        garden.setUUID(gardenWorkspace.getId());

        try {
            Double altitude = gardenWorkspace.getDouble("garden:altitude");
            if (altitude != null)
                garden.setGpsAltitude(altitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct altitude");
        }
        try {
            Double latitude = gardenWorkspace.getDouble("garden:latitude");
            if (latitude != null)
                garden.setGpsLatitude(latitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct latitude");
        }
        try {
            Double longitude = gardenWorkspace.getDouble("garden:longitude");
            if (longitude != null)
                garden.setGpsLongitude(longitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct longitude");
        }
        garden.setLocality(gardenWorkspace.getString("garden:city"));
        if ("null".equals(garden.getLocality()))
            garden.setLocality(gardenWorkspace.getTitle());
        garden.setAdminArea(gardenWorkspace.getString("garden:region"));
        garden.setName(gardenWorkspace.getTitle());
        garden.setCountryName(gardenWorkspace.getString("location:country"));
        garden.setCountryCode(gardenWorkspace.getString("location:countrycode"));
        garden.setIncredibleEdible(gardenWorkspace.getProperties().getBoolean("garden:incredibleedible"));
        garden.setLocalityForecast(gardenWorkspace.getString("garden:forecast_locality"));
        // garden.setName(garden.getLocality() + " (" + gardenWorkspace.getString("dc:creator") + ")");
        return garden;
    }

    public static Document convert(String parentPath, GardenInterface garden) {
        Document doc = new Document(parentPath, garden.getLocality(), NuxeoGardenProvider.DOCTYPE_GARDEN);
        doc.set("dc:title", garden.getName());
        doc.set("garden:altitude", garden.getGpsAltitude());
        doc.set("garden:latitude", garden.getGpsLatitude());
        doc.set("garden:longitude", garden.getGpsLongitude());
        doc.set("garden:city", garden.getLocality());
        doc.set("garden:region", garden.getAdminArea());
        doc.set("location:country", garden.getCountryName());
        doc.set("location:countrycode", garden.getCountryCode());
        doc.set("garden:incredibleedible", String.valueOf(garden.isIncredibleEdible()));
        doc.set("garden:forecast_locality",garden.getLocalityForecast());
        return doc;
    }
}
