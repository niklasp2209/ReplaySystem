package de.bukkitnews.replay.module;

import de.bukkitnews.replay.ReplaySystem;
import de.bukkitnews.replay.module.database.DatabaseModule;
import de.bukkitnews.replay.module.replay.ReplayModule;

import java.util.LinkedHashMap;

/**
 * Manages and controls the lifecycle of modules within the HotPotato application.
 * This class ensures that modules can be integrated into the system and removed when necessary.
 */
public final class ModuleManager {

    private final ReplaySystem replaySystem;
    private final LinkedHashMap<Class<? extends CustomModule>, CustomModule> modules;

    public ModuleManager(ReplaySystem replaySystem){
        this.replaySystem = replaySystem;
        this.modules = new LinkedHashMap<>();
    }

    /**
     * Activates all modules in the module list by calling their enable method.
     * This method is responsible for starting all modules in the system.
     */
    public void activateModules(){
        this.modules.put(DatabaseModule.class, new DatabaseModule(this.replaySystem));
        this.modules.put(ReplayModule.class, new ReplayModule(this.replaySystem));

        loadModules();
    }

    /**
     * Deactivates all modules by calling their stop method.
     * This method removes all modules from the system.
     */
    public void deactivateModules(){
        unloadModules();
    }

    /**
     * Loads all modules into the system by invoking their activation methods.
     */
    private void loadModules(){
        this.modules.forEach((moduleClass, customModuleInstance) -> {
            customModuleInstance.activate();
            this.replaySystem.getLogger().info(customModuleInstance.getModuleName()+" module enabled.");
        });
    }

    /**
     * Removes all modules from the system by calling their deactivation methods.
     */
    private void unloadModules() {
        this.modules.forEach((moduleClass, customModuleInstance) -> {
            customModuleInstance.deactivate();
        });
        this.modules.clear();
    }

    /**
     * Retrieves a specific module by its class.
     * @param moduleClass The class of the module to be retrieved.
     * @return The requested module if it exists, otherwise null.
     */
    public CustomModule getModule(Class<? extends CustomModule> moduleClass) {
        return this.modules.get(moduleClass);
    }
}
