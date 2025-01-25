package de.bukkitnews.replay.exception;

import lombok.NonNull;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(@NonNull String message) {
        super(message);
    }
}
