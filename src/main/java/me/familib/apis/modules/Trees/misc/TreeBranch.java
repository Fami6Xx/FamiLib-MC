package me.familib.apis.modules.Trees.misc;

import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class TreeBranch {
    Random random = new Random();
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
     */

    public boolean next(){
        Vector newVector = latestVector.clone();
        vectors.add(latestVector);

        if(!isEnded){
            int chance = random.nextInt(101);

            double randomnessFactor = 1;

            if(chance < 26){
                newVector.add(Vector.getRandom().normalize().multiply(randomnessFactor)).normalize();
            }

            if(chance < 5){
                children.put(vectors.size() ,new TreeBranch(vectors.size(), random.nextLong(), this, newVector.clone().add(Vector.getRandom()).normalize()));
            }

            if(chance < 3){
                isEnded = true;
            }

            children.forEach((num, branch) -> {
                branch.next();
            });

            return true;
        }

        AtomicBoolean edited = new AtomicBoolean(false);
        children.forEach((num, treeBranch) -> {
            if(treeBranch.next()){
                edited.set(true);
            }
        });

        return edited.get();
    }

    public void calculateRadiusForVectors(){
        radii.clear();

        ArrayList<Double> radius = new ArrayList<>();

        for (int i = 0; i < vectors.size(); i++) {
            double length = 0;
            double growthFactor = 1.5;

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
}
