package de.bukkitnews.replay.module.replay.menu;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Stack;

public class MenuUtil {

    @Getter
    private final @NotNull Player player;
    private final @NotNull HashMap<String, Object> dataMap = new HashMap<>();
    private final @NotNull Stack<Menu> history = new Stack<>();

    public MenuUtil(@NotNull Player player) {
        this.player = player;
    }

    /**
     * @param identifier A key to store the data by
     * @param data       The data itself to be stored
     */
    public void setData(@NotNull String identifier, @NotNull Object data) {
        dataMap.put(identifier, data);
    }

    public void setData(@NotNull Enum identifier, @NotNull Object data) {
        dataMap.put(identifier.toString(), data);
    }

    /**
     * @param identifier The key for the data stored in the PMC
     * @return The retrieved value or null if not found
     */
    public @NotNull Object getData(@NotNull String identifier) {
        return dataMap.get(identifier);
    }

    public @NotNull Object getData(@NotNull Enum identifier) {
        return dataMap.get(identifier.toString());
    }

    public @NotNull <T> T getData(@NotNull String identifier, @NotNull Class<T> classRef) {

        Object obj = dataMap.get(identifier);

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    public @NotNull <T> T getData(@NotNull Enum identifier, @NotNull Class<T> classRef) {

        Object obj = dataMap.get(identifier.toString());

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    public void pushMenu(@NotNull Menu menu) {
        history.push(menu);
    }

}