package org.gots.utils;

import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;

public class GoogleKeyInitializer implements JsonHttpRequestInitializer {

  private final String key;

  /**
   * @param key API key
   */
  public GoogleKeyInitializer(String key) {
    this.key = key;
  }

  public void initialize(JsonHttpRequest request) {
    request.put("key", key);
  }
}
