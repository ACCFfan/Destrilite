package com.kittycatmedias.destrilite.entity;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.client.GameScreen;
import com.kittycatmedias.destrilite.network.packet.packets.EntityChangeWorldPacket;
import com.kittycatmedias.destrilite.network.packet.packets.EntityCreatePacket;
import com.kittycatmedias.destrilite.network.packet.packets.EntityMovePacket;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.block.BlockState;

public class Entity {

    private static long nextID;
    private static final Array<Entity> entities = new Array<>();

    private final Location location, startLocation;
    private final EntityType type;
    private final ObjectMap<String, Object> meta;
    private final Rectangle bounds;

    private final long id;

    private boolean dirtyPosition,dirty, tcpPosition,tcp, hasCollision, hasGravity, walksUp, grounded;
    private int health;
    private float width, height, totalTime;

    public static final int NOTHING = -1, FROM_LEFT = 0, FROM_RIGHT = 1, FROM_TOP = 2, FROM_BOTTOM = 3;

    public Entity(Location location, EntityType type, ObjectMap<String, Object> meta, long id){
        this.location = location;
        startLocation = location.copy();
        this.type = type;
        this.walksUp = getType().walksUp();
        hasGravity = type.hasGravity();
        width = type.getWidth();
        height = type.getHeight();
        bounds = new Rectangle(location.getX(), location.getY(), width, height);
        if(id != -1)this.id = id;
        else this.id = nextID++;
        hasCollision = type.isCollidable();
        grounded = false;
        health = type.getMaxHealth();
        if(meta == null)this.meta = new ObjectMap<>();
        else this.meta = meta;
        if(getEntity(this.id) == null) {
            entities.add(this);
            type.onMetaChange(this);
            type.onCreate(this);

            if (type != EntityType.PLAYER && DestriliteGame.getInstance().isServer())
                DestriliteGame.getInstance().getServer().sendToAll(EntityCreatePacket.create(this), true);
        }else dispose();
    }

    public void update(float delta){
        totalTime += delta;
        boolean dirtyPos = location.move(delta);
        if(dirtyPos){
            bounds.x = location.getX();
            bounds.y = location.getY();
        }

        if(hasCollision){
            if(location.getVelocity().y != 0)grounded = false;
            if(location.getX()+width < 0 || location.getY()+height < 0 || location.getX() > location.getWorld().getWidth() || location.getY() > location.getWorld().getHeight())reset();
            if(hasGravity)location.getVelocity().y -= delta * location.getWorld().getGravity();
            int sX = Math.max(0, (int) location.getX()), sY = Math.max(0, (int) location.getY()),
                    eX = (int) Math.min(location.getWorld().getWidth(), location.getX()+width+1),
                    eY = (int) Math.min(location.getWorld().getHeight(), location.getY()+height+1);
            BlockState[][] blocks = location.getWorld().getBlocks();
            for(int y = sY; y < eY; y++)for(int x = sX; x < eX; x++){
                BlockState state = blocks[x][y];
                if(state.isAnyCollidable() && state.getBounds().overlaps(bounds)){
                    //Vector3 velocity = location.getVelocity();

                    final float stateX = state.getX(), stateY = state.getY(), entityX = location.getX(), entityY = location.getY(),
                            velX = location.getVelocity().x, velY = location.getVelocity().y,
                            midBlockX = 0.5f + stateX, midBlockY = 0.5f + stateY, midEntityX = entityX + width / 2, midEntityY = entityY + height / 2,
                            difMidX = Math.abs(midBlockX - midEntityX), difMidY = Math.abs(midBlockY - midEntityY);
                    //CHECK WHAT'S ABOVE / TO THE SIDE
                    final boolean
                            bottom = midBlockY > midEntityY && y > 0 && !blocks[x][y-1].isCollidable(),
                            top = midBlockY <= midEntityY && y < blocks[x].length-1 && !blocks[x][y+1].isCollidable(),
                            left = midBlockX > midEntityX && x > 0 && !blocks[x-1][y].isCollidable(),
                            right = midBlockX <= midEntityX && x < blocks.length-1 && !blocks[x+1][y].isCollidable(),
                            lr = (!bottom && !top) || difMidX >= difMidY,
                            tb = (!left && !right) || difMidY >= difMidX;
                    //GET THE POSITION INSIDE
                    //final float inX = right ? (entityX + width) - stateX : (stateX + 1) - entityX, inY = bottom ? (entityY + height) - stateY : (stateY + 1) - entityY;

                    //final float inX = Math.min(Math.abs(entityX-x-0.5f), Math.abs(entityX+width-x-0.5f)),inY = Math.min(Math.abs(entityY-y-0.5f), Math.abs(entityY+height-y-0.5f));


                    final int from;
                    if(left && lr && velX > 0)from = FROM_LEFT;
                    else if (right && lr && velX < 0)from = FROM_RIGHT;
                    else if(bottom && tb && velY > 0) from = FROM_BOTTOM;
                    else if(top && tb && velY < 0)from = FROM_TOP;
                    else from = NOTHING;


                    if(from != NOTHING) {
                        state.collides(this, from);
                        collides(state, invertFrom(from));
                    }
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
        if(DestriliteGame.getInstance().getScreen() instanceof GameScreen){
            GameScreen screen = (GameScreen) DestriliteGame.getInstance().getScreen();
            if(DestriliteGame.getInstance().isServer()){
                if(dirtyPosition && (type != EntityType.PLAYER || screen.getPlayer().getEntity() == this)) {
                    //TODO packet
                    DestriliteGame.getInstance().getServer().sendToAll(EntityMovePacket.create(this),tcpPosition);

                    dirtyPosition = false;
                    tcpPosition = false;
                }
            }else if(DestriliteGame.getInstance().isClient() && screen.getPlayer().getEntity() == this){
                DestriliteGame.getInstance().getClient().sendToServer(EntityMovePacket.create(this),tcpPosition);
                dirtyPosition = false;
                tcpPosition = false;
            }

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
        bounds.x = location.getX();
        bounds.y = location.getY();
        this.location.setVelocity(location.getVelocity());

        this.startLocation.setX(location.getX());
        this.startLocation.setY(location.getY());
        this.startLocation.setVelocity(location.getVelocity());
        if(location.getWorld() != this.location.getWorld())setWorld(location.getWorld());
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

    public long getID() {
        return id;
    }

    public void destroy(){
        //TODO packets, events
        markDirty(true);
    }

    public void dispose(){
        if(entities.contains(this, true)){
            type.onDestroy(this);
            entities.removeValue(this, true);
        }
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

    public static Entity getEntity(long id){
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

    public static int invertFrom(int from){
        if(from == FROM_LEFT)return FROM_RIGHT;
        else if(from == FROM_RIGHT)return FROM_LEFT;
        else if(from == FROM_TOP)return FROM_BOTTOM;
        else return FROM_TOP;
    }

    public float getTotalTimeAlive() {
        return totalTime;
    }

    public void setWidth(float width) {
        this.width = width;
        bounds.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
        bounds.height = height;
    }

    public void setGrounded(boolean grounded){
        this.grounded = grounded;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void setWalksUp(boolean walksUp) {
        this.walksUp = walksUp;
    }

    public void reset(){
        setLocation(startLocation);
    }

    public void setWorld(World world){
        if(DestriliteGame.getInstance().isServer()) {
            EntityChangeWorldPacket packet = new EntityChangeWorldPacket();
            packet.id = id;
            packet.world = world.getID();
            DestriliteGame.getInstance().getServer().sendToAll(packet, true);
        }
        location.setWorld(world);
        startLocation.setWorld(world);
    }
}
