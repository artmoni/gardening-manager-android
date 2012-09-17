package org.gots.help;

public class HelpUriBuilder {
	private static String baseHelpURL = "http://www.gardening-manager.com";

	public static String getUri(String page) {
		return baseHelpURL + "/" + page;
	}
}
