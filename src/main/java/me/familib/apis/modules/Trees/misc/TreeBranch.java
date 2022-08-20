package me.familib.apis.modules.Trees.misc;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Random;

public class TreeBranch {
    Random random = new Random();
    TreeBranch parent;
    ArrayList<TreeBranch> children = new ArrayList<>();
    ArrayList<Vector> vectors = new ArrayList<>();
    Vector latestVector;

    Location startLoc;

    public TreeBranch(Location startLoc, long seed){
        this.startLoc = startLoc;
        latestVector = new Vector(0, 1, 0);

        random.setSeed(seed);

        this.parent = null;
    }

    public TreeBranch(Location startLoc, long seed, TreeBranch parent){
        this.startLoc = startLoc;
        latestVector = new Vector(0, 1, 0);

        random.setSeed(seed);

        this.parent = parent;
    }

    public TreeBranch(Location startLoc, long seed, Vector startingVector){
        this.startLoc = startLoc;
        latestVector = startingVector;

        random.setSeed(seed);

        this.parent = null;
    }

    public TreeBranch(Location startLoc, long seed, TreeBranch parent, Vector startingVector){
        this.startLoc = startLoc;
        latestVector = startingVector;

        random.setSeed(seed);

        this.parent = parent;
    }

    /*
    ToDo:
     - Next function, which will randomize next vector and add latestVector to vectors, also checking for another creation of branch
     - width function, which will take in a vector from arraylist or possibly a index from that list and it will calculate width for that point
     in the tree branch.
     - length function, calculating width of itself
     - maybe a build function?
     - maybe a visualize function to spawn particles in position of vectors
     */

}
