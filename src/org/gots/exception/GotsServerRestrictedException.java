package org.gots.exception;

public class GotsServerRestrictedException extends Exception {
    @Override
    public String getMessage() {
        return "Premium licence missing";
    }
}
