package org.gots.authentication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GotsAuthenticatorService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        GotsAccountAuthenticator accountAuthenticator = new GotsAccountAuthenticator(this);
        return accountAuthenticator.getIBinder();
    }

}
