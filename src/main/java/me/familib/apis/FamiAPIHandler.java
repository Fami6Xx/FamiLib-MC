package me.familib.apis;

import me.familib.misc.FamiAPIHandler.ModularAPIHandler;

import java.util.ArrayList;
import java.util.Optional;

public class FamiAPIHandler {
    private final ArrayList<ModularAPIHandler> modules = new ArrayList<>();

    public FamiAPIHandler(){
        // Initialize all modules but don't start them
    }

    /**
     * Tries to find and initialize module
     *
     * @param name Name of module you want to initialize
     * @return true if module was successfully enabled or has already been enabled ,or false if the module can't be initialized or wasn't found
     */
    public boolean tryInitialize(String name){
        Optional<ModularAPIHandler> optional =
                modules.stream()
                        .filter(handler -> handler.getName().equals(name))
                        .findFirst();

        if(!optional.isPresent()) return false;

        if(optional.get().isEnabled()) return true;

        optional.get().setEnabled(true);

        return true;
    }

    /**
     * Tries to find and disable module
     *
     * @param name Name of module you want to disable
     * @return true if module was successfully disabled or has already been disabled ,or false if module can't be disabled or wasn't found
     */
    public boolean tryDisabling(String name){
        Optional<ModularAPIHandler> optional =
                modules.stream()
                        .filter(handler -> handler.getName().equals(name))
                        .findFirst();

        if(!optional.isPresent()) return false;

        if(!optional.get().isEnabled()) return true;

        optional.get().setEnabled(false);

        return true;
    }

    /**
     * Finds module you specified
     *
     * @param name Name of module you want to query
     * @return the module you wanted if it finds one, otherwise null
     */
    public ModularAPIHandler getModule(String name){
        Optional<ModularAPIHandler> optional =
                modules.stream()
                    .filter(handler -> handler.getName().equals(name))
                    .findFirst();

        return optional.orElse(null);
    }
}
