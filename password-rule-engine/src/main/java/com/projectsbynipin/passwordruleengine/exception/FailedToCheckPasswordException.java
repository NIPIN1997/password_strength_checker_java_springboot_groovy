package com.projectsbynipin.passwordruleengine.exception;

public class FailedToCheckPasswordException extends RuntimeException {
    public FailedToCheckPasswordException(String message) {
        super(message);
    }
}
