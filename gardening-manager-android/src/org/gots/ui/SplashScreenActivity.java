/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.lang.ref.WeakReference;

import org.gots.authentication.AuthenticationActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashScreenActivity extends AboutActivity {
    private static final int STOPSPLASH = 0;

    // private static final long SPLASHTIME = 3000;
    private static final long SPLASHTIME = 1000;

    static final class SplashHandler extends Handler {

        private WeakReference<Activity> that;

        public SplashHandler(WeakReference<Activity> that) {
            this.that = that;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
            case STOPSPLASH:
                // remove SplashScreen from view
                if (that.get() != null) {
                    Intent intent = new Intent(that.get(), DashboardActivity.class);
                    that.get().startActivity(intent);
                    that.get().finish();
                }

                break;
            }
            super.handleMessage(msg);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onRefresh(null);

    }

    private Handler getSplashHandler() {
        if (splashHandler == null) {
            WeakReference<Activity> that = new WeakReference<Activity>(this);
            splashHandler = new SplashHandler(that);
        }
        return splashHandler;
    }

    @Override
    protected void removeProgress() {
        super.removeProgress();
        if (refreshCounter == 0) {
            Message msg = new Message();
            msg.what = STOPSPLASH;
            getSplashHandler().sendMessageDelayed(msg, SPLASHTIME);
        }
    }


    @Override
    protected void onResume() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("gardening-manager");
        if (accounts.length == 0) {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.putExtra(AuthenticationActivity.ARG_ACCOUNT_TYPE, "gardening-manager");
            intent.putExtra(AuthenticationActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            startActivity(intent);
        }
        super.onResume();
    }
}
