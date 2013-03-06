package org.gots.ads;

import org.gots.preferences.GotsPreferences;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class GotsAdvertisement {
	Context mContext;
	public GotsAdvertisement(Context mContext) {
		this.mContext = mContext;
	}

	public View getAdsLayout() {
		View convertView;
		Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		AdRequest adRequest = new AdRequest();
		if (GotsPreferences.getInstance().isDEVELOPPEMENT())
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR);
		adRequest.addKeyword("garden");
		adRequest.addKeyword("potager");
		adRequest.addKeyword("plant");
		adRequest.addKeyword("vegetable");
		

		AdView adView;
		if (width > 500)
			adView = new AdView((Activity) mContext, AdSize.IAB_BANNER, GotsPreferences.getInstance().getAdmobApiKey());
		else
			adView = new AdView((Activity) mContext, AdSize.BANNER, GotsPreferences.getInstance().getAdmobApiKey());
		
		adView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		adView.loadAd(adRequest);

		convertView = new LinearLayout(mContext);
		((LinearLayout) convertView).addView(adView);
		return convertView;
	}
}
