package de.bukkitnews.replay.exception;

import de.bukkitnews.replay.module.CustomModule;
import org.jetbrains.annotations.NotNull;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException(@NotNull CustomModule customModule){
        super("Module "+customModule.getModuleName()+" is not enabled.");
    }
}
