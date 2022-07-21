package me.familib.apis.modules.HoloAPI;

import me.familib.apis.modules.HoloAPI.handlers.FollowHoloHandler;
import me.familib.apis.modules.HoloAPI.handlers.VisibilityHoloHandler;
import me.familib.apis.modules.HoloAPI.types.holograms.FollowingHologram;
import me.familib.apis.modules.HoloAPI.types.lines.UpdatingLine;
import me.familib.misc.FamiModuleHandler.AModuleHandler;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.Random;

public final class HoloAPI extends AModuleHandler implements Listener {
    Random random = new Random();
    FollowHoloHandler followHandler;
    VisibilityHoloHandler VisibilityHandler;

    @Override
    public String getName() {
        return "HoloAPI";
    }

    @Override
    public double getVersion() {
        return 1.0;
    }

    @Override
    public String getDescription() {
        return "HolographicDisplay API upgraded and buffed";
    }

    @Override
    public boolean canBeDisabled() {
        return true;
    }

    @Override
    protected boolean enable() {
        if(!getPlugin().getServer().getPluginManager().isPluginEnabled("HolographicDisplays"))
            return false;
        if(!getPlugin().getServer().getPluginManager().isPluginEnabled("ProtocolLib"))
            return false;
        this.followHandler = new FollowHoloHandler();
        this.VisibilityHandler = new VisibilityHoloHandler();

        this.followHandler.start();
        this.VisibilityHandler.start();

        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        return true;

    }

    @Override
    protected boolean disable() {
        this.followHandler.stop();
        this.VisibilityHandler.stop();

        PlayerLoginEvent.getHandlerList().unregister(this);
        EntitySpawnEvent.getHandlerList().unregister(this);

        return true;
    }

    public FollowHoloHandler getFollowHandler(){
        return this.followHandler;
    }
    public VisibilityHoloHandler getVisibilityHandler(){return this.VisibilityHandler;}

    // Methods below are there only for testing purposes
    @EventHandler
    public void onConnect(PlayerLoginEvent event){
        FollowingHologram holo = new FollowingHologram(event.getPlayer(), 5, false, false);
        for(int i = 0; i < random.nextInt(10); i++){
            if(random.nextBoolean()) {
                new UpdatingLine(holo.getHologram().appendTextLine("")) {
                    @Override
                    public String update() {
                        return event.getPlayer().getHealth() + "";
                    }
                };
            }else{
                FollowingHologram boomRandom = new FollowingHologram(event.getPlayer(), 5, false, false);
                boomRandom.getHologram().appendTextLine("Randomly created line");
            }
        }
    }
    @EventHandler
    public void onBob(EntitySpawnEvent event){
        FollowingHologram holo = new FollowingHologram(event.getEntity(), 5, false, false);
        for (int i = 0; i < random.nextInt(4); i++){
            if(random.nextBoolean()) {
                new UpdatingLine(holo.getHologram().appendTextLine(""), 5) {
                    @Override
                    public String update() {
                        try {
                            return ((LivingEntity) event.getEntity()).getHealth() + "";
                        } catch (Exception exc) {
                            return "Not Living Entity";
                        }
                    }
                };
            }else {
                FollowingHologram boomRandom = new FollowingHologram(event.getEntity(), 5, false, false);
                boomRandom.getHologram().appendTextLine("Randomly created line");

            }
        }
    }
}
