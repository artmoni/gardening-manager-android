package org.gots.ads;

import org.gots.R;
import org.gots.preferences.GotsPreferences;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class GotsAdvertisement {
    private Context mContext;

    protected String appPackageName = "org.gots.premium";

    GotsPreferences gotsPreferences;

    public GotsAdvertisement(Context mContext) {
        this.mContext = mContext;
        gotsPreferences = GotsPreferences.getInstance().initIfNew(mContext);
    }

    @SuppressWarnings("deprecation")
    public View getAdsLayout() {
        View convertView;
        Display display =  ((Activity)mContext).getWindowManager().getDefaultDisplay();
        int width;
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            width = display.getWidth();
        } else {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }
        final float density = mContext.getResources().getDisplayMetrics().density;
        width = Math.round(((float) width) / density);

        AdRequest adRequest = new AdRequest();
//        if (gotsPurchase.isPremium())
//            adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addKeyword("garden");
        adRequest.addKeyword("potager");
        adRequest.addKeyword("plant");
        adRequest.addKeyword("vegetable");
        adRequest.addKeyword("bio");
        adRequest.addKeyword("ecologique");
        adRequest.addKeyword("ecologic");

        AdView adView;
        if (width >= 936)
            adView = new AdView((Activity) mContext, AdSize.IAB_BANNER, gotsPreferences.getAdmobApiKey());
        else if (width >= 640)
            adView = new AdView((Activity) mContext, AdSize.BANNER, gotsPreferences.getAdmobApiKey());
        else
            adView = new AdView((Activity) mContext, AdSize.SMART_BANNER, gotsPreferences.getAdmobApiKey());

        adView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        adView.setGravity(Gravity.CENTER);
        adView.loadAd(adRequest);

        convertView = new LinearLayout(mContext);
        ((LinearLayout) convertView).addView(adView);
        return convertView;
    }

    public View getPremiumAds(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View ads = inflater.inflate(R.layout.premium_ads, parent);
        return ads;
    }
}
