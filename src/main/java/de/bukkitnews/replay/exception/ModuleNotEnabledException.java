package de.bukkitnews.replay.exception;

import de.bukkitnews.replay.module.CustomModule;
import lombok.NonNull;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException(@NonNull CustomModule customModule){
        super("Module "+customModule.getModuleName()+" is not enabled.");
    }
}
