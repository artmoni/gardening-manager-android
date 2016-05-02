package org.gots.action.provider.nuxeo;

import android.content.Context;

import org.gots.action.ActionFactory;
import org.gots.action.BaseAction;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public class NuxeoActionConverter {

    public static BaseAction convert(Context context, Document document) {
        BaseAction action = ActionFactory.buildAction(context, document.getTitle());
        if (action != null) {
            action.setUUID(document.getId());
            action.setDuration(Integer.valueOf(document.getString("action:duration")));
//            action.setDateActionDone(document.getDate("action:dateactiondone"));
        }
        return action;
    }

}
