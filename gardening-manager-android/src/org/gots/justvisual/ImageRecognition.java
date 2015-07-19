package org.gots.justvisual;

/**
 * Created by sfleury on 13/07/15.
 */
public class ImageRecognition {
    private static String API_KEY = "sJso7xjF2GO94sA3lWCkwUarL1P3hCWe";
    private static String API_ID = "940";
    private static String SERVER_URL = "garden.vsapi01.com";

    public String getURL(String imageUrl) {
        String url = "http://" + SERVER_URL + "/api-search/by-url?url=" + imageUrl + "&apiid=" + API_ID + "&apikey=" + API_KEY;
        return url;

    }
}
