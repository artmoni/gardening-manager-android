package org.gots.preferences;

public interface GotsServiceAPIKEY {

	public abstract String getAdmobApiKey();

	public abstract String getWeatherApiKey();

	public abstract String getAnalyticsApiKey();

	public abstract void setDEVELOPPEMENT(boolean DEVELOPPEMENT);

	public abstract boolean isDEVELOPPEMENT();

}
