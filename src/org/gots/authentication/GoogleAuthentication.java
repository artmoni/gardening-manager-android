package org.gots.authentication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;

public class GoogleAuthentication implements GotsSocialAuthentication {
    // private String CLIENT_ID = "473239775303-khctmm26flfc9c3m97ge3uss4ajo8c3r.apps.googleusercontent.com";
    //
    // private String CLIENT_SECRET = "sdxIz8qR2xdIE4FaYb3CZYvz";
    //
    // private String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";

    final String G_PLUS_SCOPE = "https://www.googleapis.com/auth/plus.me";

    final String USERINFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile";

    protected String TAG = "GoogleAuthentication";

    private Context mContext;

    public GoogleAuthentication(Context context) {
        this.mContext = context;
    }

    protected String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                inputStream.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    @Override
    public String getToken(String accountName) throws UserRecoverableAuthException, IOException {
        String token = null;

        final String SCOPE_PREFIX = "oauth2:";
        final String SCOPES = SCOPE_PREFIX + Scopes.PLUS_LOGIN + " " + Scopes.PLUS_PROFILE + " "
                + "https://www.googleapis.com/auth/userinfo.email";

        try {
            token = GoogleAuthUtil.getToken(mContext, accountName, SCOPES);
        } catch (GoogleAuthException e) {
            Log.e(TAG, e.getMessage(), e);
            Toast.makeText(mContext, "Network problem", Toast.LENGTH_LONG).show();;
        }
        Log.d(TAG, "GoogleAuthUtil.getToken=" + token);

        return token;
    }

//    public String getEmail(String accessToken, String userid) {
//        URL url;
//        String login = null;
//        try {
//
//            url = new URL("https://www.googleapis.com/plus/v1/people/" + userid + "?access_token=" + accessToken);
//
//            HttpURLConnection con = (HttpURLConnection) url.openConnection();
//            int serverCode = con.getResponseCode();
//            // successful query
//            if (serverCode == 200) {
//                try {
//                    InputStream is = con.getInputStream();
//                    JSONObject jsonArray;
//                    jsonArray = new JSONObject(convertStreamToString(is));
//
//                    login = (String) jsonArray.getJSONObject("emails").get("value");
//
//                    Log.d(TAG, "Google login = " + login);
//                    is.close();
//                } catch (JSONException e) {
//                    Log.e(TAG, e.getMessage(), e);
//                    return null;
//                }
//                // bad token, invalidate and get a new one
//            } else if (serverCode == 401) {
//                GoogleAuthUtil.invalidateToken(mContext, accessToken);
//                // Log.e(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
//                Log.e(TAG, "Server auth error: ");
//                // unknown error, do something else
//            } else {
//                Log.e(TAG, "Server returned the following error code: " + serverCode);
//
//            }
//        } catch (MalformedURLException e1) {
//            Log.e(TAG, e1.getMessage(), e1);
//        } catch (IOException e1) {
//            Log.e(TAG, e1.getMessage(), e1);
//        }
//        return login;
//    }

    @Override
    public User getUser(String accessToken) {
        URL url;
        User user = new User();
        try {
            url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int serverCode = con.getResponseCode();
            // successful query
            if (serverCode == 200) {
                try {
                    InputStream is = con.getInputStream();
                    JSONObject jsonArray;
                    jsonArray = new JSONObject(convertStreamToString(is));

                    user.setId((String) jsonArray.get("id"));
                    user.setFamily_name((String) jsonArray.get("family_name"));
                    user.setGiven_name((String) jsonArray.get("given_name"));
                    user.setPictureURL((String) jsonArray.get("picture"));
                    user.setLocale((String) jsonArray.get("locale"));
                    user.setName((String) jsonArray.get("name"));
                    user.setGender((String) jsonArray.get("gender"));
//                    user.setEmail(getEmail(accessToken, "me"));
                    Log.d(TAG, "Google User = " + user.getName());
                    is.close();
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }
                // bad token, invalidate and get a new one
            } else if (serverCode == 401) {
                GoogleAuthUtil.invalidateToken(mContext, accessToken);
                // Log.e(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
                Log.e(TAG, "Server auth error: ");
                // unknown error, do something else
            } else {
                Log.e("Server returned the following error code: " + serverCode, null);
            }
        } catch (MalformedURLException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        } catch (IOException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        }
        return user;
    }

