package org.gots.utils;

import org.apache.http.HttpRequest;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;

import android.util.Log;

public class TemporaryTokenRequestInterceptor implements RequestInterceptor {
    private String mToken;

    private String mLogin;

    private String mDeviceId;

    private String mAppname;

    public TemporaryTokenRequestInterceptor(String appname, String token, String login, String deviceid) {
        Log.i("TemporaryTokenRequestInterceptor", "" + token);
        mToken = token;
        mLogin = login;
        mDeviceId = deviceid;
        mAppname = appname;
    }

    @Override
    public void processHttpRequest(HttpRequest request) {
        request.addHeader("X-User-Id", mLogin);
        request.addHeader("X-Device-Id", mDeviceId);
        request.addHeader("X-Application-Name", mAppname);
        request.addHeader("X-Temporary-Authentication-Token", mToken);
        Log.d("TemporaryTokenRequestInterceptor", "processHttpRequest");
    }

    @Override
    public void processRequest(Request request, Connector connector) {
        request.put("X-User-Id", mLogin);
        request.put("X-Device-Id", mDeviceId);
        request.put("X-Application-Name", mAppname);
        request.put("X-Temporary-Authentication-Token", mToken);
        Log.d("TokenRequestInterceptor", "processRequest");

    }

}
