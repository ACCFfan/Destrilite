package com.kittycatmedias.destrilite.world;

import com.kittycatmedias.destrilite.world.block.BlockState;

import java.util.Random;

public abstract class WorldGenerator {

    protected final int width, height;
    protected final long seed;

    protected final Random random;

    public WorldGenerator(long seed, int width, int height){
        this.width = width;
        this.height = height;
        this.seed = seed;
        random = new Random(seed);
    }

    public abstract BlockState[][] generateBlocks();



    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public Random getRandom() {
        return random;
    }

    public long getSeed() {
        return seed;
    }
}
