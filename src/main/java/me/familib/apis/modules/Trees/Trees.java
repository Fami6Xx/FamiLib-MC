package me.familib.apis.modules.Trees;

import me.familib.misc.FamiModuleHandler.AModuleHandler;
import me.familib.misc.FamiModuleHandler.ModuleSettings;

import javax.annotation.Nullable;

public final class Trees extends AModuleHandler {
    @Override
    public String getName() {
        return "Trees";
    }

    @Override
    public double getVersion() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Procedurally generates big Tree";
    }

    @Override
    public boolean canBeDisabled() {
        return true;
    }

    @Nullable
    @Override
    public ModuleSettings getModuleSettings() {
        return null;
    }

    @Override
    protected boolean enable() {
        return true;
    }

    @Override
    protected boolean disable() {
        return true;
    }

}
