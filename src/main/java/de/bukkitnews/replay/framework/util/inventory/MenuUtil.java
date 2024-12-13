package de.bukkitnews.replay.framework.util.inventory;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Stack;

public class MenuUtil {

    @Getter
    private final Player player;
    private final HashMap<String, Object> dataMap = new HashMap<>();
    private final Stack<Menu> history = new Stack<>();

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
    public Object getData(@NonNull String identifier) {
        return this.dataMap.get(identifier);
    }

    public Object getData(@NonNull Enum identifier) {
        return this.dataMap.get(identifier.toString());
    }

    public <T> T getData(@NonNull String identifier, @NonNull Class<T> classRef) {

        Object obj = this.dataMap.get(identifier);

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    public <T> T getData(@NonNull Enum identifier, @NonNull Class<T> classRef) {

        Object obj = this.dataMap.get(identifier.toString());

        if (obj == null) {
            return null;
        } else {
            return classRef.cast(obj);
        }
    }

    /**
     * @return Get the previous menu that was opened for the player
     */
    public Menu lastMenu() {
        this.history.pop(); //Makes back() work for some reason
        return this.history.pop();
    }

    public void pushMenu(@NonNull Menu menu) {
        this.history.push(menu);
    }

}