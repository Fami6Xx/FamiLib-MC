package me.familib;

import me.familib.apis.FamiModuleHandler;
import org.bukkit.plugin.java.JavaPlugin;

public final class FamiLib extends JavaPlugin {
    private FamiModuleHandler moduleHandler;

    @Override
    public void onEnable() {
        // Plugin startup logic
        moduleHandler = new FamiModuleHandler();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        moduleHandler.disableAll();
    }

    public FamiModuleHandler getModuleHandler() {
        return moduleHandler;
    }

    public static FamiLib getInstance() {
        return (FamiLib) getFamiLib();
    }

    public static JavaPlugin getFamiLib(){
        return getProvidingPlugin(FamiLib.class);
    }
}
