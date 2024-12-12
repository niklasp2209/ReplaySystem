package de.bukkitnews.replay.framework.exception;

import de.bukkitnews.replay.module.CustomModule;

public class ModuleNotEnabledException extends Exception {

    public ModuleNotEnabledException(CustomModule customModule){
        super("Module "+customModule.getModuleName()+" is not enabled.");
    }
}
