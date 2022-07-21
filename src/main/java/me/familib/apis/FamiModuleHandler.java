package me.familib.apis;

import me.familib.apis.modules.HoloAPI.HoloAPI;
import me.familib.apis.modules.Trees.Trees;
import me.familib.misc.FamiModuleHandler.AModuleHandler;

import java.util.ArrayList;
import java.util.Optional;

public class FamiModuleHandler {
    private final ArrayList<AModuleHandler> modules = new ArrayList<>();

    public FamiModuleHandler(){
        // Initialize all modules and start those, whom are configured to do so

        modules.add(new HoloAPI());
        modules.add(new Trees());
        initialize();
        tryEnabling("Trees");
        tryEnabling("HoloAPI");
    }

    /**
     * Run at start of ModuleHandler, it enables modules which can't be disabled
     */
    private void initialize(){
        modules.forEach(module -> {
            if(!module.canBeDisabled())
                tryEnabling(module.getName());
        });
    }

    public void disableAll(){
        modules.forEach(handler -> {
            if(handler.isEnabled())
                handler.setEnabled(false, true);
        });
    }

    /**
     * Tries to find and enable module
     *
     * @param name Name of module you want to enable
     * @return true if module was successfully enabled or has already been enabled ,or false if the module can't be initialized or wasn't found
     */
    public boolean tryEnabling(String name){
        Optional<AModuleHandler> optional =
                modules.stream()
                        .filter(handler -> handler.getName().equals(name))
                        .filter(AModuleHandler::canBeDisabled)
                        .findFirst();

        if(!optional.isPresent()) return false;

        if(optional.get().isEnabled()) return true;

        return optional.get().setEnabled(true);
    }

    /**
     * Tries to find and disable module
     *
     * @param name Name of module you want to disable
     * @return true if module was successfully disabled or has already been disabled ,or false if module can't be disabled or wasn't found
     */
    public boolean tryDisabling(String name){
        Optional<AModuleHandler> optional =
                modules.stream()
                        .filter(handler -> handler.getName().equals(name))
                        .filter(AModuleHandler::canBeDisabled)
                        .findFirst();

        if(!optional.isPresent()) return false;

        if(!optional.get().isEnabled()) return true;

        return optional.get().setEnabled(false);
    }

    /**
     * Finds module you specified
     *
     * @param name Name of module you want to query
     * @return the module you wanted if it finds one, otherwise null
     */
    public AModuleHandler getModule(String name){
        Optional<AModuleHandler> optional =
                modules.stream()
                    .filter(handler -> handler.getName().equals(name))
                    .findFirst();

        return optional.orElse(null);
    }
}
