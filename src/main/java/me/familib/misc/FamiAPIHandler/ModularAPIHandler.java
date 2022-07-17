package me.familib.misc.FamiAPIHandler;

import me.familib.FamiLib;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class ModularAPIHandler {
    private boolean enabled = false;
    public abstract String getName();
    public abstract double getVersion();
    public abstract String getDescription();

    /**
     * Checks if module is enabled or disabled
     * @return true if yes, otherwise false
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * Enables/Disables Module
     */
    public void setEnabled(boolean bool){
        enabled = bool;
        if(bool) enable();
        else disable();
    }

    /**
     * For turning off and on use setEnabled, these functions are there only for modules!!
     */
    protected abstract void enable();

    /**
     * For turning off and on use setEnabled, these functions are there only for modules!!
     */
    protected abstract void disable();

    /**
     * Gets Main class
     */
    public JavaPlugin getPlugin(){
        return FamiLib.getFamiLib();
    }
}
