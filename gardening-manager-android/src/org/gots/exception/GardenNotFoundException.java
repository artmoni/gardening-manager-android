package org.gots.exception;

public class GardenNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    @Override
    public String getMessage() {
        return "No Garden found";
    }
}
