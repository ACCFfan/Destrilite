package com.kittycatmedias.destrilite.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.network.packet.packets.EntityCreatePacket;
import com.kittycatmedias.destrilite.world.Location;

public class Entity {

    private static int nextID;
    private static final Array<Entity> entities = new Array<>();

    private final Location location;
    private final EntityType type;
    private final ObjectMap<String, Integer> meta;

    private final int id;

    private boolean dirty, tcp, hasCollision;
    private int health;

    public Entity(Location location, EntityType type, ObjectMap<String, Integer> meta){
        this.location = location;
        this.type = type;
        id = nextID++;
        hasCollision = type.isCollidable();
        health = type.getMaxHealth();
        if(meta == null)this.meta = new ObjectMap<>();
        else this.meta = meta;
        type.onMetaChange(this);
        entities.add(this);

        type.onCreate(this);
    }


    //ONLY FOR USE FROM PACKETS
    public Entity(Location location, EntityType type, ObjectMap<String, Integer> meta, int id){
        this.location = location;
        this.type = type;
        this.id = id;
        nextID = id+1;
        hasCollision = type.isCollidable();
        health = type.getMaxHealth();
        if(meta == null)this.meta = new ObjectMap<>();
        else this.meta = meta;
        type.onMetaChange(this);
        entities.add(this);

        type.onCreate(this);
    }

    public void update(float delta){

        type.update(this, delta);

        if(location.move(delta))markDirty(false);
        if(dirty){
            //TODO packet

            dirty = false;
            tcp = false;
        }
    }

    public void render(SpriteBatch batch, float delta) {
        type.render(batch, this, delta);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location){
        this.location.setX(location.getX());
        this.location.setY(location.getY());
        this.location.setVelocity(location.getVelocity());
        markDirty(true);
    }



    private void markDirty(boolean tcp){
        if(!tcp)tcp = true;
        dirty = true;
    }

    public int getID() {
        return id;
    }

    public void destroy(){
        //TODO packets, events
        markDirty(true);
    }

    public void dispose(){
        entities.removeValue(this, true);

        type.onDestroy(this);
    }

    public int getHealth() {
        return health;
    }

    public boolean hasCollision() {
        return hasCollision;
    }

    public EntityType getType() {
        return type;
    }

    public void setHealth(int health) {
        //TODO events
        this.health = health;

        markDirty(true);
    }

    public void setCollision(boolean collision) {
        this.hasCollision = collision;
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

    public static Array<Entity> getEntities() {
        return entities;
    }

    public static Entity getEntity(int id){
        for(Entity entity : entities)if(entity.getID() == id)return entity;
        return null;
    }
}
