package me.familib;

import org.bukkit.plugin.java.JavaPlugin;

public final class FamiLib extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static JavaPlugin getFamiLib(){
        return getProvidingPlugin(FamiLib.class);
    }
}
