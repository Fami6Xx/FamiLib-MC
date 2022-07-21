package me.familib.apis.modules.MenuManager;

import me.familib.apis.modules.MenuManager.handlers.MenuInvClickHandler;
import me.familib.apis.modules.MenuManager.utils.PlayerMenu;
import me.familib.misc.FamiModuleHandler.AModuleHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class MenuManager extends AModuleHandler {
    MenuInvClickHandler clickHandler;

    private static final HashMap<Player, PlayerMenu> playerMenuMap = new HashMap<>();

    @Override
    public String getName() {
        return "MenuManager";
    }

    @Override
    public double getVersion() {
        return 1;
    }

    @Override
    public String getDescription() {
        return "Manages Menus created by this module's api";
    }

    @Override
    public boolean canBeDisabled() {
        return false;
    }

    @Override
    protected boolean enable() {
        this.clickHandler = new MenuInvClickHandler();
        getPlugin().getServer().getPluginManager().registerEvents(this.clickHandler, getPlugin());
        return true;
    }

    @Override
    protected boolean disable() {
        InventoryClickEvent.getHandlerList().unregister(this.clickHandler);
        return true;
    }

    public PlayerMenu getPlayerMenu(Player player){
        PlayerMenu playerMenu;
        if (!(playerMenuMap.containsKey(player))) {
            playerMenu = new PlayerMenu(player);
            playerMenuMap.put(player, playerMenu);

            return playerMenu;
        } else {
            return playerMenuMap.get(player);
        }
    }
}
