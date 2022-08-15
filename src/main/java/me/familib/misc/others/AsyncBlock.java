package me.familib.misc.others;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

public class AsyncBlock {
    final Location location;

    Material material = null;

    public AsyncBlock(Location loc){
        this.location = loc;
    }
    public AsyncBlock(Location loc, Material material){
        this.location = loc;
        this.material = material;
    }

    public void setMaterial(Material material){
        this.material = material;
    }

    public Material getMaterial(){
        return this.material;
    }

    public Location getLocation(){
        return this.location;
    }

    public AsyncBlock getBlockRelative(BlockFace face){
        Block block = location.getBlock().getRelative(face);
        return new AsyncBlock(block.getLocation(), block.getType());
    }
}
