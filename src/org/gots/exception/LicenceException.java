package org.gots.exception;

public class LicenceException extends Exception {
    @Override
    public String getMessage() {
        return "Premium licence missing";
    }
}
