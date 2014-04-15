package org.gots.exception;

import org.gots.R;

import android.content.Context;

public class GotsUserNotConnectedException extends GotsException {
    private static final long serialVersionUID = -2949217490218307754L;

    public GotsUserNotConnectedException(Context context) {
        super(context);
    }

    @Override
    public String getMessage() {
        return mContext.getResources().getString(R.string.login_disconnect_state);
    }

}
