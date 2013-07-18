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
        try {
            Long altitude = gardenWorkspace.getLong("garden:altitude");
            if (altitude != null)
                garden.setGpsAltitude(altitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden.getName() + " has not a correct altitude");
        }

        try {

            Long longitude = gardenWorkspace.getLong("garden:longitude");
            if (longitude != null)
                garden.setGpsLongitude(longitude);
        } catch (NumberFormatException exception) {
            Log.w("NuxeoGarden", garden.getName() + " has not a correct longitude");
        }

        garden.setUUID(gardenWorkspace.getId());

        return garden;
    }

    public static Document convert(String parentPath, GardenInterface garden) {
        Document doc = new Document(parentPath, garden.getName(), "Garden");
        
        return doc;
    }
}
