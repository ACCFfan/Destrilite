package com.kittycatmedias.destrilite.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.network.packet.packets.EntityMovePacket;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.block.BlockState;

public class Entity {

    private static int nextID;
    private static final Array<Entity> entities = new Array<>();

    private final Location location;
    private final EntityType type;
    private final ObjectMap<String, Object> meta;
    private final Rectangle bounds;

    private final int id;

    private boolean dirtyPosition,dirty, tcpPosition,tcp, hasCollision, hasGravity, walksUp;
    private int health;
    private float width, height, totalFrames;

    public static final int FROM_LEFT = 0, FROM_RIGHT = 1, FROM_TOP = 2, FROM_BOTTOM = 3;

    public Entity(Location location, EntityType type, ObjectMap<String, Object> meta){
        this.location = location;
        this.type = type;
        this.walksUp = getType().walksUp();
        hasGravity = type.hasGravity();
        width = type.getWidth();
        height = type.getHeight();
        bounds = new Rectangle(location.getX(), location.getY(), width, height);
        id = nextID++;
        hasCollision = type.isCollidable();
        health = type.getMaxHealth();
        if(meta == null)this.meta = new ObjectMap<>();
        else this.meta = meta;
        entities.add(this);

        type.onMetaChange(this);
        type.onCreate(this);
    }


    //ONLY FOR USE FROM PACKETS
    public Entity(Location location, EntityType type, ObjectMap<String, Object> meta, int id){
        this.location = location;
        this.type = type;
        this.walksUp = getType().walksUp();
        hasGravity = type.hasGravity();
        width = type.getWidth();
        height = type.getHeight();
        bounds = new Rectangle(location.getX(), location.getY(), width, height);
        this.id = id;
        nextID = id+1;
        hasCollision = type.isCollidable();
        health = type.getMaxHealth();
        if(meta == null)this.meta = new ObjectMap<>();
        else this.meta = meta;
        entities.add(this);

        type.onMetaChange(this);
        type.onCreate(this);
    }

    public void update(float delta){
        boolean dirtyPos = location.move(delta);
        if(dirtyPos){
            bounds.x = location.getX();
            bounds.y = location.getY();
        }

        if(hasCollision){
            if(hasGravity)location.getVelocity().y -= delta * location.getWorld().getGravity();
            int sX = Math.max(0, (int) location.getX()), sY = Math.max(0, (int) location.getY()),
                    eX = (int) Math.min(location.getWorld().getWidth(), location.getX()+width+1),
                    eY = (int) Math.min(location.getWorld().getHeight(), location.getY()+height+1);
            BlockState[][] blocks = location.getWorld().getBlocks();
            for(int x = sX; x < eX; x++)for(int y = sY; y < eY; y++){
                BlockState state = blocks[x][y];
                if(state.isCollidable() && state.getBounds().overlaps(bounds)){
                    Vector3 velocity = location.getVelocity();

                    final float stateX = state.getX(), stateY = state.getY(), entityX = location.getX(), entityY = location.getY();
                    //CHECK WHAT'S ABOVE / TO THE SIDE
                    final boolean bottom = state.getY() > entityY && y > 0 && !blocks[x][y-1].isCollidable(),
                            top = state.getY() < entityY && y < blocks[x].length-1 && !blocks[x][y+1].isCollidable(),
                            left = state.getX() > entityX && x > 0 && !blocks[x-1][y].isCollidable(),
                            right = state.getX() < entityX && x < blocks.length-1 && !blocks[x+1][y].isCollidable();
                    //GET THE POSITION INSIDE
                    final float inX = left ? (entityX + width) - stateX : (stateX + 1) - entityX, inY = bottom ? (entityY + height) - stateY : (stateY + 1) - entityY;
                    final int from;
                    if(bottom && ((!left && !right) || inY < inX)) from = FROM_BOTTOM;
                    else if(top && ((!left && !right) || inY < inX))from = FROM_TOP;
                    else if(left && ((!bottom && !top) || inX < inY))from = FROM_LEFT;
                    else from = FROM_RIGHT;



                    state.collides(this, from);
                    collides(state, invertFrom(from));
                }
            }
        }

        Vector3 velocity = location.getVelocity();
        if(velocity.x != 0 || velocity.y != 0) {
            final float min = 0.05f;
            final float pow = (float) Math.pow((1 - velocity.z), delta);
            velocity.set(velocity.x * pow, hasGravity ? velocity.y : velocity.y * pow, velocity.z);
            if (velocity.x < min && velocity.x > -min) velocity.set(0, velocity.y, velocity.z);
            if (!hasGravity && velocity.y < min && velocity.y > -min) velocity.set(velocity.x, 0, velocity.z);
        }

        type.update(this, delta);

        if(dirtyPos)markDirtyPosition(false);
        if(DestriliteGame.getInstance().isServer()){
            if(dirtyPosition) {
                //TODO packet
                DestriliteGame.getInstance().getServer().sendToAll(EntityMovePacket.create(this),tcpPosition);

                dirtyPosition = false;
                tcpPosition = false;
            }
        }
    }

    public void render(SpriteBatch batch, float delta) {
        totalFrames += delta;
        type.render(batch, this, delta);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location){
        this.location.setX(location.getX());
        this.location.setY(location.getY());
        this.location.setWorld(location.getWorld());
        bounds.x = location.getX();
        bounds.y = location.getY();
        this.location.setVelocity(location.getVelocity());
        markDirtyPosition(true);


        //TODO events
    }

    public void setLocation(float x, float y){
        location.setX(x);
        location.setY(y);
        bounds.x = x;
        bounds.y = y;
        markDirtyPosition(true);
    }


    private void markDirtyPosition(boolean tcp){
        if(!tcp)this.tcpPosition = true;
        dirtyPosition = true;
    }

    private void markDirty(boolean tcp){
        if(!tcp)this.tcp = true;
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

    public boolean hasMeta(String key){
        return meta.containsKey(key);
    }

    public Object getMeta(String key){
        return meta.get(key, null);
    }

    public void setMeta(String key, Integer meta){
        this.meta.put(key, meta);
        type.onMetaChange(this);
        markDirty(true);
    }

    public static Array<Entity> getEntities() {
        return entities;
    }

    public static Entity getEntity(int id){
        for(Entity entity : entities)if(entity.getID() == id)return entity;
        return null;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean hasGravity() {
        return hasGravity;
    }

    public boolean walksUp() {
        return walksUp;
    }

    public void collides(BlockState state, int from){
        type.onCollide(this, state, from);
    }

    public int invertFrom(int from){
        if(from == FROM_LEFT)return FROM_RIGHT;
        else if(from == FROM_RIGHT)return FROM_LEFT;
        else if(from == FROM_TOP)return FROM_BOTTOM;
        else return FROM_TOP;
    }

    public float getTotalFrames() {
        return totalFrames;
    }

    public void setWidth(float width) {
        this.width = width;
        bounds.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
        bounds.height = height;
    }

    public void setWalksUp(boolean walksUp) {
        this.walksUp = walksUp;
    }
}