    @Override
    public String getUserID(String accessToken) {
        URL url;
        String userID = null;
        try {
            url = new URL("https://www.googleapis.com/oauth2/v1/userinfo?access_token=" + accessToken);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int serverCode = con.getResponseCode();
            // successful query
            if (serverCode == 200) {
                try {
                    InputStream is = con.getInputStream();
                    JSONObject jsonArray;
                    jsonArray = new JSONObject(convertStreamToString(is));
                    userID = (String) jsonArray.get("id");

                    Log.d(TAG, "Google User ID= " + userID);
                    is.close();
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }
                // bad token, invalidate and get a new one
            } else if (serverCode == 401) {
                GoogleAuthUtil.invalidateToken(mContext, accessToken);
                // Log.e(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
                Log.e(TAG, "Server auth error: ");
                // unknown error, do something else
            } else {
                Log.e("Server returned the following error code: " + serverCode, null);
            }
        } catch (MalformedURLException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        } catch (IOException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        }
        return userID;
    }

    /*
     * {
     * "kind": "plus#peopleFeed",
     * "etag": "\"LTv_6IJISeUQGTVXLjMeOtebkoM/TpYBX4SHaUdpT1vFSGaWTPuziFk\"",
     * "title": "Google+ List of Visible People",
     * "nextPageToken": "CGQQ_rSOpJoo",
     * "totalItems": 166,
     * "items": [
     * {
     * "kind": "plus#person",
     * "etag": "\"LTv_6IJISeUQGTVXLjMeOtebkoM/yQDt23lqidObGrf1Slt734cfonM\"",
     * "objectType": "person",
     * "id": "103452520282063622981",
     * "displayName": "Abdelwaheb Didi",
     * "url": "https://profiles.google.com/103452520282063622981",
     * "image": {
     * "url": "https://lh4.googleusercontent.com/-eBavB406LE0/AAAAAAAAAAI/AAAAAAAAAAA/luk3KYRL5Go/photo.jpg?sz=50"
     * }
     * },
     * {
     * "kind": "plus#person",
     * "etag": "\"LTv_6IJISeUQGTVXLjMeOtebkoM/GOPHYmZ5bYn78QroJ7fmYkE76I0\"",
     * "objectType": "person",
     * "id": "101208666708520907297",
     * "displayName": "Adrien Lalanne-Cassou",
     * "url": "https://plus.google.com/101208666708520907297",
     * "image": {
     * "url": "https://lh6.googleusercontent.com/-YPwvQKYzLhc/AAAAAAAAAAI/AAAAAAAAAAA/b1qXz9LNFYQ/photo.jpg?sz=50"
     * }
     * }
     * }
     */
    @Override
    public List<String> getUserFriends(String accessToken, String userId) {

        URL url;
        List<String> friends = new ArrayList<String>();
        try {
            url = new URL("https://www.googleapis.com/plus/v1/people/" + userId + "/people/visible?access_token="
                    + accessToken);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            int serverCode = con.getResponseCode();
            // successful query
            if (serverCode == 200) {
                try {
                    InputStream is = con.getInputStream();
                    JSONArray jsonArray;
                    JSONObject json = new JSONObject(convertStreamToString(is));
                    jsonArray = json.getJSONArray("items");
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jo = jsonArray.getJSONObject(i);
                        String friendName = jo.getString("displayName");
                        friends.add(friendName);

                    }

                    // String name = getFirstName(readResponse(is));
                    Log.d(TAG, "Hello " + friends + "!");
                    is.close();
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    return null;
                }
                // bad token, invalidate and get a new one
            } else if (serverCode == 401) {
                GoogleAuthUtil.invalidateToken(mContext, accessToken);
                // Log.e(TAG, "Server auth error: " + readResponse(con.getErrorStream()));
                Log.e(TAG, "Server auth error: ");
                // unknown error, do something else
            } else {
                Log.e("Server returned the following error code: " + serverCode, null);
            }
        } catch (MalformedURLException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        } catch (IOException e1) {
            Log.e(TAG, e1.getMessage(), e1);
        }
        return friends;
    }
}
