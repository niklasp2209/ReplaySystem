package de.bukkitnews.replay.exception;


import org.jetbrains.annotations.NotNull;

public class EntityCreationException extends Exception {
    public EntityCreationException(@NotNull String message) {
        super(message);
    }

    public EntityCreationException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }
}
