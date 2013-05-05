package org.gots.preferences;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class GotsPreferences {

	private static final boolean ISDEVELOPMENT = true;

	private static final String ORG_GOTS_GARDEN_PASSWORD = "org.gots.garden.password";
	private static final String ORG_GOTS_GARDEN_LOGIN = "org.gots.garden.login";
	private static final String ORG_GOTS_GARDEN_SERVERCONNECTED = "org.gots.garden.connected";

	private static boolean ORG_GOTS_PREMIUM_LICENCE = Boolean.valueOf(System.getProperty("boolean.isdevelopment",
			"false"));

	private static final String ANALYTICS_API_KEY = System.getProperty("key.analyticsapi", "UA-916500-18");
	private static final String WEATHER_API_KEY = System.getProperty("key.weatherapi",
			"6ba97b2306fd5b9d47992d8716dab16a");
	private static final String ADMOB_API_KEY = System.getProperty("key.admobapi", "a14f50fa231b26d");
	private static final String GARDENING_MANAGER_DIRECTORY = "Gardening-Manager";

	private static final String GARDENING_MANAGER_NUXEO_AUTOMATION_TEST = "http://192.168.100.90:8080/nuxeo/site/automation";
	// private static final String GARDENING_MANAGER_NUXEO_AUTOMATION =
	// "http://my.gardening-manager.com/site/automation";

	private static final String GARDENING_MANAGER_NUXEO_AUTOMATION = "http://srv2.gardening-manager.com:8090/nuxeo/site/automation";

	private static String OAuthtToken;
	private static GotsPreferences preferences;
	private static SharedPreferences sharedPreferences;

	private Context mContext;

	private GotsPreferences(Context context) {
		mContext = context;
	}

	public static GotsPreferences getInstance(Context context) {
		if (preferences == null) {
			preferences = new GotsPreferences(context);

		}
		return preferences;
	}

	public boolean isPremium() {
		return unlockPremium();
	}

	public static String getAnalyticsApiKey() {
		return ANALYTICS_API_KEY;
	}

	public static String getWeatherApiKey() {
		return WEATHER_API_KEY;
	}

	public static String getAdmobApiKey() {

		return ADMOB_API_KEY;
	}

	public static String getGardeningManagerDirectory() {
		return GARDENING_MANAGER_DIRECTORY;
	}

	public static String getGardeningManagerServerURI() {
		return ISDEVELOPMENT ? GARDENING_MANAGER_NUXEO_AUTOMATION_TEST : GARDENING_MANAGER_NUXEO_AUTOMATION;
	}

//	public void setPREMIUM(boolean pREMIUM) {
//		ORG_GOTS_PREMIUM_LICENCE = pREMIUM;
//	}

	public static boolean isDevelopment() {
		return ISDEVELOPMENT;
	}

	public String getNUXEO_LOGIN() {
		sharedPreferences = mContext.getSharedPreferences("org.gots.garden", 0);

		return sharedPreferences.getString(ORG_GOTS_GARDEN_LOGIN, "");
	}

	public void setNUXEO_LOGIN(String NUXEO_LOGIN) {
		sharedPreferences = mContext.getSharedPreferences("org.gots.garden", 0);

		SharedPreferences.Editor prefedit = sharedPreferences.edit();
		prefedit.putString(ORG_GOTS_GARDEN_LOGIN, NUXEO_LOGIN);
		prefedit.commit();
	}

	public String getNUXEO_PASSWORD() {
		sharedPreferences = mContext.getSharedPreferences("org.gots.garden", 0);

		return sharedPreferences.getString(ORG_GOTS_GARDEN_PASSWORD, "");
	}

	public void setNUXEO_PASSWORD(String NUXEO_PASSWORD) {
		sharedPreferences = mContext.getSharedPreferences("org.gots.garden", 0);

		SharedPreferences.Editor prefedit = sharedPreferences.edit();
		prefedit.putString(ORG_GOTS_GARDEN_PASSWORD, NUXEO_PASSWORD);
		prefedit.commit();
	}

	public void setConnectedToServer(boolean isConnected) {
		sharedPreferences = mContext.getSharedPreferences("org.gots.garden", 0);

		SharedPreferences.Editor prefedit = sharedPreferences.edit();
		prefedit.putBoolean(ORG_GOTS_GARDEN_SERVERCONNECTED, isConnected);
		prefedit.commit();
	}

	public boolean isConnectedToServer() {
		sharedPreferences = mContext.getSharedPreferences("org.gots.garden", 0);
		return sharedPreferences.getBoolean(ORG_GOTS_GARDEN_SERVERCONNECTED, false);
	}

	public String getOAuthtToken() {
		return OAuthtToken;
	}

	public void setOAuthtToken(String oAuthtToken) {
		OAuthtToken = oAuthtToken;
	}
	
	private boolean unlockPremium() {
		boolean unlocked = false;
		PackageManager pm = mContext.getPackageManager();
		List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

		for (ApplicationInfo applicationInfo : packages) {
			try {

				PackageInfo packageInfo = pm
						.getPackageInfo(applicationInfo.packageName, PackageManager.GET_PERMISSIONS);
				if ("org.gots.premium".equals(packageInfo.packageName)) {
					unlocked = true;
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return unlocked;

	}

}
