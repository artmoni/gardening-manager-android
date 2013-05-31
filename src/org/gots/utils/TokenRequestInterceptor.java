package org.gots.utils;

import org.apache.http.HttpRequest;
import org.gots.preferences.GotsPreferences;
import org.nuxeo.ecm.automation.client.jaxrs.RequestInterceptor;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Connector;
import org.nuxeo.ecm.automation.client.jaxrs.spi.Request;

import android.util.Log;

public class TokenRequestInterceptor implements RequestInterceptor {
	private String mToken;
	private String mLogin;
	private String mDeviceId ;
	private String mAppname;

	public TokenRequestInterceptor(String appname, String token, String login,String deviceid) {
		Log.i("TokenRequestInterceptor", "" + token);
		mToken = token;
		mLogin = login;
		mDeviceId = deviceid;
		mAppname=appname;
	}

	@Override
	public void processHttpRequest(HttpRequest request) {
		request.addHeader("X-User-Id", mLogin);
		request.addHeader("X-Device-Id", mDeviceId);
		request.addHeader("X-Application-Name", mAppname);
		request.addHeader("X-Authentication-Token", mToken);
		Log.d("TokenRequestInterceptor","processHttpRequest");
	}

	@Override
	public void processRequest(Request request, Connector connector) {
		request.put("X-User-Id", mLogin);
		request.put("X-Device-Id", mDeviceId);
		request.put("X-Application-Name", mAppname);
		request.put("X-Authentication-Token", mToken);
		Log.d("TokenRequestInterceptor","processRequest");

	}

}
