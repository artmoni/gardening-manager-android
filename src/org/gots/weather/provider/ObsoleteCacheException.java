package org.gots.weather.provider;

public class ObsoleteCacheException extends Exception {
    private static final long serialVersionUID = 1L;

    public ObsoleteCacheException() {
        super("Obsolete file cache");
    }
}
