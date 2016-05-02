package org.gots.help;

import android.content.Context;
import android.util.Log;

import org.gots.context.GotsContext;

import java.util.Locale;

public class HelpUriBuilder {

    public static String getUri(Context context, String page) {
        String lang = Locale.getDefault().getLanguage();
        Log.i("Langue", lang);

        return GotsContext.get(context).getServerConfig().getDocumentationURI() + "/" + lang + "/" + page;
    }
}
