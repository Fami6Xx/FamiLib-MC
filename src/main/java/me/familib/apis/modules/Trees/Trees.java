package me.familib.apis.modules.Trees;

import me.familib.misc.FamiModuleHandler.AModuleHandler;

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
    protected boolean enable() {
        return true;
    }

    @Override
    protected boolean disable() {
        return true;
    }
}
