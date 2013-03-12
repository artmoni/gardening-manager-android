package org.gots.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class GotsPreferences  {
	private static boolean PREMIUM = Boolean.valueOf(System.getProperty("boolean.isdevelopment", "false"));
	// private static final String ANALYTICS_API_KEY = "UA-916500-18";
	private static final String ANALYTICS_API_KEY = System.getProperty("key.analyticsapi", "UA-916500-18");
	private static final String WEATHER_API_KEY = System.getProperty("key.weatherapi",
			"6ba97b2306fd5b9d47992d8716dab16a");
	private static final String ADMOB_API_KEY = System.getProperty("key.admobapi", "a14f50fa231b26d");
	private static final String GARDENING_MANAGER_DIRECTORY = "Gardening-Manager";

	private static final String GARDENING_MANAGER_NUXEO_AUTOMATION_TEST = "http://192.168.100.90:8080/nuxeo/site/automation";
	private static final String GARDENING_MANAGER_NUXEO_AUTOMATION = "http://my.gardening-manager.com/site/automation";
	private static final boolean ISDEVELOPMENT = true;

	private static GotsPreferences preferences;
	private static String NUXEO_LOGIN = "bob";
	private static String NUXEO_PASSWORD = "password";
	

	private static SharedPreferences sharedPreferences;
	private String login;
	private String password;

	public static GotsPreferences getInstance(Context context) {
		if (preferences == null){
			preferences = new GotsPreferences();
			sharedPreferences =context.getSharedPreferences("org.gots.garden", 0);

		}
		return preferences;
	}

	public static boolean isPremium() {
		return PREMIUM;
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

	public static void setPREMIUM(boolean pREMIUM) {
		PREMIUM = pREMIUM;
	}

	public static boolean isDevelopment() {
		return ISDEVELOPMENT;
	}

	

	public static String getNUXEO_LOGIN() {
		return sharedPreferences.getString("org.gots.garden.password", "");
	}

	public static void setNUXEO_LOGIN(String NUXEO_LOGIN) {
		SharedPreferences.Editor prefedit = sharedPreferences.edit();
		prefedit.putString("org.gots.garden.login", NUXEO_LOGIN);
		prefedit.commit();
	}

	public static String getNUXEO_PASSWORD() {

		return sharedPreferences.getString("org.gots.garden.login", "");
	}

	public static void setNUXEO_PASSWORD(String NUXEO_PASSWORD) {
		SharedPreferences.Editor prefedit = sharedPreferences.edit();
		prefedit.putString("org.gots.garden.password", NUXEO_PASSWORD);
		prefedit.commit();
	}

}
