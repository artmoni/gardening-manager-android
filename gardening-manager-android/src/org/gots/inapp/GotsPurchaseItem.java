package org.gots.inapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import java.util.Calendar;
import java.util.List;

/*
 * This class describe all Playstore inapp Items
 */
public class GotsPurchaseItem {
    public static final int BUY_REQUEST_CODE = 87632;

    private static final String TAG = "GotsPurchaseItem";

    private static final boolean FORCE_PREMIUM = false;
    public static final String SKU_TEST_PURCHASE = "android.test.purchased";

    public static final String SKU_PREMIUM = "gots.premium";

    public static final String SKU_FEATURE_PDFHISTORY = "gots.feature.pdfhistory";

    public static final String SKU_FEATURE_PARROT = "gots.feature.parrot";

    public static final String SKU_FEATURE_RECOGNITION_50 = "gots.feature.recognition.50";

    public static final String SKU_FEATURE_RECOGNITION_100 = "gots.feature.recognition.100";

    public static final String FEATURE_RECOGNITION_COUNTER = "gots.feature.recognition.counter";

    public static final String FEATURE_RECOGNITION_COUNTERDAILY = "gots.feature.recognition.counterdaily";

    public static final String FEATURE_RECOGNITION_LASTDAY = "gots.feature.recognition.lastday";

    public static int RECOGNITION_FREE_COUNTER_MAX = 3;
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

    public void setFeatureRecognitionCounter(int nbRecognitionAllowed) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(FEATURE_RECOGNITION_COUNTER, nbRecognitionAllowed);
        editor.commit();
    }

    public boolean isPremium() {
        return prefs.getBoolean(SKU_PREMIUM, false) ? true : unlockPremium() ? true : FORCE_PREMIUM;
    }

    private boolean unlockPremium() {
        boolean unlocked = false;

        // Premium Licence is installed as app
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

        // AMAZON Publication
        if (FORCE_PREMIUM)
            unlocked = true;
        return unlocked;

    }

    public boolean getFeatureExportPDF() {
        return prefs.getBoolean(SKU_FEATURE_PDFHISTORY, false) || isPremium();
    }

    public boolean getFeatureParrot() {
        return prefs.getBoolean(SKU_FEATURE_PARROT, false) || isPremium();
    }

    public int getFeatureRecognitionCounter() {
        return prefs.getInt(FEATURE_RECOGNITION_COUNTER, 0);
    }

//    public int getFeatureRecognitionFreeCounterMax() {
//        return prefs.getInt(FEATURE_RECOGNITION_COUNTER, 0) + (isPremium() ? 10 : 3);
//    }

    public int getFeatureRecognitionFreeCounter() {
        Calendar cal = Calendar.getInstance();

        int lastDay = prefs.getInt(FEATURE_RECOGNITION_LASTDAY, cal.get(Calendar.DAY_OF_YEAR));
        if (lastDay != cal.get(Calendar.DAY_OF_YEAR) || !prefs.contains(FEATURE_RECOGNITION_COUNTERDAILY)) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FEATURE_RECOGNITION_COUNTERDAILY, RECOGNITION_FREE_COUNTER_MAX);
            editor.putInt(FEATURE_RECOGNITION_LASTDAY, cal.get(Calendar.DAY_OF_YEAR));
            editor.commit();
        }
        return prefs.getInt(FEATURE_RECOGNITION_COUNTERDAILY, 0);
    }


    public void decrementRecognitionDailyCounter() {
        if (getFeatureRecognitionFreeCounter() > 0)
        // setFeatureRecognitionCounter(getFeatureRecognitionCounter() - 1);
        {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(FEATURE_RECOGNITION_COUNTERDAILY, getFeatureRecognitionFreeCounter() - 1);
            editor.commit();
        } else if (getFeatureRecognitionCounter() > 0) {
            setFeatureRecognitionCounter(getFeatureRecognitionCounter() - 1);
        }
    }
}
