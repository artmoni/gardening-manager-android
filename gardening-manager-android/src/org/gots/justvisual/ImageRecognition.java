package org.gots.justvisual;

/**
 * Created by sfleury on 13/07/15.
 */
public class ImageRecognition {

    private static String URL = "http://services.gardening-manager.com/justvisual/";

    public String getURL(String imageUrl) {
        return URL + imageUrl;

    }
}
