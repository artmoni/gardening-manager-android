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
            Long altitude = gardenWorkspace.getLong("garden:altitude");
            if (altitude != null)
                garden.setGpsAltitude(altitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct altitude");
        }
        try {
            Long latitude = gardenWorkspace.getLong("garden:latitude");
            if (latitude != null)
                garden.setGpsLatitude(latitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct latitude");
        }
        try {

            Long longitude = gardenWorkspace.getLong("garden:longitude");
            if (longitude != null)
                garden.setGpsLongitude(longitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct longitude");
        }
        garden.setLocality(gardenWorkspace.getString("garden:city"));
        if ("null".equals(garden.getLocality()))
            garden.setLocality(gardenWorkspace.getTitle());
        garden.setCountryName(gardenWorkspace.getString("garden:country"));
        garden.setAdminArea(gardenWorkspace.getString("garden:region"));
        garden.setName(garden.getLocality() + " (" + gardenWorkspace.getString("dc:creator") + ")");
        return garden;
    }

    public static Document convert(String parentPath, GardenInterface garden) {
        Document doc = new Document(parentPath, garden.getLocality(), "Garden");
        doc.set("dc:title", garden.getLocality() + "(" + garden.getAdminArea() + ")");
        doc.set("garden:altitude", garden.getGpsAltitude());
        doc.set("garden:latitude", garden.getGpsLatitude());
        doc.set("garden:longitude", garden.getGpsLongitude());
        doc.set("garden:city", garden.getLocality());
        doc.set("garden:region", garden.getAdminArea());
        doc.set("garden:country", garden.getCountryName());

        return doc;
    }
}
