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

import java.util.ArrayList;

import org.gots.R;
import org.gots.authentication.AuthenticationActivity;
import org.gots.inapp.GotsPurchaseItem;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;

public class SplashScreenActivity extends BaseGotsActivity {

    protected static final String TAG = "SplashScreenActivity";

    IabHelper buyHelper;

    private ImageView imageRefresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

    }

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
        } else {
            checkPurchaseFeature();
            displayVersionName();
        }
        super.onResume();
    }

    private void checkPurchaseFeature() {
        final ArrayList<String> moreSkus = new ArrayList<String>();
        /*
         * Synchronize Purchase feature
         */
        moreSkus.add(GotsPurchaseItem.SKU_PREMIUM);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_PDFHISTORY);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_PARROT);
        buyHelper = new IabHelper(getApplicationContext(), gotsPrefs.getPlayStorePubKey());

        buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            @Override
            public void onIabSetupFinished(IabResult result) {
                // Toast.makeText(getApplicationContext(), "Set up finished!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Set up finished!");

                if (result.isSuccess())
                    buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
                        @Override
                        public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                            if (result.isSuccess()) {
                                gotsPurchase.setPremium(inv.hasPurchase(GotsPurchaseItem.SKU_PREMIUM));
                                gotsPurchase.setFeatureExportPDF(inv.hasPurchase(GotsPurchaseItem.SKU_FEATURE_PDFHISTORY));
                                gotsPurchase.setFeatureParrot(inv.hasPurchase(GotsPurchaseItem.SKU_FEATURE_PARROT));
                                Log.i(TAG, "Successful got inventory!");

                            } else {
                                Log.i(TAG, "Error getting inventory!");
                            }

                            // Thread.currentThread();
                            // try {
                            // Thread.sleep(3000);
                            // imageRefresh.clearAnimation();
                            // startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            // SplashScreenActivity.this.finish();
                            // } catch (Exception e) {
                            // Log.e(TAG, e.getMessage());
                            // }
                        }
                    });

            }
        });

    }

    private void displayVersionName() {
        new AsyncTask<Void, Integer, String>() {
            private TextView name;

            @Override
            protected void onPreExecute() {
                name = (TextView) findViewById(R.id.textVersion);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                PackageInfo pInfo;
                String version = "";
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;

                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }

                return version;
            }

            protected void onPostExecute(String version) {
                name.setText("Version " + version);
            };
        }.execute();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("gardening-manager");
        return accounts.length != 0;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        myFadeInAnimation.setRepeatCount(Animation.INFINITE);
        imageRefresh = (ImageView) findViewById(R.id.imageRefresh);
        imageRefresh.startAnimation(myFadeInAnimation);
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return gardenManager.getMyGardens(true);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        imageRefresh.clearAnimation();
        super.onNuxeoDataRetrieved(data);
    }
}
