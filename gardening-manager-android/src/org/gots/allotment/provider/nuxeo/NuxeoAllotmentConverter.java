package org.gots.allotment.provider.nuxeo;

import android.util.Log;

import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

public class NuxeoAllotmentConverter {

    private static final String ALLOTMENT_NAME = "dc:title";

    private static final String ALLOTMENT_DESCRIPTION = "dc:description";

    private static final String TAG = "NuxeoAllotmentConverter";

    public static BaseAllotmentInterface convert(Document document) {
        try {
            BaseAllotmentInterface allotment = new Allotment();
            allotment.setName(!"null".equals(document.getString(ALLOTMENT_NAME)) ? document.getString(ALLOTMENT_NAME) : document.getName());
            allotment.setDescription(!"null".equals(document.getString(ALLOTMENT_DESCRIPTION)) ? document.getString(ALLOTMENT_DESCRIPTION) : "");
            allotment.setUUID(document.getId());
            return allotment;
        } catch (Exception e) {
            Log.e(TAG, "Your document schema is not correct", e);
            return null;
        }
    }

    public static Document convert(String parentPath, BaseAllotmentInterface allotment) {
        Document doc = new Document(parentPath, allotment.getName(), "Allotment");
        doc.set("dc:title", allotment.getName());
        doc.set(ALLOTMENT_NAME, allotment.getName());
        doc.set(ALLOTMENT_DESCRIPTION, allotment.getDescription());
        return doc;
    }

    public static PropertyMap convertToProperties(BaseAllotmentInterface allotment) {
        PropertyMap properties = new PropertyMap();
        properties.set("dc:title", allotment.getName());
        properties.set(ALLOTMENT_NAME, allotment.getName());
        properties.set(ALLOTMENT_DESCRIPTION, allotment.getDescription());
        return properties;
    }
}
