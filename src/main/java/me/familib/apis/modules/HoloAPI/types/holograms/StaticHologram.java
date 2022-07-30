package me.familib.apis.modules.HoloAPI.types.holograms;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import me.familib.FamiLib;
import me.familib.apis.modules.HoloAPI.HoloAPI;
import me.familib.misc.IExecuteQueue;
import org.bukkit.Location;

public class StaticHologram extends famiHologram {
    HoloAPI api = (HoloAPI) FamiLib.getInstance().getModuleHandler().getModule("HoloAPI");

    public StaticHologram(Location loc, boolean isVisibleByDefault, double visibleDistance, boolean seeThroughBlocks){
        super(
                HologramsAPI.createHologram(FamiLib.getFamiLib(), loc)
        );

        getHologram().getVisibilityManager().setVisibleByDefault(isVisibleByDefault);
        updateVisibility(visibleDistance, seeThroughBlocks);

        StaticHologram staticHologram = this;

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().addToList(getUUID(), staticHologram)
        );
    }

    @Override
    public Location getBaseLocation(){
        return getHologram().getLocation().clone();
    }

    @Override
    public void destroy() {
        StaticHologram staticHologram = this;

        api.getVisibilityHandler().queue.add(
                () -> api.getVisibilityHandler().removeFromList(getUUID(), staticHologram)
        );
    }
}
