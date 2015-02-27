package org.gots.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class RouteNode implements Serializable {

    @SerializedName("rnode:allowTaskReassignment")
    boolean allowTaskReassignment;

    @SerializedName("rnode:lastActor")
    String lastActor;

    @SerializedName("rnode:startDate")
    String startDate;

    @SerializedName("rnode:taskDueDate")
    String taskDueDate;

    @SerializedName("rnode:taskDirective")
    String taskDirective;

    @SerializedName("rnode:taskButtons")
    ArrayList<TaskButton> taskButtons;

    public boolean isAllowTaskReassignment() {
        return allowTaskReassignment;
    }

    public void setAllowTaskReassignment(boolean allowTaskReassignment) {
        this.allowTaskReassignment = allowTaskReassignment;
    }

    public String getLastActor() {
        return lastActor;
    }

    public void setLastActor(String lastActor) {
        this.lastActor = lastActor;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getTaskDueDate() {
        return taskDueDate;
    }

    public void setTaskDueDate(String taskDueDate) {
        this.taskDueDate = taskDueDate;
    }

    public String getTaskDirective() {
        return taskDirective;
    }

    public void setTaskDirective(String taskDirective) {
        this.taskDirective = taskDirective;
    }

    public ArrayList<TaskButton> getTaskButtons() {
        return taskButtons;
    }

    public void setTaskButtons(ArrayList<TaskButton> taskButtons) {
        this.taskButtons = taskButtons;
    }

    // "rnode:allowTaskReassignment": false,
    // "rnode:button": "complete",
    // "rnode:canceled": null,
    // "rnode:count": 2,
    // "rnode:endDate": "2015-02-09T11:03:46.29Z",
    // "rnode:escalationRules": [],
    // "rnode:executeOnlyFirstTransition": false,
    // "rnode:hasMultipleTasks": false,
    // "rnode:hasTask": true,
    // "rnode:inputChain": "",
    // "rnode:lastActor": "gardening.manager@gmail.com",
    // "rnode:merge": "",
    // "rnode:nodeId": "Task58b",
    // "rnode:outputChain": "",
    // "rnode:start": false,
    // "rnode:startDate": "2015-02-09T12:13:12.64Z",
    // "rnode:stop": false,
    // "rnode:subRouteInstanceId": "",
    // "rnode:subRouteModelExpr": "",
    // "rnode:subRouteVariables": [],
    // "rnode:taskAssignees": [],
    // "rnode:taskAssigneesExpr": "Context[\"workflowInitiator\"]",
    // "rnode:taskAssigneesPermission": "",
    // "rnode:taskButtons": [
    // {
    // "filter": "",
    // "label": "Complete",
    // "name": "complete"
    // }
    // ],
    // "rnode:taskDescription": "",
    // "rnode:taskDirective": "Please complete your document and ask for approbation",
    // "rnode:taskDocType": "TaskDoc",
    // "rnode:taskDueDate": "2015-02-14T12:13:12.65Z",
    // "rnode:taskDueDateExpr": "CurrentDate.days(5)",
    // "rnode:taskLayout": "Task58b@taskLayout",
    // "rnode:taskNotificationTemplate": "workflowTaskAssigned",
    // "rnode:taskX": "86",
    // "rnode:taskY": "215",
    // "rnode:tasksInfo": [
    // {
    // "actor": null,
    // "comment": null,
    // "ended": false,
    // "status": null,
    // "taskDocId": "f26c7f6e-285c-4014-a133-552624481ea2"
    // }
    // ],
    // "rnode:transitions": [
    // {
    // "chain": "",
    // "condition": "NodeVariables[\"button\"] ==\"complete\"",
    // "description": null,
    // "label": "complete",
    // "name": "completeTask58b",
    // "result": false,
    // "targetId": "Task7a0"
    // }
    // ],
    // "rnode:variablesFacet": "facet-var_Task58b",
    // "var_Task58b:assignees": []

}
