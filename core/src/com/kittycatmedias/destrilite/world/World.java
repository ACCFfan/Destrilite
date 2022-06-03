package com.kittycatmedias.destrilite.world;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.event.EventListener;
import com.kittycatmedias.destrilite.network.packet.PacketHandler;
import com.kittycatmedias.destrilite.network.packet.PacketListener;
import com.kittycatmedias.destrilite.network.packet.packets.EntityCreatePacket;
import com.kittycatmedias.destrilite.network.packet.packets.EntityMovePacket;
import com.kittycatmedias.destrilite.network.packet.packets.WorldCreatePacket;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;

import java.util.Random;

public class World implements EventListener, PacketListener {
    private final WorldGenerator generator;
    private final BlockState[][] blocks;
    private final int width, height;
    private final long seed;

    private final Rectangle viewBounds;
    private final Random random;

    private Array<Entity> entities;

    private float gravity;

    private int id;

    private static int nextID = 0;

    public static final Array<World> worlds = new Array<>();

    public World(WorldGenerator generator, long seed){

        DestriliteGame.getInstance().getEventManager().addListener(this);
        DestriliteGame.getInstance().getPacketManager().addListener(this);

        this.generator = generator;
        this.seed = seed;
        random = new Random(seed);
        blocks = generator.generateBlocks(random);
        width = blocks.length;
        height = blocks[0].length;
        gravity = 4f;
        viewBounds = new Rectangle();
        id = nextID++;
        worlds.add(this);
        entities = new Array<>();

        DestriliteGame.getInstance().getEventManager().addListener(this);
        DestriliteGame.getInstance().getPacketManager().addListener(this);

        if(DestriliteGame.getInstance().isServer())DestriliteGame.getInstance().getServer().sendToAll(WorldCreatePacket.create(this), true);

        for(int x = 0; x < width; x++)for(int y = 0; y < height; y++){
            blocks[x][y].setWorld(this);
            blocks[x][y].getType().onWorldLoad(blocks[x][y]);
        }
    }


    //AGAIN, ONLY USE ON PACKETS
    public World(WorldGenerator generator, long seed, int id){

        DestriliteGame.getInstance().getEventManager().addListener(this);
        DestriliteGame.getInstance().getPacketManager().addListener(this);

        this.generator = generator;
        this.seed = seed;
        random = new Random(seed);
        blocks = generator.generateBlocks(random);
        width = blocks.length;
        height = blocks[0].length;
        gravity = 0.1f;
        viewBounds = new Rectangle();
        this.id = id;
        nextID = id+1;
        worlds.add(this);
        entities = new Array<>();

        for(int x = 0; x < width; x++)for(int y = 0; y < height; y++){
            blocks[x][y].setWorld(this);
            blocks[x][y].getType().onWorldLoad(blocks[x][y]);
        }
    }

    public void createEntity(Entity entity){
        //TODO events, you know the deal


        entities.add(entity);
        if(DestriliteGame.getInstance().isServer())DestriliteGame.getInstance().getServer().sendToAll(EntityCreatePacket.create(entity), true);
    }

    public BlockState setBlock(int x, int y, BlockType type){
        //TODO events for both of these

        BlockState state = blocks[x][y];
        state.setType(type);
        return state;
    }

    public BlockState setWall(int x, int y, WallType type){
        BlockState state = blocks[x][y];
        state.setWall(type);
        return state;
    }

    public void update(float delta){
        for(BlockState[] bStates : blocks)for(BlockState block : bStates)block.update(delta);
        for(Entity entity : entities)entity.update(delta);
    }

    public void setBounds(OrthographicCamera camera){
        final float width = camera.viewportWidth * camera.zoom,
                height = camera.viewportHeight * camera.zoom,
                w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x),
                h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
        viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
    }

    public void renderBlocks(SpriteBatch batch, float delta, boolean wall){
        final int col1 = Math.max(0, (int)(viewBounds.x - 1)),
                col2 = Math.min(width, (int)((viewBounds.x + viewBounds.width + 2))),
                row1 = Math.max(0, (int)(viewBounds.y - 1)),
                row2 = Math.min(height, (int)((viewBounds.y + viewBounds.height + 2)));
        BlockState state;
        Sprite sprite;
        for(int x = col1; x < col2; x++)for(int y = row1; y < row2; y++){
            state = blocks[x][y];
            sprite = wall ? state.getWall().getSprite(state) : state.getType().getSprite(state);
            if((wall && !state.getWall().isAir()) || (!wall && !state.getType().isAir())) {
                sprite.rotate(state.getRotate() * 90);
                sprite.flip(state.isFlippedX(), state.isFlippedY());
                batch.draw(sprite, x * 8 - (wall ? 1f : 0f), y * 8 - (wall ? 1f : 0f), wall ? 10f : 8f, wall ? 10f : 8f);
                sprite.flip(state.isFlippedX(), state.isFlippedY());
                sprite.rotate((4 - state.getRotate()) * 90);
            }
        }
    }

    public void render(SpriteBatch batch, float delta){
        for(Entity entity : entities)entity.render(batch, delta);
    }

    public BlockState[][] getBlocks() {
        return blocks;
    }

    public long getSeed() {
        return seed;
    }

    public Random getRandom() {
        return random;
    }

    public float getGravity() {
        return gravity;
    }

    public WorldGenerator getGenerator() {
        return generator;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void dispose(){
        for(Entity entity : entities) entity.dispose();
        worlds.removeValue(this, true);
        DestriliteGame.getInstance().getEventManager().removeListener(this);
        DestriliteGame.getInstance().getPacketManager().removeListener(this);
    }

    public int getID() {
        return id;
    }

    public static World getWorld(int id){
        for(World world : worlds)if(world.getID() == id)return world;
        return null;
    }

    public boolean aboveIsOpen(int x, int y, float height){
        final int upTo = (int) Math.min(y + MathUtils.ceil(height) + 1, this.height);
        boolean open = true;
        for(int i = y+1; i < upTo; i++)if(blocks[x][i].isCollidable())open = false;
        return open;
    }

    @PacketHandler
    public void onEntityCreate(EntityCreatePacket packet){
        if(packet.world == id)createEntity(EntityCreatePacket.decode(packet));
    }

    @PacketHandler
    public void onEntityMove(EntityMovePacket packet){
        Entity entity = EntityMovePacket.decode(packet);
        entity.setLocation(packet.x,packet.y);
        entity.getLocation().getVelocity().set(packet.x, packet.y, entity.getLocation().getVelocity().z);
    }
}
