package com.kittycatmedias.destrilite.world.block;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.world.block.blocktype.*;

public class BlockType {

    protected final int damage, id;
    protected final boolean indestructible, collidable, flippableX, flippableY, rotatable;

    protected final String name;

    protected Sprite sprite;

    protected static final Array<BlockType> types = new Array<>();
    private static int nextID = 0;

    public static final BlockType AIR = registerType(new AirBlock());
    public static final BlockType DIRT = registerType(new DirtBlock());
    public static final BlockType GRASS = registerType(new GrassBlock());
    public static final BlockType STONE = registerType(new StoneBlock());
    public static final BlockType BLACKSTONE = registerType(new BlackstoneBlock());
    public static final BlockType ROSE = registerType(new RoseBlock());
    public static final BlockType GLOWBERRY = registerType(new GlowberryBlock());
    public static final BlockType TALL_GRASS = registerType(new ShortGrassBlock());
    public static final BlockType SHORT_GRASS = registerType(new TallGrassBlock());
    public static final BlockType LOG = registerType(new LogBlock());
    public static final BlockType BEAM = registerType(new BeamBlock());
    public static final BlockType PLATFORM = registerType(new PlatformBlock());
    public static final BlockType ROOT = registerType(new RootBlock());
    public static final BlockType LARGE_ROOT = registerType(new LargeRootBlock());
    public static final BlockType CHEST = registerType(new ChestBlock());
    public static final BlockType ENTRANCE = registerType(new EntranceBlock());
    public static final BlockType EXIT = registerType(new ExitBlock());


    public static BlockType registerType(BlockType type){
        if(!types.contains(type, true))types.add(type);
        return type;
    }

    public boolean isAir(){return this == AIR;}

    protected BlockType(String name, int damage, boolean indestructible, boolean collidable, boolean flippableX, boolean flippableY, boolean rotatable){
        this.damage = damage;
        this.indestructible = indestructible;
        this.collidable = collidable;
        this.name = name;
        this.flippableX = flippableX;
        this.flippableY = flippableY;
        this.rotatable = rotatable;
        id = nextID++;
    }

    public void update(BlockState blockState, float delta){

    }

    public void onCreate(BlockState blockState){

    }

    public void onWorldLoad(BlockState state){

    }

    public void onChange(BlockState state){

    }

    public void onMetaChange(BlockState state){

    }

    public void onDestroy(BlockState blockState){

        onChange(blockState);
    }

    public String getName() {
        return name;
    }

    public int getDamage(){return damage;}

    public boolean isCollidable() {
        return collidable;
    }

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
        sprite = atlas.createSprite("blocks/"+name);
    }

    public Sprite getSprite(BlockState state) {
        return sprite;
    }

    public static Array<BlockType> getTypes() {
        return types;
    }

    public int getId() {
        return id;
    }

    public static BlockType getType(int id){
        return types.get(id);
    }
}
