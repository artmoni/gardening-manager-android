package org.gots.seed.provider.nuxeo;

import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoGrowingSeedConverter {

    private static final String TAG = NuxeoGrowingSeedConverter.class.getSimpleName();

    public static GrowingSeedInterface convert(Document document) {
        try {
            GrowingSeedInterface seed = new GrowingSeed();
            seed.setDateSowing(document.getDate("growingseed:datesowing"));
//            seed.setVariety(document.getTitle());
//            seed.setFamily(document.getString("vendorseed:family"));
//            seed.setSpecie(document.getString("vendorseed:specie"));
//            seed.setDurationMin(Integer.valueOf(document.getString("vendorseed:durationmin") != null ? document.getString("vendorseed:durationmin") : "-1"));
//            seed.setDurationMax(Integer.valueOf(document.getString("vendorseed:durationmax")));
//            seed.setDateSowingMin(Integer.valueOf(document.getString("vendorseed:datesowingmin")));
//            seed.setDateSowingMax(Integer.valueOf(document.getString("vendorseed:datesowingmax")));
//            seed.setDescriptionCultivation(document.getString("vendorseed:description_cultivation"));
//            seed.setDescriptionDiseases(document.getString("vendorseed:description_diseases"));
//            seed.setDescriptionGrowth(document.getString("vendorseed:description_growth"));
//            seed.setDescriptionHarvest(document.getString("vendorseed:description_harvest"));

            seed.setUUID(document.getId());
            return seed;
        } catch (Exception e) {
            Log.e(TAG, "Your document schema is not correct", e);
            return null;
        }
    }
}
