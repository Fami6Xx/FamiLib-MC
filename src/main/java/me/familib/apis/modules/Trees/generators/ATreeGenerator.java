package me.familib.apis.modules.Trees.generators;

import me.familib.misc.others.AsyncBlock;

import java.util.ArrayList;

public abstract class ATreeGenerator {
    /**
     * Method takes location from where to start and uses it to generate whole tree
     * @return Returns list of AsyncBlocks to be built
     */
    public abstract ArrayList<AsyncBlock> generate(AsyncBlock startBlock);

    public abstract void build(ArrayList<AsyncBlock> blocks);
}
