package org.gots.help;

import java.util.Locale;

import org.gots.preferences.GotsPreferences;

import android.util.Log;

public class HelpUriBuilder {

	public static String getUri(String page) {
		String lang = Locale.getDefault().getLanguage();
		Log.i("Langue",lang);
		
		return GotsPreferences.getInstance().getDocumentationURI() + "/"+lang+"/" + page;
	}
}
