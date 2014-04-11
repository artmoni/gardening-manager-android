package org.gots.exception;

import org.gots.R;

import android.content.Context;

public class GotsServerRestrictedException extends Exception {
    private Context mContext;

    public GotsServerRestrictedException(Context context) {
        mContext = context;
    }

    @Override
    public String getMessage() {
        return mContext.getResources().getString(R.string.login_connect_restricted);
    }
}
