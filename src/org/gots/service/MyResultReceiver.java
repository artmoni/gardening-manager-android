package org.gots.service;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import org.json.JSONException;

/**
 * Callback manager
 */
public class MyResultReceiver extends ResultReceiver {
    private Receiver mReceiver;

    public MyResultReceiver(Handler handler) {
        super(handler);
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData) throws JSONException;
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            try {
                mReceiver.onReceiveResult(resultCode, resultData);
            } catch (JSONException e) {

            }
        }
    }
}
