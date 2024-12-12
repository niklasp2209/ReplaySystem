package de.bukkitnews.replay.framework.exception;

public class MenuManagerException extends Exception {

    public MenuManagerException() {
        super();
    }

    public MenuManagerException(String message) {
        super(message);
    }

    public MenuManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public MenuManagerException(Throwable cause) {
        super(cause);
    }
}