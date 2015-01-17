package org.gots.exception;

public class GardenNotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "No Garden found";
    }
}
