package org.gots.context;

import android.app.Application;

public class SimpleGotsApplication extends Application implements GotsContextProvider {

    GotsContext gotsContext;

    @Override
    public GotsContext getGotsContext() {
        if (gotsContext == null) {
            gotsContext = new GotsContext(this);
        }
        return gotsContext;
    }

}
