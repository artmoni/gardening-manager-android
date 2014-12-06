/* *********************************************************************** *
 * project: org.gots.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : contact at gardening-manager dot com                  *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   See also COPYING, LICENSE and WARRANTY file
 *   Contributors:                                                         *
 *                - Sebastien FLEURY                                       *
 *                                                                         *
 * *********************************************************************** */
package org.gots.preferences;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.nuxeo.android.config.NuxeoServerConfig;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class GotsPreferences extends NuxeoServerConfig {

    public GotsPreferences(Context androidContext) {
        super(androidContext);
        initIfNew(androidContext);
    }

    private static final String TAG = "GotsPreferences";

    public static final boolean ISDEVELOPMENT = false;

    public static final boolean DEBUG = false;

    Account userAccount;

    // /**
    // * @see NuxeoServerConfig#PREF_SERVER_PASSWORD
    // */
    // public static final String ORG_GOTS_GARDEN_PASSWORD =
    // "org.gots.garden.password";
    public static final String ORG_GOTS_GARDEN_PASSWORD = NuxeoServerConfig.PREF_SERVER_PASSWORD;

    // public static final String ORG_GOTS_GARDEN_NUXEO_URI =
    // "org.gots.garden.nuxeo.uri";
    public static final String ORG_GOTS_GARDEN_NUXEO_URI = NuxeoServerConfig.PREF_SERVER_URL;

    // /**
    // * @see NuxeoServerConfig#PREF_SERVER_LOGIN
    // */
    // public static final String ORG_GOTS_GARDEN_LOGIN =
    // "org.gots.garden.login";
    public static final String ORG_GOTS_GARDEN_LOGIN = NuxeoServerConfig.PREF_SERVER_LOGIN;

    public static final String ORG_GOTS_GARDEN_SUCCESSFUL_LOGIN = "nuxeo.successful.login";

    public static final String ORG_GOTS_GARDEN_SERVERCONNECTED = "org.gots.garden.connected";

    public static final String ORG_GOTS_GARDEN_DEVICEID = "org.gots.garden.deviceid";

    // /**
    // * @see NuxeoServerConfig#PREF_SERVER_TOKEN
    // */
    // public static final String ORG_GOTS_GARDEN_TOKEN =
    // "org.gots.garden.token";
    public static final String ORG_GOTS_GARDEN_TOKEN = NuxeoServerConfig.PREF_SERVER_TOKEN;

    public static final String ORG_GOTS_PARROT_TOKEN = "parrot.auth.token";

    public static final String ORG_GOTS_PARROT_LOGIN = "parrot.login";

    // private String ANALYTICS_API_KEY = "UA-916500-18";

    // private static final String WEATHER_API_KEY = "";

    // private static final String ADMOB_API_KEY = "a14f50fa231b26d";

    public final String GARDENING_MANAGER_DIRECTORY = "Gardening-Manager";

    public static final String GARDENING_MANAGER_APPNAME = "Gardening Manager";

    private static final String GARDENING_MANAGER_DOCUMENTATION_URL = "http://doc.gardening-manager.com";

    private static final String GARDENING_MANAGER_NUXEO_AUTOMATION_TEST = "http://services.gardening-manager.com/test/";

    private static final String GARDENING_MANAGER_NUXEO_AUTOMATION = "http://services.gardening-manager.com/nuxeo/";
   // private static final String GARDENING_MANAGER_NUXEO_AUTOMATION = "http://192.168.43.143:8080/nuxeo/";

    public static final String ORG_GOTS_CURRENT_GARDENID = "org.gots.preference.gardenid";

    public static final String ORG_GOTS_CURRENT_ALLOTMENT = "org.gots.preference.allotmentid";

    protected SharedPreferences sharedPreferences;

    protected Context mContext;

    private static GotsPreferences instance = null;

    private static Exception firstCall;

    private boolean initDone = false;

    public static final String URL_TWITTER_GARDENING_MANAGER = "https://twitter.com/GardeningApp";

    public static final String URL_FACEBOOK_GARDENING_MANAGER = "http://www.facebook.com/pages/Gardening-Manager/120589404779871";

    public static final String URL_GOOGLEPLUS_GARDENING_MANAGER = "https://plus.google.com/u/0/b/108868805153744305734/communities/105269291264998461912";

    public static final String URL_SAUTERDANSLESFLAQUES = "http://www.sauterdanslesflaques.com";

    public static final String URL_ARTMONI = "http://www.artmoni.eu";

    public static final String URL_TRANSLATE_GARDENING_MANAGER = "http://translate.gardening-manager.com";

    private Properties properties = new Properties();

    public static final String SYNC_SCHEDULE = "sync.schedule";

    private static final String ACCOUNT_TYPE = "gardening-manager";

    // private static final String PUBKEY =
    // "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtAFVYGad4FaKIZ9A0W2JfMh+B1PQMU+tal9B0XYbEJdZy6UCwqoH42/YLDn0GTjKA+ozAZJtaQqoU/ew95tYKEYszj067HfVehpRtKxLlySFMnqdai0SuGyl5EI4QQovsw3wFU1ihELWBaCg2CcTJqk1jXcWaxsqPPPWty5tAcMwQDWZ0cw6uw8QddztiKlw5IB1XTWdhZTuPL/RcR0Ns+lbEB2kdosozekXr+dRqZ4+PKyHn+j8/407hb76gqn9CmrGhOsJ3E7aOVRCZWZ9nf6aJfFYJP5JY/QHsa+9OsiSj8QXS2vic3ay+MazF09bteN7Wnb15Y9CBK/sM2RAqQIDAQAB";

//    private GotsPreferences() {
//    }

//    /**
//     * After first call, {@link #initIfNew(Context)} must be called else a {@link NotConfiguredException} will be thrown
//     * on the second call attempt.
//     */
//    public static synchronized GotsPreferences getInstance() {
//        if (instance == null) {
//            instance = new GotsPreferences();
//            firstCall = new Exception();
//        } else if (!instance.initDone) {
//            throw new NotConfiguredException(firstCall);
//        }
//        return instance;
//    }

    public void reset() {
        initDone = false;
    }

    /**
     * If it was already called once, the method returns without any change.
     */
    public synchronized GotsPreferences initIfNew(Context context) {
        if (!initDone) {
            mContext = context;
            setSharedPreferences(PreferenceManager.getDefaultSharedPreferences(context));
            InputStream propertiesStream = null;
            try {
                propertiesStream = mContext.getResources().openRawResource(R.raw.config);
                properties.load(propertiesStream);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
            setGardeningManagerServerURI(ISDEVELOPMENT ? GARDENING_MANAGER_NUXEO_AUTOMATION_TEST : GARDENING_MANAGER_NUXEO_AUTOMATION);
            initDone = true;
        }
        return this;
    }

    public SharedPreferences getSharedPrefs() {
        return sharedPreferences;
    }

    protected void setSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        // initFromPrefs(sharedPreferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (ORG_GOTS_GARDEN_SERVERCONNECTED.equals(key)) {
            mContext.sendBroadcast(new Intent(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
            Log.d(TAG, key + " has changed: " + isConnectedToServer());
        } else if (ORG_GOTS_CURRENT_GARDENID.equals(key)) {
            mContext.sendBroadcast(new Intent(BroadCastMessages.GARDEN_SETTINGS_CHANGED));
            Log.d(TAG, key + " has changed: " + getCurrentGardenId());
        }
        // initFromPrefs(prefs);
    }

    protected void initFromPrefs(SharedPreferences prefs) {
        // if need to store some values instead of read from pref each time
    }

    public void set(String key, String value) {
        SharedPreferences.Editor prefedit = sharedPreferences.edit();
        prefedit.putString(key, value);
        prefedit.commit();
        Log.d(TAG, key + "=" + value);
    }

    public void set(String key, long value) {
        SharedPreferences.Editor prefedit = sharedPreferences.edit();
        prefedit.putLong(key, value);
        prefedit.commit();
        Log.d(TAG, key + "=" + value);
    }

    public void set(String key, int value) {
        SharedPreferences.Editor prefedit = sharedPreferences.edit();
        prefedit.putInt(key, value);
        prefedit.commit();
        Log.d(TAG, key + "=" + value);
    }

    public void set(String key, boolean value) {
        SharedPreferences.Editor prefedit = sharedPreferences.edit();
        prefedit.putBoolean(key, value);
        prefedit.commit();
        Log.d(TAG, key + "=" + value);
    }

    public String get(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public long get(String key, long defValue) {
        return sharedPreferences.getLong(key, defValue);
    }

    public int get(String key, int defValue) {
        return sharedPreferences.getInt(key, defValue);
    }

    public boolean get(String key, boolean defValue) {
        return sharedPreferences.getBoolean(key, defValue);
    }

    public String getAnalyticsApiKey() {
        return properties.getProperty("analytics.apikey");
    }

    public String getWeatherApiKey() {
        return properties.getProperty("previmeteo.apikey");
    }

    public String getAdmobApiKey() {

        return properties.getProperty("admob.apikey");
    }

    public void setGardeningManagerServerURI(String uri) {
        if (!uri.endsWith("/")) {
            uri = uri + "/";
        }
        Log.d(TAG, "setGardeningManagerServerURI " + uri);
        set(ORG_GOTS_GARDEN_NUXEO_URI, uri);
    }

    public String getGardeningManagerServerURI() {
        String url = sharedPreferences.getString(ORG_GOTS_GARDEN_NUXEO_URI, "");
        Log.d(TAG, "return " + url);
        return url;
    }

    // public void setPREMIUM(boolean pREMIUM) {
    // ORG_GOTS_PREMIUM_LICENCE = pREMIUM;
    // }

    public static boolean isDevelopment() {
        return ISDEVELOPMENT;
    }

    public String getNuxeoLogin() {
        return sharedPreferences.getString(ORG_GOTS_GARDEN_LOGIN, "guest");
    }

    public void setNuxeoLogin(String login) {
        set(ORG_GOTS_GARDEN_LOGIN, login);
    }

    public String getNuxeoPassword() {
        return sharedPreferences.getString(ORG_GOTS_GARDEN_PASSWORD, "");
    }

    public void setNuxeoPassword(String password) {
        set(ORG_GOTS_GARDEN_PASSWORD, password);
    }

    public void setConnectedToServer(boolean isConnected) {
        set(ORG_GOTS_GARDEN_SERVERCONNECTED, isConnected);
    }

    public boolean isConnectedToServer() {
        return sharedPreferences.getBoolean(ORG_GOTS_GARDEN_SERVERCONNECTED, false) && !"guest".equals(getNuxeoLogin());
    }

    public String getToken() {
        return sharedPreferences.getString(ORG_GOTS_GARDEN_TOKEN, "");
    }

    public void setToken(String token) {
        set(ORG_GOTS_GARDEN_TOKEN, token);
    }

    public void setScheduleTimeForNotification(long timeinsec) {
        set(SYNC_SCHEDULE, timeinsec);
    }

    public long getScheduleTimeForNotification() {
        return Long.valueOf(sharedPreferences.getString(SYNC_SCHEDULE, "1"));

    }

    public String getDeviceId() {
        return get(ORG_GOTS_GARDEN_DEVICEID, "");
    }

    public void setDeviceId(String device_id) {
        set(ORG_GOTS_GARDEN_DEVICEID, device_id);
    }

    public String getGardeningManagerAppname() {
        return GARDENING_MANAGER_APPNAME;
    }

    public String getNuxeoAuthenticationURI() {
        return getGardeningManagerServerURI() + "authentication/token?";
    }

    public String getNuxeoAutomationURI() {
        return getGardeningManagerServerURI() + "site/automation";
    }

    public int getCurrentGardenId() {
        SharedPreferences preferences = mContext.getSharedPreferences("org.gots.preference", 0);
        int oldGardenId = preferences.getInt("org.gots.preference.gardenid", -1);
        if (oldGardenId > -1) {
            set(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, oldGardenId);
            SharedPreferences.Editor prefedit = preferences.edit();
            prefedit.putInt("org.gots.preference.gardenid", -1);
            prefedit.commit();
        }
        return get(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, -1);
    }

    public String getLastSuccessfulNuxeoLogin() {
        return get(ORG_GOTS_GARDEN_SUCCESSFUL_LOGIN, "");
    }

    public void setLastSuccessfulNuxeoLogin(String login) {
        set(ORG_GOTS_GARDEN_SUCCESSFUL_LOGIN, login);
    }

    public String getDocumentationURI() {
        return GARDENING_MANAGER_DOCUMENTATION_URL;
    }

    public File getGotsExternalFileDir() {
        return mContext.getExternalFilesDir(GARDENING_MANAGER_DIRECTORY);
    }

    public String getPlayStorePubKey() {

        return properties.getProperty("playstore.pubkey");
    }

    public Account getUserAccount() {
        // AccountManager accountManager = AccountManager.get(mContext);
        // Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        // if (accounts.length > 0)
        // return accounts[0];

        AccountManager mAccountManager = AccountManager.get(mContext);
        final Account account = new Account(getNuxeoLogin(), ACCOUNT_TYPE);
        mAccountManager.addAccountExplicitly(account, getNuxeoPassword(), null);

        return account;
    }

    public void setUserAccount(Account userAccount) {
        this.userAccount = userAccount;
    }

    public void setParrotToken(String token) {
        set(ORG_GOTS_PARROT_TOKEN, token);
    }

    public String getParrotToken() {
        return get(ORG_GOTS_PARROT_TOKEN, null);
    }

}
