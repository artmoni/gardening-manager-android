package org.gots.preferences;

public class GotsPreferences implements GotsServiceAPIKEY {
	private static boolean DEVELOPPEMENT = false;
	private static final String ANALYTICS_API_KEY = "UA-916500-18";
	private static final String WEATHER_API_KEY = "6ba97b2306fd5b9d47992d8716dab16a";
	private static final String ADMOB_API_KEY = "a14f50fa231b26d";

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
	public void setDEVELOPPEMENT(boolean dEVELOPPEMENT) {
		DEVELOPPEMENT = dEVELOPPEMENT;
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

}
