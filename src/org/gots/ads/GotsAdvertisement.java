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
	private String editorId = "a14f50fa231b26d";

	public GotsAdvertisement(Context mContext) {
		this.mContext = mContext;
	}

	public View getAdsLayout() {
		View convertView;
		Display display = ((Activity) mContext).getWindowManager().getDefaultDisplay();
		int width = display.getWidth();
		int height = display.getHeight();

		AdRequest adRequest = new AdRequest();
		if (GotsPreferences.DEVELOPPEMENT)
			adRequest.addTestDevice(AdRequest.TEST_EMULATOR);

		AdView adView;
		if (width > 500)
			adView = new AdView((Activity) mContext, AdSize.IAB_BANNER, editorId);
		else
			adView = new AdView((Activity) mContext, AdSize.BANNER, editorId);
		
		adView.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));

		adView.loadAd(adRequest);

		convertView = new LinearLayout(mContext);
		((LinearLayout) convertView).addView(adView);
		return convertView;
	}
}
