package org.gots.bean;

import java.io.Serializable;
import java.util.Date;

import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;

import com.google.gson.annotations.SerializedName;

/*
 * {"id":"ec232bf1-c997-444f-acb9-2605ca5258f2","docref":"e68a2769-1a7e-4cf3-8f3f-b42366016991","name":
 * "Validation","taskName":"Validation","directive":"Have a look and moderate this document","comment":"","dueDate":
 * "2015-01-12T13:28:54.13Z"
 * ,"documentTitle":"l","documentLink":"nxpath/default/default-domain/sections/Catalog/l@view_documents"
 * ,"startDate":"2015-01-07T13:28:54.14Z","expired":false}
 */
public class TaskInfo implements Serializable {
    @SerializedName("id")
    String id;

    @SerializedName("docref")
    String docref;

    @SerializedName("name")
    String name;

    @SerializedName("taskName")
    String taskName;

    @SerializedName("directive")
    String directive;

    @SerializedName("comment")
    String comment;

    @SerializedName("dueDate")
    String dueDate;

    @SerializedName("documentTitle")
    String documentTitle;

    @SerializedName("documentLink")
    String documentLink;

    @SerializedName("startDate")
    String startDate;

    @SerializedName("expired")
    boolean expired;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocref() {
        return docref;
    }

    public void setDocref(String docref) {
        this.docref = docref;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }

    public String getDocumentLink() {
        return documentLink;
    }

    public void setDocumentLink(String documentLink) {
        this.documentLink = documentLink;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

}
