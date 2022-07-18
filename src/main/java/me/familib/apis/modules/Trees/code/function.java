package me.familib.apis.modules.Trees.code;

import me.familib.FamiLib;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class function {
    private final Material wood;
    private final Material leaves;
    private final int growthMultiplier;
    private final Location startLoc;
    private final Random random = new Random();

    private final int[] branchingCounter = new int[]{0};

    private final ArrayList<Block> growingBlocks = new ArrayList<>();
    private final ArrayList<Block> branchingEnds = new ArrayList<>();
    private final BlockFace[] sideFaces = {BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST};
    private final BlockFace[] allFaces = {BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.DOWN, BlockFace.UP};


    public function(Material wood, Material leaves, Location startLocation){
        this.wood = wood;
        this.leaves = leaves;

        this.startLoc = startLocation;
        this.growthMultiplier = 2;
        random.setSeed(random.nextLong());
    }
    public function(Material wood, Material leaves, Location startLocation, int growthMultiplier){
        this.wood = wood;
        this.leaves = leaves;
        this.growthMultiplier = growthMultiplier;
        this.startLoc = startLocation;
        random.setSeed(random.nextLong());
    }

    private int calculateHeight(){
        int startY = startLoc.getBlockY();
        int heighestY = startY;
        for(Block block : growingBlocks){
            if(block.getY() > heighestY) heighestY = block.getY();
        }
        return heighestY - startY;
    }
    private int calculateHeight(Block block){
        int startY = startLoc.getBlockY();
        int heighestY = block.getY();
        return heighestY - startY;
    }

    private int nearPercentage(Block block){
        int percentage = 0;
        for(BlockFace face : sideFaces){
            if(block.getRelative(face).getType() == wood && growingBlocks.contains(block.getRelative(face)))
                percentage += 25;

        }
        return percentage;
    }


    private void createBase(){
        ArrayList<Block> blocks = new ArrayList<>();
        blocks.add(startLoc.getBlock());
        startLoc.getBlock().setType(wood);
        do{
            if(blocks.size() == 1){
                for(BlockFace face : sideFaces){
                    Block newBlock = startLoc.getBlock().getRelative(face);
                    if(newBlock.getType().isSolid() || newBlock.isEmpty()) {
                        newBlock.setType(wood);
                        blocks.add(newBlock);
                    }
                }
            }else{

                ArrayList<Block> toAdd = new ArrayList<>();
                for (Block block : blocks) {
                    if(block == null) break;
                    for (BlockFace face : sideFaces) {
                        Block newBlock = block.getRelative(face);
                        if(newBlock.getType().isSolid() || newBlock.isEmpty()) {
                            int chance = random.nextInt(100);
                            if (chance <= 67) {
                                newBlock.setType(wood);
                                toAdd.add(newBlock);
                            }
                        }
                    }
                }

                blocks.addAll(toAdd);
            }
        }while (random.nextInt(100 * growthMultiplier) <= 88 * growthMultiplier && blocks.size() < 40 * growthMultiplier);
        growingBlocks.addAll(blocks);
    }

    private ArrayList<Block> getGrowingBlocks(){
        ArrayList<Block> insides = new ArrayList<>();
        final boolean[] first = {true};
        for(Block block : growingBlocks){
            if(first[0]){
                insides.add(block);
                first[0] = false;
                continue;
            }
            if(block == null) continue;
            int nP = nearPercentage(block);
            if(nP <= 25)
                continue;
            if(random.nextInt(99) <= nearPercentage(block))
                insides.add(block);
        }
        return insides;
    }
    private void placeBlocks(ArrayList<Block> arr, Material placeMaterial){
        for(Block block : arr){
            if(block == null)break;
            if(block.getType() != placeMaterial)
                block.setType(placeMaterial);
        }
    }
    private void arrayUP(ArrayList<Block> arr){
        for(int i = 0; i <= arr.size() - 1; i++){
            Block block = arr.get(i);
            if(block == null) continue;
            arr.set(i, block.getLocation().add(0, 1, 0).getBlock());
        }
    }

    private void checkForBranches(){
        for(Block block : growingBlocks){
            if(nearPercentage(block) < 100){
                for(BlockFace face : allFaces){
                    if(!block.getRelative(face).getType().isSolid()){
                        if(face != BlockFace.UP){
                            if(random.nextDouble() < 0.01){
                                branching(face, block.getRelative(face).getLocation(), calculateHeight(block) / 3, face.getOppositeFace());
                            }
                        }
                    }
                }
            }
        }
    }

    private void growLeaves(Block startBlock, int Loops){
        ArrayList<Block> growingLeaves = new ArrayList<>();
        growingLeaves.add(startBlock);
        new BukkitRunnable(){
            int count = 0;
            @Override
            public void run() {
                if(count == Loops){cancel();}
                count++;
                ArrayList<Block> toAdd = new ArrayList<>();

                for(Block block : growingLeaves){
                    if(block == null) continue;
                    for(BlockFace face : allFaces){
                        int rand = random.nextInt(100);
                        if(rand >= 75){
                            continue;
                        }
                        Block newBlock = block.getRelative(face);
                        if(newBlock.getType().isSolid() || newBlock.isEmpty()) {
                            if(random.nextInt(100) >= 75){
                                continue;
                            }
                            newBlock.setType(leaves);

                            toAdd.add(newBlock);
                        }
                    }
                }
                growingLeaves.clear();
                growingLeaves.addAll(toAdd);
            }
        }.runTaskTimer(FamiLib.getFamiLib(), 0L, 5L);
    }
    public void fixGaps(){
        ArrayList<Block> list = (ArrayList<Block>) growingBlocks.clone();
        for(Block block : list){
            for(BlockFace face : sideFaces){
                if(block.getRelative(face).getType() == wood || block.getRelative(face).getType().isSolid()){
                    continue;
                }
                Block fixing = block.getRelative(face);

                if(nearPercentage(fixing) >= 75){
                    fixing.setType(wood);
                    growingBlocks.add(fixing);
                }
            }
        }
    }
    private void branching(BlockFace face, Location startLoc, int maxGrowth, BlockFace tree){
        final Block[] block = {startLoc.getBlock()};
        final Block[] blockBefore = new Block[1];
        final BlockFace notThisFace = tree;
        block[0].setType(wood);
        branchingCounter[0]++;
        new BukkitRunnable(){
            short time = 0;
            @Override
            public void run() {
                if(time > maxGrowth){
                    cancel();
                    branchingEnds.add(block[0]);
                    branchingCounter[0]--;
                }else {
                    time++;
                    if (random.nextBoolean()) {
                        block[0] = block[0].getRelative(BlockFace.UP);
                        block[0].setType(wood);
                        blockBefore[0] = block[0];
                        block[0] = block[0].getRelative(face);
                        block[0].setType(wood);
                    } else {
                        blockBefore[0] = block[0];
                        block[0] = block[0].getRelative(face);
                        block[0].setType(wood);
                    }

                    if (random.nextDouble() <= 0.08) {
                        for (BlockFace faceLoop : sideFaces) {
                            if (block[0].getRelative(faceLoop) == blockBefore[0]) continue;
                            if(faceLoop == notThisFace) continue;
                            if (faceLoop == face) continue;
                            branching(faceLoop, block[0].getRelative(faceLoop).getLocation(), maxGrowth / 3, notThisFace);
                        }
                    }

                    if (random.nextDouble() <= 0.15) {
                        cancel();
                        branchingEnds.add(block[0]);
                        branchingCounter[0]--;
                    }
                }

            }
        }.runTaskTimer(FamiLib.getFamiLib(), 0L, 2L);
    }

    public void startGrowth(){
        createBase();
        new BukkitRunnable(){
            short over = 0;
            short skipped = 1;
            @Override
            public void run() {
                fixGaps();
                ArrayList<Block> blocks = growingBlocks;
                arrayUP(blocks);
                placeBlocks(blocks, wood);

                if(random.nextDouble() >= 0.4 * skipped) {
                    skipped++;
                    return;
                }
                skipped = 0;
                if (blocks.size() != 1) {
                    blocks = getGrowingBlocks();
                    growingBlocks.clear();
                    growingBlocks.addAll(blocks);
                    checkForBranches();
                    return;
                }
                if (over <= 2) {
                    blocks = getGrowingBlocks();
                    growingBlocks.clear();
                    growingBlocks.addAll(blocks);
                    over++;
                    return;
                }

                cancel();
                for(BlockFace face : sideFaces){
                    if(random.nextBoolean()){
                        branching(face, blocks.get(0).getRelative(face).getLocation(), calculateHeight() / 4, null);
                    }
                }
                new BukkitRunnable(){
                    @Override
                    public void run() {
                        if(branchingCounter[0] == 0){
                            for(Block block : branchingEnds){
                                growLeaves(block ,calculateHeight(block) / 5);
                            }
                            for (Block block : growingBlocks){
                                growLeaves(block ,calculateHeight(block) / 4);
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(FamiLib.getFamiLib(), 10L, 10L);
            }
        }.runTaskTimer(FamiLib.getFamiLib(), 0L, 10L);
    }
}
