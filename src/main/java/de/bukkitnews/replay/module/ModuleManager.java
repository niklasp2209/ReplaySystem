package de.bukkitnews.replay.module;

import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.database.DatabaseModule;
import de.bukkitnews.replay.module.replay.ReplayModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * Manages and controls the lifecycle of modules within the HotPotato application.
 * This class ensures that modules can be integrated into the system and removed when necessary.
 */
public final class ModuleManager {

    private final ReplaySystem replaySystem;
    private final LinkedHashMap<Class<? extends CustomModule>, CustomModule> modules;

    public ModuleManager(@NotNull ReplaySystem replaySystem) {
        this.replaySystem = replaySystem;
        this.modules = new LinkedHashMap<>();
    }

    /**
     * Activates all modules in the module list by calling their enable method.
     * This method is responsible for starting all modules in the system.
     */
    public void activateModules() {
        modules.put(DatabaseModule.class, new DatabaseModule(replaySystem));
        modules.put(ReplayModule.class, new ReplayModule(replaySystem));

        loadModules();
    }

    /**
     * Deactivates all modules by calling their stop method.
     * This method removes all modules from the system.
     */
    public void deactivateModules() {
        unloadModules();
    }

    /**
     * Loads all modules into the system by invoking their activation methods.
     */
    private void loadModules() {
        modules.forEach((moduleClass, customModuleInstance) -> {
            customModuleInstance.activate();
            replaySystem.getLogger().info(customModuleInstance.getModuleName() + " module enabled.");
        });
    }

    /**
     * Removes all modules from the system by calling their deactivation methods.
     */
    private void unloadModules() {
        modules.forEach((moduleClass, customModuleInstance) -> {
            customModuleInstance.deactivate();
        });
        modules.clear();
    }

    /**
     * Retrieves a specific module by its class.
     *
     * @param moduleClass The class of the module to be retrieved.
     * @return The requested module if it exists, otherwise null.
     */
    public @NotNull Optional<CustomModule> getModule(@Nullable Class<? extends CustomModule> moduleClass) {
        return Optional.ofNullable(modules.get(moduleClass));
    }
}
