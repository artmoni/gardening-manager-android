package org.gots.exception;

import android.content.Context;

import org.gots.R;

public class GotsException extends Exception {

    private static final long serialVersionUID = -3635594830382970216L;

    protected Context mContext;

    public GotsException(Context context) {
        mContext = context;
    }

    public String getMessageDescription() {
        return mContext.getResources().getString(R.string.login_sso_description);
    }


}
