package com.kittycatmedias.destrilite.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kittycatmedias.destrilite.client.GameScreen;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;

import java.util.Random;

public class World  {
    private final WorldGenerator generator;
    private final BlockState[][] blocks;
    private final int width, height;
    private final long seed;

    private final Rectangle viewBounds;
    private final Random random;

    private float gravity;


    public World(WorldGenerator generator, long seed, int width, int height){
        this.generator = generator;
        this.width = width;
        this.height = height;
        this.seed = seed;
        random = new Random(seed);
        blocks = generator.generateBlocks(random, width, height);
        gravity = 0.1f;
        viewBounds = new Rectangle();
        for(int x = 0; x < width; x++)for(int y = 0; y < height; y++)blocks[x][y].getType().onWorldLoad(this);
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

    }

    public void setBounds(OrthographicCamera camera){
        final float width = camera.viewportWidth * camera.zoom,
                height = camera.viewportHeight * camera.zoom,
                w = width * Math.abs(camera.up.y) + height * Math.abs(camera.up.x),
                h = height * Math.abs(camera.up.y) + width * Math.abs(camera.up.x);
        viewBounds.set(camera.position.x - w / 2, camera.position.y - h / 2, w, h);
    }

    public void render(Batch batch, float delta, boolean wall){
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

    }
}
