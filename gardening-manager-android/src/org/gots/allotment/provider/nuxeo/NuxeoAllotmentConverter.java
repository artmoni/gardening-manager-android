package org.gots.allotment.provider.nuxeo;

import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoAllotmentConverter {

    private static final String TAG = "NuxeoAllotmentConverter";

    public static BaseAllotmentInterface convert(Document document) {
        try {
            BaseAllotmentInterface allotment = new Allotment();
            allotment.setName(document.getName());
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
        
        return doc;
    }
}
