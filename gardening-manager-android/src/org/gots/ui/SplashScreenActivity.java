/*******************************ù**************************************************
 * Copyright (c) 2012 sfleury.ù
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import org.gots.authentication.AuthenticationActivity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;

public class SplashScreenActivity extends AboutActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void removeProgress() {
        super.removeProgress();
        if (refreshCounter == 0) {
            // gotsPrefs.setParrotToken(null);
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
    };

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if (arg1 == 1)
            onRefresh(null);
        if (arg1 == 2)
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onResume() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("gardening-manager");
        if (accounts.length == 0) {
            Intent intent = new Intent(this, AuthenticationActivity.class);
            intent.putExtra(AuthenticationActivity.ARG_ACCOUNT_TYPE, "gardening-manager");
            intent.putExtra(AuthenticationActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
            startActivityForResult(intent, 1);
            // finish();
        } else
            onRefresh(null);

        super.onResume();
    }
}
