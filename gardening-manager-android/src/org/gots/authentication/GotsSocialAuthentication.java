package org.gots.authentication;

import java.io.IOException;
import java.util.List;

import org.gots.authentication.provider.google.User;

import com.google.android.gms.auth.GoogleAuthException;

public interface GotsSocialAuthentication {

    public abstract List<String> getUserFriends(String accessToken, String userId);

    public abstract String getUserID(String accessToken);

    public abstract String getToken(String accountName) throws GoogleAuthException, IOException ;

    public abstract User getUser(String accessToken);

}
