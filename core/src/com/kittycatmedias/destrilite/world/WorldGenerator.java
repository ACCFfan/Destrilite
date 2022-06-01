package com.kittycatmedias.destrilite.world;

import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;
import sun.jvm.hotspot.opto.Block;

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

    protected void smooth(BlockState[][] blocks, int iterations) {
        BlockType[][] newTypes = new BlockType[width][height], oldTypes = new BlockType[width][height];
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                oldTypes[x][y] = blocks[x][y].getType();
            }
        for (int i = 0; i < iterations; i++) {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                        newTypes[x][y] = oldTypes[x][y];
                    } else {
                        int t = (oldTypes[x - 1][y - 1] == BlockType.AIR ? 0 : 1) + (oldTypes[x - 1][y] == BlockType.AIR ? 0 : 1) + (oldTypes[x - 1][y + 1] == BlockType.AIR ? 0 : 1) + (oldTypes[x][y - 1] == BlockType.AIR ? 0 : 1) + (oldTypes[x][y + 1] == BlockType.AIR ? 0 : 1) + (oldTypes[x + 1][y - 1] == BlockType.AIR ? 0 : 1) + (oldTypes[x + 1][y] == BlockType.AIR ? 0 : 1) + (oldTypes[x + 1][y + 1] == BlockType.AIR ? 0 : 1);
                        BlockType type = oldTypes[x][y];
                        if (t > 4) type = BlockType.DIRT;
                        if (t < 4) type = BlockType.AIR;
                        newTypes[x][y] = type;
                    }
                }
            oldTypes = newTypes;
        }
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                blocks[x][y].setType(newTypes[x][y]);
            }
    }

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

    protected boolean inRange(int x, int y) {return x>=0&&x<width&&y>=0&&y<height;}
}
