package me.familib.apis.modules.Trees.code;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Location loc = player.getLocation();
        if(args.length == 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    function function = new function(Material.WOOD, Material.LEAVES, loc);
                    function.startGrowth();

                }
            }.runTaskLater(Bukkit.getPluginManager().getPlugin("Trees"), 25);

        }else{
            try {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        function function = new function(Material.WOOD, Material.LEAVES, loc, Integer.getInteger(args[0]));
                        function.startGrowth();

                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("Trees"), 25);
            }catch (NumberFormatException exc){
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        function function = new function(Material.WOOD, Material.LEAVES, loc);
                        function.startGrowth();

                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("Trees"), 25);
            }
        }
        return true;
    }
}
