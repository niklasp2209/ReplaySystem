package de.bukkitnews.replay.exception;

import lombok.NonNull;

public class MenuManagerNotSetupException extends Exception {

    public MenuManagerNotSetupException(@NonNull String message) {
        super(message);
    }
}

