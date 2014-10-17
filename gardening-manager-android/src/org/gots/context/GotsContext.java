package org.gots.context;

import org.nuxeo.android.context.NuxeoContext;

import android.content.Context;

public class GotsContext extends NuxeoContext {

    public GotsContext(Context androidContext) {
        super(androidContext);
    }

    public static GotsContext get(Context gotsContextProvider) {
        if (gotsContextProvider instanceof GotsContextProvider) {
            GotsContextProvider nxApp = (GotsContextProvider) gotsContextProvider;
            return nxApp.getGotsContext();
        } else {
            throw new UnsupportedOperationException("Your application Context should implement GotsContextProvider !");
        }
    }
}
