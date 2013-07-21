package org.gots.garden.provider.nuxeo;

import org.gots.bean.Garden;
import org.gots.garden.GardenInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoGardenConvertor {

    public static GardenInterface convert(Document gardenWorkspace) {
        GardenInterface garden = new Garden();
        garden.setName(gardenWorkspace.getTitle());
        garden.setLocality(gardenWorkspace.getTitle());
        garden.setUUID(gardenWorkspace.getPath());

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
            Log.w("NuxeoGarden", garden + " has not a correct altitude");
        }
        try {

            Long longitude = gardenWorkspace.getLong("garden:longitude");
            if (longitude != null)
                garden.setGpsLongitude(longitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden + " has not a correct longitude");
        }


        return garden;
    }

    public static Document convert(String parentPath, GardenInterface garden) {
        Document doc = new Document(parentPath, garden.getLocality(), "Garden");
        doc.set("dc:title",garden.getLocality());
        doc.set("garden:altitude",garden.getGpsAltitude());
        doc.set("garden:longitude",garden.getGpsLongitude());
        
        return doc;
    }
}
