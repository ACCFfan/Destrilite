package com.kittycatmedias.destrilite.world.generator;

import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.WorldGenerator;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;

import java.util.ArrayList;
import java.util.Random;

public class Grasslands extends WorldGenerator {
    public Grasslands(long seed, int width, int height) {
        super(seed, width, height);
    }


    @Override
    public BlockState[][] generateBlocks() {
        BlockState[][] blocks = new BlockState[width][height];

        ArrayList<Cave> caves = new ArrayList<>();

        for(int x = 0; x < width; x++)for(int y = 0; y < height; y++){
            BlockState b = new BlockState(x,y,BlockType.STONE,WallType.STONE);
            blocks[x][y] = b;
        }

        int utc = 20;

        {
            int x = random.nextInt(10) + utc, range = 4, y = random.nextInt(height - utc*2) + utc, upTo = random.nextInt(10) - utc + width;
            while(x < upTo){
                caves.add(new Cave(x, y, range));
                if(random.nextBoolean())x++;
                range += random.nextInt(3) - 1;
                if(range > 7) range = 7;
                if(range < 2) range = 2;
                y += random.nextInt(3) - 1;
                if(y > height - utc) y = height - utc;
                if(y < utc) y = utc;

                if(random.nextInt(20) == 0){
                    int x2 = x, y2 = y, r2 = Math.min(range, 3), amnt = random.nextInt(15)+5;
                    boolean dir = random.nextBoolean();
                    while(amnt>0){
                        y2+=dir?-1:1;
                        if(y2 > height - utc) y2 = height - utc;
                        if(y2 < utc) y2 = utc;
                        if(random.nextBoolean())x2+= random.nextInt(3) - 1;
                        if(x2 > width - utc) x2 = height - utc;
                        if(x2 < utc) x2 = utc;

                        r2 += random.nextInt(3) - 1;
                        if(r2 > 3) r2 = 3;
                        if(r2 < 2) r2 = 2;
                        amnt--;
                        caves.add(new Cave(x2, y2, r2));
                    }
                }
            }
        }

        int upTo = random.nextInt(8);
        for(int i = 0; i < upTo+6;i++){
            int x = random.nextInt(width - utc*2) + utc, y = random.nextInt(height - utc*2) + utc, range = random.nextInt(3)+2, amnt = random.nextInt(10)+5;
            boolean dirX = random.nextBoolean(), dirY = random.nextBoolean();
            while(amnt>0){
                if(random.nextBoolean())y+=dirY?-1:1;
                if(random.nextBoolean())x+=dirX?-1:1;
                if(random.nextInt(5) == 0){
                    if(random.nextBoolean())dirX = !dirX;
                    else dirY = !dirY;
                }

                if(y > height - utc) y = height - utc;
                if(y < utc) y = utc;

                if(x > width - utc) x = height - utc;
                if(x < utc) x = utc;

                range += random.nextInt(3) - 1;
                if(range > 7) range = 7;
                if(range < 1) range = 1;
                amnt--;
                caves.add(new Cave(x, y, range));
            }
        }

        for(Cave cave : caves){
            double range = cave.range, rangeMax = range + 5, lx = cave.x, ly = cave.y;
            int upToX = (int) (lx + rangeMax + 1), upToY = (int) (ly + rangeMax + 1);
            for (int x = (int) (lx - rangeMax); x < upToX; x++)for (int y = (int) (ly - rangeMax); y < upToY; y++) {
                if (inRange(x,y,width,height)) {
                    double sqrt = Math.sqrt((x - lx) * (x - lx) + (y - ly) * (y - ly));
                    if(sqrt <= rangeMax){
                        BlockState state = blocks[x][y];
                        boolean flowered = random.nextInt(10) == 0;
                        if(sqrt < range) {
                            if(sqrt < range-2)state.setWall(WallType.AIR);
                            else if(!state.getWall().isAir())state.setWall(flowered ? WallType.FLOWERED : WallType.GRASS);
                            state.setType(BlockType.AIR);
                        }else if(!state.getType().isAir()){
                            state.setWall(flowered ? WallType.FLOWERED : WallType.GRASS);
                            state.setType(BlockType.DIRT);
                        }
                    }
                }
            }
        }

        BlockState t, t2, t3, t4;
        BlockType type;
        for(int x = 0; x < width; x++)for(int y = 0; y < height - 1; y++){
            t = blocks[x][y];
            t2 = blocks[x][y+1];
            t3 = inRange(x+1,y+1,width,height) ? blocks[x+1][y+1] : null;
            t4 = inRange(x+1,y,width,height) ? blocks[x+1][y] : null;
            if (t.getType() == BlockType.DIRT && !t2.isCollidable()){
                t.setType(BlockType.GRASS);
                if(t2.getType().isAir() && random.nextBoolean()){
                    type = random.nextBoolean() ? BlockType.SHORT_GRASS : BlockType.TALL_GRASS;
                    if(random.nextInt(4) == 0)type = random.nextInt(8) == 0 ? BlockType.GLOWBERRY : BlockType.ROSE;
                    if(t3 != null && t4 != null && t4.isCollidable() && random.nextInt(16) == 0){
                        boolean half = random.nextBoolean();
                        type = BlockType.LOG;
                        t2.setType(type);
                        t3.setType(type);
                        t2.setMeta("half", half ? 0 : 1);
                        t2.setMeta("flip", half ? 0 : 1);
                        t2.setMeta("mush", random.nextBoolean() ? 0 : 1);
                        t3.setMeta("half", half ? 1 : 0);
                        t3.setMeta("flip", half ? 0 : 1);
                        t3.setMeta("mush", random.nextBoolean() ? 0 : 1);
                    }else t2.setType(type);
                }
            }
        }

        for(int x = 0; x < width; x++)for(int y = 0; y < height; y++){
            int b1 = x < 4 ? x : x > width - 5 ? (width - x - 1) : -1;
            int b2 = y < 4 ? y : y > height - 5 ? (height - y - 1) :  - 1;
            int b = b1 != -1 && b2 != -1 ? Math.min(b1, b2) : Math.max(b1,b2);
            if(b != -1 && random.nextInt(b+1) == 0){
                blocks[x][y].setType(BlockType.BLACKSTONE);
            }
        }

        return blocks;
    }

    private boolean inRange(int x, int y, int width, int height){
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    private static class Cave{
        protected final int x, y;
        protected final double range;

        protected Cave(int x, int y, double range) {
            this.x = x;
            this.y = y;
            this.range = range;
        }
    }
}
