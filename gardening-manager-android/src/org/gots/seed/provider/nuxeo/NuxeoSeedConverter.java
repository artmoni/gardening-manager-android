package org.gots.seed.provider.nuxeo;

import java.util.Locale;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.LikeStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import android.util.Log;

public class NuxeoSeedConverter {

    private static final String TAG = "NuxeoSeedConverter";

    public static BaseSeedInterface convert(Document document) {
        try {
            BaseSeedInterface seed = new GrowingSeed();
            seed.setVariety(document.getTitle());
            seed.setFamily(document.getString("vendorseed:family"));
            seed.setSpecie(document.getString("vendorseed:specie"));
            seed.setDurationMin(Integer.valueOf(document.getString("vendorseed:durationmin") != null ? document.getString("vendorseed:durationmin") : "-1"));
            seed.setDurationMax(Integer.valueOf(document.getString("vendorseed:durationmax")));
            seed.setDateSowingMin(Integer.valueOf(document.getString("vendorseed:datesowingmin")));
            seed.setDateSowingMax(Integer.valueOf(document.getString("vendorseed:datesowingmax")));
            seed.setDescriptionCultivation(document.getString("vendorseed:description_cultivation"));
            seed.setDescriptionDiseases(document.getString("vendorseed:description_diseases"));
            seed.setDescriptionGrowth(document.getString("vendorseed:description_growth"));
            seed.setDescriptionHarvest(document.getString("vendorseed:description_harvest"));
            seed.setLanguage(document.getString("vendorseed:language"));
            seed.setBareCode(document.getString("vendorseed:barcode"));
            seed.setState(document.getState());
            seed.setUUID(document.getId());
            return seed;
        } catch (Exception e) {
            Log.e(TAG, "Your document schema is not correct", e);
            return null;
        }
    }

    public static Document convert(String parentPath, BaseSeedInterface seed) {
        Document doc = new Document(parentPath, seed.getName(), "VendorSeed");
        doc.set("dc:title", seed.getVariety());
        doc.set("vendorseed:datesowingmin", String.valueOf(seed.getDateSowingMin()));
        doc.set("vendorseed:datesowingmax", String.valueOf(seed.getDateSowingMax()));
        doc.set("vendorseed:durationmin", String.valueOf(seed.getDurationMin()));
        doc.set("vendorseed:durationmax", String.valueOf(seed.getDurationMax()));
        doc.set("vendorseed:family", seed.getFamily());
        doc.set("vendorseed:specie", seed.getSpecie());
        doc.set("vendorseed:variety", seed.getVariety());
        doc.set("vendorseed:barcode", seed.getBareCode());
        doc.set("vendorseed:language", Locale.getDefault().getCountry().toLowerCase());
        doc.set("vendorseed:description_cultivation", seed.getDescriptionCultivation());
        doc.set("vendorseed:description_diseases", seed.getDescriptionDiseases());
        doc.set("vendorseed:description_growth", seed.getDescriptionGrowth());
        doc.set("vendorseed:description_harvest", seed.getDescriptionHarvest());


        return doc;
    }

    public static LikeStatus getLikeStatus(Blob likeStatus) {
        LikeStatus likes = new LikeStatus();
        try {
            // {"userLikeStatus":0,"username":"Guest","dislikesCount":0,"likesCount":0,"activityObject":"doc:default:625e24be-cead-496d-a017-3526273b4de8"}
            JSONObject json = new JSONObject(likeStatus.toString());
            likes.setUserLikeStatus(json.getInt("userLikeStatus"));
            likes.setLikesCount(json.getInt("likesCount"));

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        return likes;

    }
}
