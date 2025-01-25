package de.bukkitnews.replay.exception;

import lombok.NonNull;

public class EntityCreationException extends Exception {
    public EntityCreationException(@NonNull String message) {
        super(message);
    }

    public EntityCreationException(@NonNull String message, @NonNull Throwable cause) {
        super(message, cause);
    }
}
