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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;

import net.minidev.json.JSONObject;
 
import org.bouncycastle.crypto.digests.SHA256Digest;
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
import android.util.Base64;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;

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

        /*
         * BADGETKIT
         */
        try {

            // Create an HMAC-protected JWS object with some payload
            // final String body =
            // "{\"slug\": \"some-system\", \"name\": \"Some System\", \"url\":\"http://srv3.gardening-manager.com:3000\"}";
            // String payload =
            // "{     key: \"master\",exp: 1393436029, method: \"POST\",path: \"/systems\",  body: { alg: \"sha256\",  hash: "
            // + bodySHA256 + "  }";
            JSONObject jsonbody = new JSONObject();
            jsonbody.put("slug", "some-system");
            jsonbody.put("name", "Some System");
            jsonbody.put("url", "http://srv3.gardening-manager.com:8280");
            String bodySHA256 = SHA256(jsonbody.toString());

            JSONObject body = new JSONObject();
            body.put("alg", "sha256");
            body.put("hash", bodySHA256);
            
            JSONObject jsonHeader = new JSONObject();
            jsonHeader.put("typ", "JWT");
            jsonHeader.put("alg", "HS256");
            
            JSONObject jsonPayload = new JSONObject();
            jsonPayload.put("key", "master");
            jsonPayload.put("exp", "1393436029");
            jsonPayload.put("method", "POST");
            jsonPayload.put("path", "/systems");
            jsonPayload.put("body", body.toString());

            JSONObject json= new JSONObject();
            json.put("secret", "artmonimobile");
            json.put("header", jsonHeader.toString());
            json.put("payload", jsonPayload.toString());
            
            JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.HS256), new Payload(json));
            // We need a 256-bit key for HS256 which must be pre-shared
            byte[] sharedKey = new byte[32];
            new SecureRandom().nextBytes(sharedKey);

            // Apply the HMAC to the JWS object
            jwsObject.sign(new MACSigner(sharedKey));
            String token = jwsObject.serialize();
            Log.i(TAG, json.toString());
            Log.i(TAG, "JWT token=" + token);
        } catch (JOSEException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Serialise to URL-safe format
    }

    public static String SHA256(String text) throws NoSuchAlgorithmException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.update(text.getBytes());
        byte[] digest = md.digest();

        return Base64.encodeToString(digest, Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        // if (arg1 == 1)
        // onRefresh(null);
        // if (arg1 == 2)
        // startActivity(new Intent(getApplicationContext(), MainActivity.class));
        super.onActivityResult(arg0, arg1, arg2);
    }

    @Override
    protected void onResume() {
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
        // AccountManager accountManager = AccountManager.get(this);
        // Account[] accounts = accountManager.getAccountsByType("gardening-manager");
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
            if (!gotsPurchase.isPremium())
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
}
