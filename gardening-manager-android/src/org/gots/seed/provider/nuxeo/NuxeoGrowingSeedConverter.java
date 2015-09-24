package org.gots.seed.provider.nuxeo;

import org.gots.seed.GrowingSeed;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoGrowingSeedConverter {

    private static final String TAG = NuxeoGrowingSeedConverter.class.getSimpleName();

    public static GrowingSeed populate(GrowingSeed seed, Document document) {
        try {
            seed.setDateSowing(document.getDate("growingseed:datesowing"));
            seed.setDateHarvest(document.getDate("growingseed:dateharvest"));
            seed.getPlant().setUUID(document.getId());
            return seed;
        } catch (Exception e) {
            Log.e(TAG, "Your document schema is not correct", e);
            return null;
        }
    }
}
