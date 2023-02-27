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

            String state = "";

            int chance = random.nextInt(101);

            double randomnessFactor = settings.randomnessFactor;

            if(chance < settings.randomWayChance){
                newVector.add(randomVector().multiply(randomnessFactor)).normalize();
                state += "Random way, ";
            }

            if(chance < settings.branchChance){
                children.put(vectors.size() ,new TreeBranch(vectors.size(), random.nextLong(), this, newVector.clone().add(randomVector().multiply(randomnessFactor)).normalize()));
                state += "Branched, ";
            }

            if(parent == null){
                System.out.println("--");
                System.out.println("Debug - Chance: " + chance);
                System.out.println("Debug - Needed chance: " + settings.endChance * getNthChild() * settings.endChanceMultiplierByParentNumber);
                if(state.equals("")) state = "Continue";
                System.out.println("Debug - " + state);
                System.out.println("Debug - Current vector: " + latestVector);
                System.out.println("Debug - New vector: " + newVector);
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

    private Vector randomVector(){
        Vector newVector = new Vector(0,0,0);

        if(random.nextBoolean()){
            newVector.setX(random.nextDouble() * -1);
        }else{
            newVector.setX(random.nextDouble());
        }

        if(random.nextBoolean()){
            newVector.setZ(random.nextDouble() * -1);
        }else{
            newVector.setZ(random.nextDouble());
        }

        if(random.nextBoolean()){
            newVector.setY(random.nextDouble() * -1);
        }else{
            newVector.setY(random.nextDouble());
        }

        return newVector;
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

    public ArrayList<Location> getCirclePoints(Location center, Vector direction, double radius, int numPoints) {
        ArrayList<Location> points = new ArrayList<>();
        double increment = 2 * Math.PI / numPoints;
        for (double angle = 0; angle < Math.PI * 2; angle += increment) {
            double x = Math.cos(angle) * radius;
            double z = Math.sin(angle) * radius;
            Vector offset = new Vector(x, 0, z).multiply(direction);
            Location point = center.clone().add(offset);
            points.add(point);
        }
        return points;
    }

    // Implement the calculated radius math formula to show how the branch would be thick
    // have to take in mind that it will be much more vectors to calculate so the thread will be more blocked and therefore slower
    // so maybe implement a setting to change how much vectors will be calculated in the circle, so that it can be decreased /  increased by the customers need

    // Source: https://ciphrd.com/2019/09/11/generating-a-3d-growing-tree-using-a-space-colonization-algorithm/
    // Mesh construction a. Static mesh from our branches
    public void visualize(Location startLoc){
        startLoc.getWorld().spawnParticle(Particle.REDSTONE, startLoc, 1);

        for(int i = 0; i < vectors.size(); i++){
            Vector vector = vectors.get(i);
            startLoc.add(vector);
            startLoc.getWorld().spawnParticle(Particle.REDSTONE, startLoc, 1);

            getCirclePoints(startLoc, vector, radii.get(i), 4).forEach(location -> location.getWorld().spawnParticle(Particle.REDSTONE, location, 1));

            if(children.containsKey(i)){
                TreeBranch branch = children.get(i);
                branch.visualize(startLoc.clone());
            }
        }
    }
}