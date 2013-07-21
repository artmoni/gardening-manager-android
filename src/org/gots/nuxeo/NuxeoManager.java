/* *********************************************************************** *
 * project: org.gots.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : contact at gardening-manager dot com                  *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   http://www.gnu.org/licenses/gpl-2.0.html                              *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *   Contributors:                                                         *
 *                - jcarsique                                                *
 *                                                                         *
 * *********************************************************************** */
package org.gots.nuxeo;

import org.gots.preferences.GotsPreferences;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.context.NuxeoContextFactory;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.TokenRequestInterceptor;

import android.content.Context;
import android.util.Log;

/**
 * @author jcarsique
 *
 */
public class NuxeoManager {
    private static final String TAG = "NuxeoManager";

    private static NuxeoManager instance;

    private static GotsPreferences gotsPrefs;

    private static NuxeoContext nuxeoContext;

    private static NuxeoServerConfig nxConfig;

    private static AndroidAutomationClient nuxeoClient;

    private NuxeoManager() {
    }

    public static NuxeoManager getInstance() {
        if (instance == null) {
            instance = new NuxeoManager();
        }
        return instance;
    }

    public static void init(Context context) {
        gotsPrefs = GotsPreferences.getInstance(context);

        nuxeoContext = NuxeoContextFactory.getNuxeoContext(context);
        nxConfig = nuxeoContext.getServerConfig();
        // nxConfig.setLogin(myLogin);
        // nxConfig.setPassword(gotsPrefs.getNuxeoPassword());
        // nxConfig.setToken(myToken);
        // Uri nxAutomationURI = Uri.parse(Uri.encode(GotsPreferences.getGardeningManagerServerURI()));
        // nxConfig.setServerBaseUrl(nxAutomationURI);
        nxConfig.setServerBaseUrl(gotsPrefs.getGardeningManagerServerURI());
        // nxConfig.setCacheKey(NuxeoServerConfig.PREF_SERVER_TOKEN);
        // nuxeoContext.onConfigChanged();
        // nuxeoContext.getNetworkStatus().reset();
        Log.d(TAG, "getSession with: " + nxConfig.getServerBaseUrl() + " login=" + nxConfig.getLogin() + " password="
                + nxConfig.getPassword());
    }

    public AndroidAutomationClient getNuxeoClient() {
        if (nuxeoClient == null || nuxeoClient.getBaseUrl() == null) {
            nuxeoClient = nuxeoContext.getNuxeoClient();
            String myToken = gotsPrefs.getToken();
            String myLogin = gotsPrefs.getNuxeoLogin();
            String myDeviceId = gotsPrefs.getDeviceId();
            String myApp = gotsPrefs.getGardeningManagerAppname();
            nuxeoClient.setRequestInterceptor(new TokenRequestInterceptor(myApp, myToken, myLogin, myDeviceId));
        }
        return nuxeoClient;
    }

    /**
     * Android 11+: raises a {@link android.os.NetworkOnMainThreadException} if
     * called from the main thread and tries to
     * perform a network call (Nuxeo server)
     */
    public Session getSession() throws NotAvailableOffline {
        return getNuxeoClient().getSession();
    }
}
