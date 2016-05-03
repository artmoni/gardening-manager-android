/**
 * ****************************ù**************************************************
 * Copyright (c) 2012 sfleury.ù
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.ui;

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
import com.android.vending.billing.util.Purchase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.gots.R;
import org.gots.authentication.AuthenticationActivity;
import org.gots.inapp.GotsPurchaseItem;

import java.util.ArrayList;

public class SplashScreenActivity extends BaseGotsActivity {

    protected static final String TAG = "SplashScreenActivity";

    IabHelper buyHelper;

    private ImageView imageRefresh;

    private TextView versionTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        versionTextView = (TextView) findViewById(R.id.textVersion);
        imageRefresh = (ImageView) findViewById(R.id.imageRefresh);
        Animation myRotateAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);
        myRotateAnimation.setRepeatCount(Animation.INFINITE);
        imageRefresh.startAnimation(myRotateAnimation);

    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
//        if (arg1 == 1)
//            onRefresh(null);
        // if (arg1 == 2)
        // startActivity(new Intent(getApplicationContext(), MainActivity.class));
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                return getVersionName();
            }

            @Override
            protected void onPostExecute(String version) {
                versionTextView.setText("Version " + version);
                super.onPostExecute(version);
            }
        }.execute();


    }

    private void checkPurchaseFeature() {
        final ArrayList<String> moreSkus = new ArrayList<String>();
        /*
         * Synchronize Purchase feature
         */
        moreSkus.add(GotsPurchaseItem.SKU_PREMIUM);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_PDFHISTORY);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_PARROT);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_50);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_100);

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
                                if (inv.hasPurchase(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_50)) {
                                    gotsPurchase.setFeatureRecognitionCounter(gotsPurchase.getFeatureRecognitionCounter() + 50);
                                    buyHelper.consumeAsync(inv.getPurchase(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_50), new IabHelper.OnConsumeFinishedListener() {
                                        @Override
                                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                                            if (result.isSuccess()) {
                                                Log.i(TAG, "Consume " + purchase.getSku());
                                            } else {
                                                Log.w(TAG, "Error consumming " + purchase.getSku());

                                            }
                                        }
                                    });
                                }
                                if (inv.hasPurchase(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_100)) {
                                    gotsPurchase.setFeatureRecognitionCounter(gotsPurchase.getFeatureRecognitionCounter() + 100);
                                    buyHelper.consumeAsync(inv.getPurchase(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_100), new IabHelper.OnConsumeFinishedListener() {
                                        @Override
                                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                                            if (result.isSuccess()) {
                                                Log.i(TAG, "Consume " + purchase.getSku());
                                            } else {
                                                Log.w(TAG, "Error consumming " + purchase.getSku());

                                            }
                                        }
                                    });
                                }
                                if (inv.hasPurchase(GotsPurchaseItem.SKU_TEST_PURCHASE)) {
                                    gotsPurchase.setFeatureRecognitionCounter(gotsPurchase.getFeatureRecognitionCounter() + 50);
                                    buyHelper.consumeAsync(inv.getPurchase(GotsPurchaseItem.SKU_TEST_PURCHASE), new IabHelper.OnConsumeFinishedListener() {
                                        @Override
                                        public void onConsumeFinished(Purchase purchase, IabResult result) {
                                            if (result.isSuccess()) {
                                                Log.i(TAG, "Consume " + purchase.getSku());
                                            } else {
                                                Log.w(TAG, "Error consumming " + purchase.getSku());

                                            }
                                        }
                                    });
                                }
                                Log.i(TAG, "Successful got inventory!");

                            } else {
                                Log.w(TAG, "Error getting inventory!");
                            }
                        }
                    });

            }
        });

    }

    private String getVersionName() {
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

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccountsByType("gardening-manager");
        if (accounts.length == 0) {
            return null;
        } else {
            if (!gotsPurchase.isPremium() && GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) == ConnectionResult.SUCCESS)
                checkPurchaseFeature();
        }

        return gardenManager.getMyGardens(true);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        super.onNuxeoDataRetrieved(data);
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        Intent intent = new Intent(this, AuthenticationActivity.class);
        intent.putExtra(AuthenticationActivity.ARG_ACCOUNT_TYPE, "gardening-manager");
        intent.putExtra(AuthenticationActivity.ARG_ADD_ACCOUNT, true);
        startActivityForResult(intent, 1);
        super.onNuxeoDataRetrieveFailed();
    }

    @Override
    protected void onPause() {
        imageRefresh.clearAnimation();
        super.onPause();
    }

    @Override
    protected boolean requireFloatingButton() {
        return false;
    }
}
