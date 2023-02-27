package me.familib.apis.modules.Trees;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import me.familib.FamiLib;
import me.familib.apis.modules.Trees.misc.TreeBranch;
import me.familib.misc.FamiModuleHandler.AModuleHandler;
import me.familib.misc.FamiModuleHandler.ModuleSettings;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.EventListener;
import java.util.logging.Level;

public final class Trees extends AModuleHandler implements Listener {
    TreeSettings settings = new TreeSettings();

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
        return settings;
    }

    @Override
    protected boolean enable() {
        FamiLib.getInstance().getServer().getPluginManager().registerEvents(this, FamiLib.getFamiLib());

        return true;
    }

    @Override
    protected boolean disable() {
        return true;
    }

    @EventHandler
    public void onJump(AsyncPlayerChatEvent event){
        TreeBranch base = new TreeBranch(-1, Integer.parseInt(event.getMessage()));

        Location startLoc = event.getPlayer().getLocation();

        event.getPlayer().sendMessage("Started");

        new BukkitRunnable(){
            int iterations = 0;
            int end = 50;

            @Override
            public void run() {
                iterations++;

                if(end < 0){
                    cancel();
                }

                if(!base.next()){
                    event.getPlayer().sendMessage("DEBUG | Ended");

                    if(end == 50){
                        base.calculateRadiusForVectors();
                    }

                    base.visualize(startLoc.clone());
                    end--;
                    return;
                }

                if(iterations % 10 == 0){
                    event.getPlayer().sendMessage("DEBUG | " + iterations);
                    base.calculateRadiusForVectors();
                    base.visualize(startLoc.clone());
                }
            }
        }.runTaskTimerAsynchronously(getPlugin(), 10, 10);
    }
}
