package org.gots.bean;

import com.google.gson.annotations.SerializedName;
//"rnode:taskButtons": [
//{
//  "filter": "",
//  "label": "Complete",
//  "name": "complete"
//}
//],

public class TaskButton {
    @SerializedName("filter")
    String filter;

    @SerializedName("label")
    String label;

    @SerializedName("name")
    String name;

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
