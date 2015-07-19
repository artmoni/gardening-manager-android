package org.gots.justvisual;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sfleury on 15/07/15.
 */
public class JustVisualResult {
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

    @Override
    public String toString() {
        return plantNames + " - " + id + " - " + pageUrl + " - " + title;
    }
}
