package org.gots.ads;

import org.gots.R;
import org.gots.preferences.GotsPreferences;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class GotsAdvertisement {
    Context mContext;

    protected String appPackageName = "org.gots.premium";

    public GotsAdvertisement(Context mContext) {
        this.mContext = mContext;
    }

    public View getAdsLayout() {
        View convertView;
        Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
        // int width = display.getWidth();
        // int height = display.getHeight();

        final float density = mContext.getResources().getDisplayMetrics().density;
        int width = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        width = Math.round(((float) width) / density);

        AdRequest adRequest = new AdRequest();
        if (GotsPreferences.getInstance().isPremium())
            adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
        adRequest.addKeyword("garden");
        adRequest.addKeyword("potager");
        adRequest.addKeyword("plant");
        adRequest.addKeyword("vegetable");

        AdView adView;
        if (width >= 936)
            adView = new AdView((Activity) mContext, AdSize.IAB_BANNER, GotsPreferences.getAdmobApiKey());
        else if (width >= 640)
            adView = new AdView((Activity) mContext, AdSize.BANNER, GotsPreferences.getAdmobApiKey());
        else
            adView = new AdView((Activity) mContext, AdSize.SMART_BANNER, GotsPreferences.getAdmobApiKey());

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
        ads.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                GoogleAnalyticsTracker.getInstance().trackPageView(GotsAdvertisement.class.getSimpleName() + "Premium");
                GoogleAnalyticsTracker.getInstance().dispatch();

                Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName));
                marketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                mContext.startActivity(marketIntent);
            }
        });
        return ads;
    }
}
