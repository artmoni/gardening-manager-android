package org.gots.seed.provider.nuxeo;

import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoGrowingSeedConverter {

    private static final String TAG = NuxeoGrowingSeedConverter.class.getSimpleName();

    public static GrowingSeedInterface populate(GrowingSeedInterface seed, Document document) {
        try {
            seed.setDateSowing(document.getDate("growingseed:datesowing"));
            seed.setUUID(document.getId());
            return seed;
        } catch (Exception e) {
            Log.e(TAG, "Your document schema is not correct", e);
            return null;
        }
    }
}
