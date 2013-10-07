package org.gots.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.gots.ui.LoginActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import org.json.JSONException;
import org.json.JSONObject;

public class GoogleAuthManager implements MyResultReceiver.Receiver {

    protected final LoginActivity activity;

    protected Context context;

    protected MyResultReceiver mReceiver;

    public GoogleAuthManager(LoginActivity activity) {
        this.activity = activity;
    }

    /**
     * Callback handler
     */
    @Override
    public void onReceiveResult(int resultCode, Bundle resultData) throws JSONException {
        switch (resultCode) {
        case 0:
            // in progress
            break;
        case 1:
            // ca marche
            JSONObject jsonObject = new JSONObject(
                    resultData.getString(Intent.EXTRA_RETURN_RESULT));
            break;
        case -2:
            // exception
        }
    }

    public void connectGoogle(String url, String command, String clientId,
                              String scope) {
        this.context = activity.getApplicationContext();
        mReceiver = new MyResultReceiver(new Handler());
        mReceiver.setReceiver(this);
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null,
                activity, QueryService.class);
        intent.putExtra("URL", url);
        intent.putExtra("RECEIVER", mReceiver);
        intent.putExtra("COMMAND", command);
        intent.putExtra("TYPE", "POST");
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("client_id", clientId);
        parameters.put("scope", scope);
        intent.putExtra("PARAMETERS", (Serializable) parameters);
        activity.startService(intent);
    }

}
