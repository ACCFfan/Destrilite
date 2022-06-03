package com.kittycatmedias.destrilite.world.block;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.event.events.update.world.block.BlockDamageEvent;
import com.kittycatmedias.destrilite.event.events.update.world.block.BlockDestroyEvent;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;

import java.util.Map;

public class BlockState {
    private final int x, y;
    private World world;
    private BlockType type;
    private WallType wall;

    private boolean flipX, flipY, collidable;
    private int damage, rotate;

    private final ObjectMap<String, Integer> meta;

    public BlockState(int x, int y, BlockType type){
        this.x = x;
        this.y = y;
        meta = new ObjectMap<>();
        this.wall = WallType.AIR;
        setType(type);
    }

    public BlockState(int x, int y, BlockType block, WallType wall){
        this.x = x;
        this.y = y;
        meta = new ObjectMap<>();
        this.wall = wall;
        setType(block);
    }

    public void update(float delta){
        type.update(this, delta);
    }

    public void setFlips(boolean flipX, boolean flipY, int rotate){
        this.flipX = flipX;
        this.flipY = flipY;
        this.rotate = rotate;
    }

    public void randomizeFlip(){
        flipX = type.isFlippableX() && MathUtils.randomBoolean();
        flipY = type.isFlippableY() && MathUtils.randomBoolean();
        rotate = type.isRotatable() ? MathUtils.random(4) : 0;
    }

    public Array<String> getMeta() {
        Array<String> c = new Array<>();
        meta.keys().forEach(c::add);
        return c;
    }

    public Integer getMeta(String key){
        return meta.get(key, -1);
    }

    public void setMeta(String key, Integer meta){
        this.meta.put(key, meta);
        type.onMetaChange(this);
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public BlockType getType() {
        return type;
    }

    public boolean isFlippedX() {
        return flipX;
    }

    public boolean isFlippedY() {
        return flipY;
    }

    public int getRotate() {
        return rotate;
    }

    public int getDamage(){return damage;}

    public void damage(int amount){
        if(!type.isIndestructible()) {
            BlockDamageEvent e = new BlockDamageEvent(this, damage - amount);
            if (DestriliteGame.getInstance().getEventManager().callEvent(e)){
                if(e.getDamage() != this.damage)randomizeFlip();
                this.damage = e.getDamage();
                if(this.damage <= 0)destroy();
            }
        }
    }

    public void destroy(){

        //TODO wall destroy
        if(DestriliteGame.getInstance().getEventManager().callEvent(new BlockDestroyEvent(this))) {
            type.onDestroy(this);
            meta.clear();
            setType(BlockType.AIR);
        }
    }

    public boolean isCollidable() {
        return collidable;
    }

    public void setCollidable(boolean collidable){
        //TODO events

        this.collidable = collidable;
    }

    public void setType(BlockType type){
        setType(type, null);
    }

    public void setType(BlockType type, ObjectMap<String, Integer> meta){
        if(type != null) {
            if(this.type != null)this.type.onChange(this);
            this.meta.clear();
            if(meta != null)meta.keys().forEach(k -> this.meta.put(k, meta.get(k)));
            this.type = type;
            collidable = type.isCollidable();
            damage = type.getDamage();
            randomizeFlip();
            type.onCreate(this);
        }
    }

    public WallType getWall(){return wall;}

    public void setWall(WallType type){wall = type;}

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
