package org.gots.inapp;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

/*
 * This class describe all Playstore inapp Items
 */
public class GotsPurchaseItem {
    public static final int BUY_REQUEST_CODE = 87632;

    private static final String TAG = "GotsPurchaseItem";

    public static String SKU_TEST_PURCHASE = "android.test.purchased";

    public static String SKU_PREMIUM = "gots.premium";

    public static String SKU_FEATURE_PDFHISTORY = "gots.feature.pdfhistory";

    public static String SKU_FEATURE_PARROT = "gots.feature.parrot";

    // public static String SKU_FEATURE_PDFHISTORY = SKU_TEST_PURCHASE;

    private SharedPreferences prefs;

    private Context mContext;

    public GotsPurchaseItem(Context context) {
        mContext = context;
        prefs = mContext.getSharedPreferences("purchaseitem", 0);
    }

    public void setFeatureExportPDF(boolean hasPurchase) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SKU_FEATURE_PDFHISTORY, hasPurchase);
        editor.commit();
    }

    public void setFeatureParrot(boolean hasParrot) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SKU_FEATURE_PARROT, hasParrot);
        editor.commit();
    }

    public void setPremium(boolean hasPurchase) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SKU_PREMIUM, hasPurchase);
        editor.commit();
    }

    public boolean isPremium() {
        return prefs.getBoolean(SKU_PREMIUM, false) ? true : unlockPremium();
    }

    private boolean unlockPremium() {
        boolean unlocked = false;
        PackageManager pm = mContext.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
                if ("org.gots.premium".equals(packageInfo.packageName)) {
                    unlocked = true;
                }
            } catch (NameNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return unlocked;

    }

    public boolean getFeatureExportPDF() {
        return prefs.getBoolean(SKU_FEATURE_PDFHISTORY, false);
    }

    public boolean getFeatureParrot() {
        return prefs.getBoolean(SKU_FEATURE_PARROT, false);
    }
}
