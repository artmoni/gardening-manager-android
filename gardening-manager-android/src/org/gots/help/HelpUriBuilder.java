package org.gots.help;

import java.util.Locale;

import org.gots.context.GotsContext;

import android.content.Context;
import android.util.Log;

public class HelpUriBuilder {

	public static String getUri(Context context, String page) {
		String lang = Locale.getDefault().getLanguage();
		Log.i("Langue",lang);
		
		return GotsContext.get(context).getServerConfig().getDocumentationURI() + "/"+lang+"/" + page;
	}
}
