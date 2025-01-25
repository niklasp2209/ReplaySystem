package de.bukkitnews.replay.module.replay.menu;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Stack;

public class MenuUtil {

    @Getter
    private final @NotNull Player player;
    private final @NotNull HashMap<String, Object> dataMap = new HashMap<>();
    private final @NotNull Stack<Menu> history = new Stack<>();

    public MenuUtil(@NonNull Player player) {
        this.player = player;
    }

    /**
     * @param identifier A key to store the data by
     * @param data       The data itself to be stored
     */
    public void setData(@NonNull String identifier, @NonNull Object data) {
        this.dataMap.put(identifier, data);
    }

    public void setData(@NonNull Enum identifier, @NonNull Object data) {
        this.dataMap.put(identifier.toString(), data);
    }

    /**
     * @param identifier The key for the data stored in the PMC
     * @return The retrieved value or null if not found
     */
    public @NotNull Object getData(@NonNull String identifier) {
        return this.dataMap.get(identifier);
    }

    public @NotNull Object getData(@NonNull Enum identifier) {
        return this.dataMap.get(identifier.toString());
    }

    public @NotNull <T> T getData(@NonNull String identifier, @NonNull Class<T> classRef) {

        Object obj = this.dataMap.get(identifier);

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    public @NotNull <T> T getData(@NonNull Enum identifier, @NonNull Class<T> classRef) {

        Object obj = this.dataMap.get(identifier.toString());

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    public void pushMenu(@NonNull Menu menu) {
        this.history.push(menu);
    }

}