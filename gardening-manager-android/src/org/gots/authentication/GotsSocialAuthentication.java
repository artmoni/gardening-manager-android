package org.gots.authentication;

import android.accounts.Account;

import com.google.android.gms.auth.GoogleAuthException;

import org.gots.authentication.provider.google.User;

import java.io.IOException;
import java.util.List;

public interface GotsSocialAuthentication {

    public abstract List<String> getUserFriends(String accessToken, String userId);

    public abstract String getUserID(String accessToken);

    public abstract String getToken(Account account) throws GoogleAuthException, IOException;

    public abstract User getUser(String accessToken);

}
