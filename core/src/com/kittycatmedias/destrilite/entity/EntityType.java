package com.kittycatmedias.destrilite.entity;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.entity.type.Worm;
import com.kittycatmedias.destrilite.world.World;

public abstract class EntityType {

    protected final int maxHealth, id;
    protected final boolean indestructible, collidable;

    protected final String name;


    private static final Array<EntityType> types = new Array<>();

    public static final EntityType WORM = registerType(new Worm());
    private static int nextID = 0;

    public static EntityType registerType(EntityType type){
        if(!types.contains(type, true))types.add(type);
        return type;
    }

    protected EntityType(String name, int maxHealth, boolean indestructible, boolean collidable){
        this.maxHealth = maxHealth;
        this.indestructible = indestructible;
        this.collidable = collidable;
        this.name = name;
        id = nextID++;
    }

    public void update(Entity entity, float delta){

    }

    public void onCreate(Entity entity){

    }

    public void onWorldLoad(World world){

    }

    public void onMetaChange(Entity entity){

    }

    public void onDestroy(Entity entity){


    }

    public String getName() {
        return name;
    }

    public int getMaxHealth(){return maxHealth;}

    public boolean isCollidable() {
        return collidable;
    }

    public boolean isIndestructible() {return indestructible;}

    public abstract void createSprite(TextureAtlas atlas);

    public abstract void render(SpriteBatch batch, Entity entity, float delta);

    public static Array<EntityType> getTypes() {
        return types;
    }

    public int getID() {
        return id;
    }

    public static EntityType getType(int id){
        return types.get(id);
    }

    public static int getNextID(){
        return nextID++;
    }

}
