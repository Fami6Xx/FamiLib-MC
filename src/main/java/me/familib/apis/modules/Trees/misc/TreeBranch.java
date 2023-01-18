package me.familib.apis.modules.Trees.misc;

import me.familib.FamiLib;
import me.familib.apis.modules.Trees.TreeSettings;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;

public class TreeBranch {
    Random random = new Random();
    TreeSettings settings = (TreeSettings) FamiLib.getInstance().getModuleHandler().getModule("Trees").getModuleSettings();
    TreeBranch parent;
    HashMap<Integer, TreeBranch> children = new HashMap<>();
    ArrayList<Vector> vectors = new ArrayList<>();
    ArrayList<Double> radii = new ArrayList<>();
    Vector latestVector;

    int startVectorIndex;

    boolean isEnded = false;

    public TreeBranch(int startVectorIndex, long seed){
        this.startVectorIndex = startVectorIndex;
        latestVector = new Vector(0, 1, 0);

        random.setSeed(seed);

        this.parent = null;
    }

    public TreeBranch(int startVectorIndex, long seed, TreeBranch parent){
        this.startVectorIndex = startVectorIndex;
        latestVector = new Vector(0, 1, 0);

        random.setSeed(seed);

        this.parent = parent;
    }

    public TreeBranch(int startVectorIndex, long seed, Vector startingVector){
        this.startVectorIndex = startVectorIndex;
        latestVector = startingVector;

        random.setSeed(seed);

        this.parent = null;
    }

    public TreeBranch(int startVectorIndex, long seed, TreeBranch parent, Vector startingVector){
        this.startVectorIndex = startVectorIndex;
        latestVector = startingVector;

        random.setSeed(seed);

        this.parent = parent;
    }

    /*
    ToDo:
     - maybe a build function?
     - maybe a visualize function to spawn particles in position of vectors
     - All seeds seem to go to positive x, dont know why
     */

    public boolean next(){

        if(!isEnded){
            Vector newVector = latestVector.clone();
            vectors.add(latestVector);

            int chance = random.nextInt(101);

            double randomnessFactor = settings.randomnessFactor;

            if(chance < settings.randomWayChance){
                newVector.add(Vector.getRandom().multiply(randomnessFactor)).normalize();
            }

            if(chance < settings.branchChance){
                children.put(vectors.size() ,new TreeBranch(vectors.size(), random.nextLong(), this, newVector.clone().add(Vector.getRandom().multiply(randomnessFactor)).normalize()));
            }

            double calculatedChance = settings.randomWayChance * 1.625;
            if(chance <= calculatedChance && parent != null){
                if(newVector.getX() > newVector.getZ() && newVector.getX() > newVector.getY()){
                    if(chance <= calculatedChance / 2){
                        newVector.setZ(newVector.getZ() * -1);
                    }
                    else{
                        newVector.setY(newVector.getY() * -1);
                    }
                }

                if(newVector.getY() > newVector.getZ() && newVector.getY() > newVector.getX()){
                    if(chance <= calculatedChance / 2){
                        newVector.setZ(newVector.getZ() * -1);
                    }
                    else{
                        newVector.setX(newVector.getX() * -1);
                    }
                }

                if(newVector.getZ() > newVector.getX() && newVector.getZ() > newVector.getY()){
                    if(chance <= calculatedChance / 2){
                        newVector.setX(newVector.getX() * -1);
                    }
                    else{
                        newVector.setY(newVector.getY() * -1);
                    }
                }
            }

            if(parent == null){
                System.out.println("Debug - Chance: " + chance);
                System.out.println("Debug - Needed chance: " + settings.endChance * getNthChild() * settings.endChanceMultiplierByParentNumber);
            }
            if(chance < settings.endChance * getNthChild() * settings.endChanceMultiplierByParentNumber){
                isEnded = true;
                vectors.add(newVector);
                System.out.println("Branch ended at vector count " + vectors.size());
            }

            latestVector = newVector;

            ((HashMap<Integer, TreeBranch>)children.clone()).forEach((num, branch) -> {
                branch.next();
            });

            return true;
        }

        AtomicBoolean edited = new AtomicBoolean(false);
        ((HashMap<Integer, TreeBranch>)children.clone()).forEach((num, treeBranch) -> {
            if(treeBranch.next()){
                edited.set(true);
            }
        });

        return edited.get();
    }

    private int getNthChild(){
        int returning = 1;

        TreeBranch parent = this.parent;
        while(parent != null){
            parent = parent.parent;
            returning++;
        }

        return returning;
    }

    public void calculateRadiusForVectors(){
        radii.clear();

        ArrayList<Double> radius = new ArrayList<>();

        for (int i = 0; i < vectors.size(); i++) {
            double length = 0;
            double growthFactor = settings.growthFactor;

            for (int j = i; j < vectors.size(); j++) {
                length += Math.pow(vectors.get(j).length(), growthFactor);
            }

            for (int j = i; j < vectors.size(); j++) {
                if(children.containsKey(j)){
                    TreeBranch branch = children.get(j);

                    length += Math.pow(branch.calculateCompleteLength(), growthFactor);

                    branch.calculateRadiusForVectors();
                }
            }

            if(radius.size() == 0){
                radius.add(Math.pow(length, 1 / growthFactor));
            }else{
                double currentRadius;

                currentRadius = Math.pow(length, 1 / growthFactor);
                currentRadius = (currentRadius + radius.get(radius.size() - 1)) / 2;

                radius.add(currentRadius);
            }
        }

        radii.addAll(radius);
    }

    public double calculateCompleteLength(){
        double length = 0;

        for(int i = 0; i < vectors.size(); i++){
            length += vectors.get(i).length();

            if(children.containsKey(i)){
                TreeBranch branch = children.get(i);
                length += branch.calculateCompleteLength();
            }
        }

        return length;
    }

    // Implement the calculated radius math formula to show how the branch would be thick
    // have to take in mind that it will be much more vectors to calculate so the thread will be more blocked and therefore slower
    // so maybe implement a setting to change how much vectors will be calculated in the circle, so that it can be decreased /  increased by the customers need
    public void visualize(Location startLoc){
        Iterator<Vector> vectorIterator = vectors.iterator();
        startLoc.getWorld().spawnParticle(Particle.REDSTONE, startLoc, 1);
        int index = 0;

        if(this.parent == null){
            FamiLib.getInstance().getServer().getLogger().log(Level.INFO, "DEBUG - Visualization started");
            FamiLib.getInstance().getServer().getLogger().log(Level.INFO, "DEBUG - " + vectors.toString());
        }

        do{
            if(!vectorIterator.hasNext())
                return;

            Vector vector = vectorIterator.next();
            startLoc.add(vector);
            startLoc.getWorld().spawnParticle(Particle.REDSTONE, startLoc, 1);

            if(children.containsKey(index)){
                TreeBranch branch = children.get(index);
                branch.visualize(startLoc.clone());
            }

            index++;
        }while(vectorIterator.hasNext());

    }
}