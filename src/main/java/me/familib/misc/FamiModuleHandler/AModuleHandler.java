package me.familib.misc.FamiModuleHandler;

import me.familib.FamiLib;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public abstract class AModuleHandler {
    private boolean enabled = false;
    public abstract String getName();
    public abstract double getVersion();
    public abstract String getDescription();
    public abstract boolean canBeDisabled();

    @Nullable
    public abstract ModuleSettings getModuleSettings();

    /**
     * Checks if module is enabled or disabled
     * @return true if yes, otherwise false
     */
    public boolean isEnabled(){
        return enabled;
    }

    /**
     * Enables/Disables Module
     *
     * @param bool true if you want to enable module, false if you want to disable
     * @return true if there was no problem and module changed status, false if module can't be disabled or something went wrong
     */
    public boolean setEnabled(boolean bool){
        if(bool){
            if(enable()){
                enabled = true;
                return true;
            }
        }
        else if(canBeDisabled()){
            if(disable()) {
                enabled = false;
                return true;
            }
        }
        return false;
    }
    /**
     * FORCE Enables/Disables Module
     *
     * @param bool true if you want to enable module, false if you want to disable
     * @param force if set then it skips canBeDisabled() and forcefully tries to disable module (Not recommended!)
     * @return true if there was no problem and module changed status, false if something terrible happened
     */
    public boolean setEnabled(boolean bool, boolean force){
        if(bool){
            if(enable()){
                enabled = true;
                return true;
            }
        }
        else if(disable()) {
            enabled = false;
            return true;
        }
        return false;
    }

    /**
     * For turning off and on use setEnabled, these functions are there only for modules!!
     */
    protected abstract boolean enable();

    /**
     * For turning off and on use setEnabled, these functions are there only for modules!!
     */
    protected abstract boolean disable();

    /**
     * Gets Main class
     */
    public static JavaPlugin getPlugin(){
        return FamiLib.getFamiLib();
    }
}
