package org.gots.help;

import java.util.Locale;

import android.util.Log;

public class HelpUriBuilder {
	private static String baseHelpURL = "http://www.gardening-manager.com";

	public static String getUri(String page) {
		String lang = Locale.getDefault().getLanguage();
		Log.i("Langue",lang);
		
		return baseHelpURL + "/"+lang+"/" + page;
	}
}
