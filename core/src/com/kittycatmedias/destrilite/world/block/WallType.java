package com.kittycatmedias.destrilite.world.block;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class WallType {

    private final int damage, id;
    private final boolean indestructible, flippableX, flippableY, rotatable;

    private final String name;

    private Sprite sprite;

    private static final Array<WallType> types = new Array<>();
    private static int nextID = 0;

    public static final WallType AIR = registerType(new WallType("air",0,true,true, true, true));
    public static final WallType STONE = registerType(new WallType("stone",2,false,true, true, true));
    public static final WallType DIRT = registerType(new WallType("dirt",1,false,true, true, true));
    public static final WallType GRASS = registerType(new WallType("grass",1,false,true, true, true));
    public static final WallType FLOWERED = registerType(new WallType("flowered",1,false,true, true, true));

    public static WallType registerType(WallType type){
        if(!types.contains(type, true))types.add(type);
        return type;
    }

    protected WallType(String name, int damage, boolean indestructible, boolean flippableX, boolean flippableY, boolean rotatable){
        this.damage = damage;
        this.indestructible = indestructible;
        this.name = name;
        this.flippableX = flippableX;
        this.flippableY = flippableY;
        this.rotatable = rotatable;
        id = getNextID();
    }
    public boolean isAir(){return this == AIR;}

    public String getName() {
        return name;
    }

    public int getDamage(){return damage;}

    public boolean isIndestructible() {return indestructible;}

    public boolean isFlippableX() {
        return flippableX;
    }

    public boolean isFlippableY() {
        return flippableY;
    }

    public boolean isRotatable() {
        return rotatable;
    }

    public void createSprite(TextureAtlas atlas) {
        sprite = atlas.createSprite("walls/"+name);
    }

    public Sprite getSprite(BlockState state) {
        return sprite;
    }

    public int getId() {
        return id;
    }

    public static Array<WallType> getTypes() {
        return types;
    }

    public static WallType getType(int id){
        return types.get(id);
    }

    public static int getNextID(){
        return nextID++;
    }
}
