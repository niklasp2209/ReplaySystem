package de.bukkitnews.replay.exception;


import org.jetbrains.annotations.NotNull;

public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(@NotNull String message) {
        super(message);
    }
}
