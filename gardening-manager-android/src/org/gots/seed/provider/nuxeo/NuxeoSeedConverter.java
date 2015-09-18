package org.gots.seed.provider.nuxeo;

import android.util.Log;

import org.gots.seed.BaseSeed;
import org.gots.seed.GrowingSeedImpl;
import org.gots.seed.LikeStatus;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import java.util.Locale;

public class NuxeoSeedConverter {

    private static final String TAG = NuxeoSeedConverter.class.getSimpleName();

    public static BaseSeed convert(Document document) throws NumberFormatException {
        BaseSeed seed = new GrowingSeedImpl();
        seed.setName(document.getString("vendorseed:name"));
        seed.setVariety(document.getTitle());
        seed.setFamily(document.getString("vendorseed:family"));
        seed.setSpecie(document.getString("vendorseed:specie"));
        seed.setDurationMin(Integer.valueOf(!"null".equals(document.getString("vendorseed:durationmin")) ? document.getString("vendorseed:durationmin") : "-1"));
        seed.setDurationMax(Integer.valueOf(!"null".equals(document.getString("vendorseed:durationmax")) ? document.getString("vendorseed:durationmax") : "-1"));
        seed.setDateSowingMin(Integer.valueOf(!"null".equals(document.getString("vendorseed:datesowingmin")) ? document.getString("vendorseed:datesowingmin") : "-1"));
        seed.setDateSowingMax(Integer.valueOf(!"null".equals(document.getString("vendorseed:datesowingmax")) ? document.getString("vendorseed:datesowingmax") : "-1"));
        seed.setDescriptionCultivation(document.getString("vendorseed:description_cultivation"));
        seed.setDescriptionDiseases(document.getString("vendorseed:description_diseases"));
        seed.setDescriptionEnvironment(document.getString("vendorseed:description_growth"));
        seed.setDescriptionHarvest(document.getString("vendorseed:description_harvest"));
        seed.setLanguage(document.getString("vendorseed:language"));
        seed.setBareCode(document.getString("vendorseed:barcode"));
        seed.setUrlDescription(document.getString("vendorseed:url"));
        seed.setState(document.getState());
        seed.setUUID(document.getId());
        return seed;
    }

    //    SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState = "project" AND vendorseed:description_harvest!="null" AND vendorseed:description_harvest!=""  AND vendorseed:description_growth!="null" AND vendorseed:description_growth!="" AND vendorseed:description_diseases!="null" AND vendorseed:description_diseases!="" AND vendorseed:description_cultivation!="null" AND vendorseed:description_cultivation!=""
    public static Document convert(String parentPath, BaseSeed seed) {
        Document doc = new Document(parentPath, seed.getName(), "VendorSeed");
        doc.set("dc:title", seed.getVariety());
        doc.set("vendorseed:name", seed.getName());
        doc.set("vendorseed:datesowingmin", String.valueOf(seed.getDateSowingMin()));
        doc.set("vendorseed:datesowingmax", String.valueOf(seed.getDateSowingMax()));
        doc.set("vendorseed:durationmin", String.valueOf(seed.getDurationMin()));
        doc.set("vendorseed:durationmax", String.valueOf(seed.getDurationMax()));
        doc.set("vendorseed:family", seed.getFamily());
        doc.set("vendorseed:specie", seed.getSpecie());
        doc.set("vendorseed:variety", seed.getVariety());
        doc.set("vendorseed:barcode", seed.getBareCode());
        doc.set("vendorseed:language", Locale.getDefault().getCountry().toLowerCase());
        doc.set("vendorseed:description_cultivation", seed.getDescriptionCultivation() != null ? seed.getDescriptionCultivation() : "");
        doc.set("vendorseed:description_diseases", seed.getDescriptionDiseases() != null ? seed.getDescriptionDiseases() : "");
        doc.set("vendorseed:description_growth", seed.getDescriptionEnvironment() != null ? seed.getDescriptionEnvironment() : "");
        doc.set("vendorseed:description_harvest", seed.getDescriptionHarvest() != null ? seed.getDescriptionHarvest() : "");
        doc.set("vendorseed:url", seed.getUrlDescription());
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
