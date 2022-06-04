package com.kittycatmedias.destrilite.world.generator;

import com.kittycatmedias.destrilite.world.block.BlockState;

public abstract class GeneratorCellType {

    public static final int LEFT = 0, RIGHT = 1, TOP = 2, BOTTOM = 3;

    private GeneratorCellType[] topSlots, bottomSlots, leftSlots, rightSlots;

    public void setSlots(GeneratorCellType[] leftSlots, GeneratorCellType[] rightSlots, GeneratorCellType[] topSlots, GeneratorCellType[] bottomSlots){
        this.topSlots = topSlots;
        this.bottomSlots = bottomSlots;
        this.leftSlots = leftSlots;
        this.rightSlots = rightSlots;
    }

    public GeneratorCellType[] getSlots(int direction){
        if(direction == LEFT)return leftSlots;
        if(direction == RIGHT)return rightSlots;
        if(direction == TOP)return topSlots;
        return bottomSlots;
    }

    public boolean canSlot(GeneratorCellType type, int direction){
        for(GeneratorCellType c : getSlots(direction))if(c == type)return true;
        return false;
    }

    public abstract BlockState[][] fill(int width, int height);

    public void populate(BlockState[][] blockStates) {

    }

    public static int inverse(int direction){
        if(direction == LEFT)return RIGHT;
        if(direction == RIGHT)return LEFT;
        if(direction == TOP)return BOTTOM;
        return TOP;
    }
}
