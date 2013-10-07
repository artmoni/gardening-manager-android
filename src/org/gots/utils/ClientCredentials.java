package org.gots.utils;

public class ClientCredentials {

  /** Value of the "API key" shown under "Simple API Access". */
  public static final String KEY = "AIzaSyAtaIJvzohVEECxELeBtNaS2T86diBVX9U";

  public static void errorIfNotSpecified() {
    if (KEY == null) {
      System.err.println("Please enter your API key in " + ClientCredentials.class);
      System.exit(1);
    }
  }
}
