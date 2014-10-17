package org.gots.context;

import org.gots.preferences.GotsPreferences;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;

import android.content.Context;

public class GotsContext extends NuxeoContext {

    public GotsContext(Context androidContext) {
        super(androidContext, new GotsPreferences(androidContext));
    }

    @Override
    public GotsPreferences getServerConfig() {
        return (GotsPreferences) super.getServerConfig();
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
