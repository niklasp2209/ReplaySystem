package de.bukkitnews.replay.exception;

import lombok.NonNull;

public class MenuManagerException extends Exception {

    public MenuManagerException() {
        super();
    }

    public MenuManagerException(@NonNull String message) {
        super(message);
    }

    public MenuManagerException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }

    public MenuManagerException(@NonNull Throwable cause) {
        super(cause);
    }
}