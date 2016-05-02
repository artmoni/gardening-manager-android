package org.gots.utils;

import android.util.Log;

import org.apache.http.HttpRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;

public class OpenIDRequestInterceptor implements RequestInterceptor {
    private String mToken;

    public OpenIDRequestInterceptor(String token) {
        Log.i("OpenIDRequestInterceptor", "" + token);
        mToken = token;
    }

    @Override
    public void processRequest(Request request, Connector connector) {
        request.put("provider", "GoogleOpenIDConnect");
        request.put("response_type", "code");
        request.put("token", mToken);
        request.put("scope", "https://www.googleapis.com/auth/userinfo.email");

		/*
         * https://accounts.google.com/o/oauth2/auth?client_id=1086164590702&
		 * redirect_uri
		 * =http://srv2.gardening-manager.com:8090/nuxeo/nxstartup.faces
		 * ?provider
		 * %3DGoogleOpenIDConnect&response_type=code&scope=https://www.googleapis
		 * .com/auth/userinfo.email
		 */
    }

    @Override
    public void processHttpRequest(HttpRequest request) {
        request.addHeader("provider", "GoogleOpenIDConnect");
        request.addHeader("response_type", "code");
        request.addHeader("token", mToken);

        request.addHeader("scope", "https://www.googleapis.com/auth/userinfo.email");
    }

}
