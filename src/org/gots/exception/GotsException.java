package org.gots.exception;

import org.gots.R;
import org.gots.ui.LoginActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

public class GotsException extends Exception {

    private static final long serialVersionUID = -3635594830382970216L;

    protected Context mContext;

    public GotsException(Context context) {
        mContext = context;
    }


}
