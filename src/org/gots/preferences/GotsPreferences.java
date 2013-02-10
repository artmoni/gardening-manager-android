package org.gots.preferences;


public class GotsPreferences implements GotsServiceAPIKEY {
	private static final boolean DEVELOPPEMENT = false;
	// private static final String ANALYTICS_API_KEY = "UA-916500-18";
	private static final String ANALYTICS_API_KEY = System.getProperty("key.analyticsapi", "UA-916500-18");
	private static final String WEATHER_API_KEY = System.getProperty("key.weatherapi", "6ba97b2306fd5b9d47992d8716dab16a");
	private static final String ADMOB_API_KEY = System.getProperty("key.admobapi", "a14f50fa231b26d");
	private static final String GARDENING_MANAGER_DIRECTORY="Gardening-Manager";

	private static GotsPreferences preferences;

	public static GotsPreferences getInstance() {
		if (preferences == null)
			preferences = new GotsPreferences();
		return preferences;
	}

	@Override
	public boolean isDEVELOPPEMENT() {
		return DEVELOPPEMENT;
	}

	@Override
	public String getAnalyticsApiKey() {

		return ANALYTICS_API_KEY;
	}

	@Override
	public String getWeatherApiKey() {

		return WEATHER_API_KEY;
	}

	@Override
	public String getAdmobApiKey() {

		return ADMOB_API_KEY;
	}

	public static String getGardeningManagerDirectory() {
		return GARDENING_MANAGER_DIRECTORY;
	}

}
