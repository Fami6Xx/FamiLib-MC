package me.familib.apis.modules.HoloAPI.handlers;

import com.gmail.filoghost.holographicdisplays.api.VisibilityManager;
import me.familib.FamiLib;
import me.familib.apis.modules.HoloAPI.types.holograms.famiHologram;
import me.familib.misc.RayCast.RayCast;
import me.familib.misc.RayCast.RayCastResult;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class VisibilityHoloHandler extends famiHoloHandler {

    @Override
    public BukkitTask startTask() {
        return new BukkitRunnable(){
            private final HashMap<UUID, List<Player>> hashMap = new HashMap<>();
            private List<Player> createList(UUID uuid){
                List<Player> l = new ArrayList<>();
                hashMap.put(uuid, l);
                return l;
            }

            public List<Player> getList(UUID uuid) {
                return hashMap.get(uuid) == null ? createList(uuid) : hashMap.get(uuid);
            }
            public void removeList(UUID uuid){
                hashMap.remove(uuid);
            }
            public void updateList(UUID uuid, List<Player> list){
                hashMap.replace(uuid, list);
            }


            public boolean checkConditions(Player player, famiHologram holo){
                boolean returnValue = true;

                Vector playerVector = player.getEyeLocation().toVector();
                Vector entityVector = holo.getBaseLocation().toVector();
                Vector vector = playerVector.clone().subtract(entityVector.clone());

                if(!holo.canSeeThroughBlocks()){
                    Location startLoc = holo.getBaseLocation();

                    RayCastResult result = new RayCast(vector, startLoc.getWorld(), startLoc, player.getEyeLocation(), holo.getDistance(), 0.1)
                            .enableIgnoreSeeThroughMaterials()
                            .shoot();

                    if(result.hasHit())
                        returnValue = false;
                }
                return returnValue;
            }
            @Override
            public void run() {
                // Checking queue and if there is something then executing it safely in this thread
                handleQueue();

                ((HashMap<UUID, ArrayList<famiHologram>>) getMap().clone()).forEach(((uuid, famiHolograms) -> {
                    famiHologram[] arr = famiHolograms.toArray(new famiHologram[0]);

                    for(famiHologram holo : arr) {
                        if (holo.getHologram().isDeleted()) {
                            removeFromList(uuid, holo);
                            removeList(holo.getUUID());
                            continue;
                        }

                        if (!holo.getHologram().getVisibilityManager().isVisibleByDefault()) {
                            VisibilityManager manager = holo.getHologram().getVisibilityManager();

                            List<Player> prevVisible = getList(holo.getUUID());
                            Collection<Player> nowVisible =
                                    !manager.isVisibleByDefault() ?
                                            holo.getHologram().getLocation().getNearbyPlayers(holo.getDistance())
                                            :
                                            holo.getHologram().getLocation().getNearbyPlayers(Bukkit.getViewDistance() * 16);

                            // Hide to everyone who left visible distance or doesn't pass conditions
                            prevVisible.forEach(player -> {
                                if (!nowVisible.contains(player)) {
                                    manager.hideTo(player);
                                }
                                if (!checkConditions(player, holo)) {
                                    manager.hideTo(player);
                                }
                            });

                            // Show hologram to players who don't see it but passed conditions
                            nowVisible.stream()
                                    .filter(player -> checkConditions(player, holo))
                                    .filter(player -> !manager.isVisibleTo(player))
                                    .forEach(manager::showTo);

                            // Filter everyone who doesn't see the hologram and collect them
                            List<Player> finalList =
                                    nowVisible.stream()
                                            .filter(manager::isVisibleTo)
                                            .collect(Collectors.toList());

                            updateList(holo.getUUID(), finalList);
                        }
                    }
                }));
            }
        }.runTaskTimerAsynchronously(FamiLib.getFamiLib(), 0L, 1L);
    }
}
