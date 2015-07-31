package org.gots.justvisual;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sfleury on 15/07/15.
 */
public class JustVisualResult {
    String uuid;

    String commonName;

    String species;

    @SerializedName("id")
    String id;

    @SerializedName("imageUrl")
    String imageUrl;

    @SerializedName("title")
    String title;

    @SerializedName("pageUrl")
    String pageUrl;

    @SerializedName("plantNames")
    String plantNames;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public String getPlantNames() {
        return plantNames;
    }

    public void setPlantNames(String plantNames) {
        this.plantNames = plantNames;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSpecies() {
        String species;
        species = plantNames.substring(plantNames.indexOf('(') + 1, plantNames.indexOf('-') != -1 ? plantNames.indexOf('-') : plantNames.indexOf(')'));
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getCommonName() {
        String commonName = null;
        if (plantNames.indexOf('-') > 0)
            commonName = plantNames.substring(plantNames.indexOf('-') + 1, plantNames.length() - 2);
        if (commonName == null)
            commonName = plantNames.substring(0, plantNames.indexOf('('));
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    @Override
    public String toString() {
        return plantNames + " - " + id + " - " + pageUrl + " - " + title;
    }
}
