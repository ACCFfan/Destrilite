package com.kittycatmedias.destrilite.world.generator;

import com.badlogic.gdx.utils.Array;

import java.awt.*;

public class GeneratorCell {

    private GeneratorCellType type;

    private Array<GeneratorCellType> possibleCells;

    private final int x, y;

    public GeneratorCell(int x, int y, Array<GeneratorCellType> types){
        possibleCells = new Array<>();
        for(GeneratorCellType type : types)possibleCells.add(type);
        this.x = x;
        this.y = y;
    }

    public GeneratorCell(int x, int y, GeneratorCellType type){
        possibleCells = new Array<>();
        this.type = type;
        possibleCells.add(type);
        this.x = x;
        this.y = y;
    }

    public GeneratorCellType getType() {
        return type;
    }

    public Array<GeneratorCellType> getPossibleCells() {
        return possibleCells;
    }



    public boolean update(Array<GeneratorCellType> left, Array<GeneratorCellType> right, Array<GeneratorCellType> top, Array<GeneratorCellType> bottom){
        if(type == null) {
            Array<GeneratorCellType> types = null;

            if (left != null) {
                Array<GeneratorCellType> types0 = new Array<>();
                for (GeneratorCellType type : left) {
                    GeneratorCellType[] types1 = type.getSlots(GeneratorCellType.RIGHT);
                    for (GeneratorCellType t : types1) if (!types0.contains(t, true)) types0.add(t);
                }
                types = new Array<>();
                types.addAll(types0);
            }

            if (right != null) {
                Array<GeneratorCellType> types0 = new Array<>();
                for (GeneratorCellType type : right) {
                    GeneratorCellType[] types1 = type.getSlots(GeneratorCellType.LEFT);
                    for (GeneratorCellType t : types1) if (!types0.contains(t, true)) types0.add(t);
                }
                if (types == null) {
                    types = new Array<>();
                    types.addAll(types0);
                } else for (GeneratorCellType type : types)
                    if (!types0.contains(type, true))
                        types.removeValue(type, true);
            }

            if (top != null) {
                Array<GeneratorCellType> types0 = new Array<>();
                for (GeneratorCellType type : top) {
                    GeneratorCellType[] types1 = type.getSlots(GeneratorCellType.BOTTOM);
                    for (GeneratorCellType t : types1) if (!types0.contains(t, true)) types0.add(t);
                }
                if (types == null) {
                    types = new Array<>();
                    types.addAll(types0);
                } else for (GeneratorCellType type : types)
                    if (!types0.contains(type, true))
                        types.removeValue(type, true);
            }

            if (bottom != null) {
                Array<GeneratorCellType> types0 = new Array<>();
                for (GeneratorCellType type : bottom) {
                    GeneratorCellType[] types1 = type.getSlots(GeneratorCellType.TOP);
                    for (GeneratorCellType t : types1) if (!types0.contains(t, true)) types0.add(t);
                }
                if (types == null) {
                    types = new Array<>();
                    types.addAll(types0);
                } else for (GeneratorCellType type : types)
                    if (!types0.contains(type, true))
                        types.removeValue(type, true);
            }
            if (possibleCells.equalsIdentity(types)) return false;
            possibleCells = types;
            return true;
        }
        return false;
    }

    public void setType(GeneratorCellType type) {
        this.type = type;
        possibleCells.clear();
        possibleCells.add(type);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
