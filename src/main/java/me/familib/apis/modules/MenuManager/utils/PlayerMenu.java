package me.familib.apis.modules.MenuManager.utils;

import org.bukkit.entity.Player;

public class PlayerMenu {
    private final Player player;

    public PlayerMenu(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return player;
    }
}
