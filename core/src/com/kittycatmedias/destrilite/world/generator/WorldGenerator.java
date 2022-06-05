package com.kittycatmedias.destrilite.world.generator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;
import com.kittycatmedias.destrilite.world.generator.generatortype.Grasslands;

import java.util.Random;

public abstract class WorldGenerator {

    private int ID;

    private static Array<WorldGenerator> generators = new Array<>();
    private static int nextID = 0;

    public static WorldGenerator GRASSLANDS = addGenerator(new Grasslands());

    public WorldGenerator(){
    }

    private static WorldGenerator addGenerator(WorldGenerator generator){
        generator.ID = nextID++;
        generators.add(generator);
        return generator;
    }

    public static WorldGenerator getGenerator(int id){
        return generators.get(id);
    }

    public int getID() {
        return ID;
    }

    public abstract BlockState[][] generateBlocks(Random random);

    protected void smooth(BlockState[][] blocks, int iterations) {
        int width = blocks.length, height = blocks[0].length;
        BlockType[][] newTypes = new BlockType[width][height], oldTypes = new BlockType[width][height];
        WallType[][] newWalls = new WallType[width][height], oldWalls = new WallType[width][height];

        ObjectMap<BlockType, Integer> blockAmnt = new ObjectMap<>();
        ObjectMap<WallType, Integer> wallAmnt = new ObjectMap<>();

        BlockType type, tMax;
        WallType wall, wMax;

        int t, w, a, wM, tM, aM;

        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                oldTypes[x][y] = blocks[x][y].getType();
                oldWalls[x][y] = blocks[x][y].getWall();
            }
        for (int i = 0; i < iterations; i++) {
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    blockAmnt.clear();
                    wallAmnt.clear();
                    type = oldTypes[x][y];
                    wall = oldWalls[x][y];
                    t = 0;
                    w = 0;
                    a = 0;

                    for (int x2 = -1; x2 < 2; x2++)
                        for (int y2 = -1; y2 < 2; y2++) {
                            if (x2 != x && y2 != y && inRange(x + x2, y + y2, width, height)) {
                                a++;
                                if(!oldTypes[x+x2][y+y2].isAir()){
                                    t++;
                                    blockAmnt.put(oldTypes[x+x2][y+y2], blockAmnt.get(oldTypes[x+x2][y+y2], 0) + 1);
                                }
                                if(!oldWalls[x+x2][y+y2].isAir()){
                                    w++;
                                    wallAmnt.put(oldWalls[x+x2][y+y2], wallAmnt.get(oldWalls[x+x2][y+y2], 0) + 1);
                                }
                            }
                        }
                    if(t > a/2){
                        tM = 0;
                        tMax = BlockType.AIR;
                        for(BlockType ty : blockAmnt.keys()){
                            aM = blockAmnt.get(ty, -1);
                            if(aM >= tM/* || (aM == tM && random.nextBoolean())*/)tMax = ty;
                        }
                        type = tMax;
                    }else type = BlockType.AIR;
                    if(w > a/2){
                        wM = 0;
                        wMax = WallType.AIR;
                        for(WallType wa : wallAmnt.keys()){
                            aM = wallAmnt.get(wa, -1);
                            if(aM >= wM/* || (aM == wM && random.nextBoolean())*/)wMax = wa;
                        }
                        wall = wMax;
                    }else wall = WallType.AIR;
                    newTypes[x][y] = type;
                    newWalls[x][y] = wall;
                }
            for (int x = 0; x < width; x++)
                for (int y = 0; y < height; y++) {
                    oldTypes[x][y] = newTypes[x][y];
                    oldWalls[x][y] = newWalls[x][y];
                }
        }
        for (int x = 0; x < width; x++)
            for (int y = 0; y < height; y++) {
                blocks[x][y].setType(newTypes[x][y]);
                blocks[x][y].setWall(newWalls[x][y]);
            }
    }

    protected boolean inRange(int x, int y, int width, int height) {return x>=0&&x<width&&y>=0&&y<height;}
}
