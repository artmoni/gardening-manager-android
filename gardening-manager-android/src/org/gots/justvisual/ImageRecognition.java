package org.gots.justvisual;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Created by sfleury on 13/07/15.
 */
public class ImageRecognition {

    private static String URL = "http://services.gardening-manager.com/justvisual/";

    public String getURL(String imageUrl) {
        return URL + imageUrl;

    }
}
