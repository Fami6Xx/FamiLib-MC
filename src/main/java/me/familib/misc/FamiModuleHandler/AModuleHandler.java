package me.familib.misc.FamiModuleHandler;

import me.familib.FamiLib;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public abstract class AModuleHandler {
    private boolean enabled = false;

    /**
     * Gets name of this module
     * @return String of name
     */
    public abstract String getName();

    /**
     * Gets version of this module
     * @return Double representing version
     */
    public abstract double getVersion();

    /**
     * Gets Description of this module
     * @return String of description
     */
    public abstract String getDescription();

    /**
     * If true, module can be safely disabled, otherwise module can't be disabled and module will initialize on start
     * @return Boolean if module can be sabled
     */
    public abstract boolean canBeDisabled();

    /**
     * Gets saved moduleSettings class from specified module. Null if module doesn't have any settings
     * @return ModuleSettings class, which contains primitive data types that can be saved in json. Otherwise null
     */
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
