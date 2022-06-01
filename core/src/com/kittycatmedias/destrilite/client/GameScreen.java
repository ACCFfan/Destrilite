package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kittycatmedias.destrilite.network.packet.PacketListener;
import com.kittycatmedias.destrilite.network.packet.packets.WorldCreatePacket;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.WorldGenerator;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;

public class GameScreen implements DestriliteScreen, PacketListener {
    private final Viewport worldViewport;
    private final OrthographicCamera camera;

    private final SpriteBatch batch;
    private final Matrix4 bufferViewMatrix;
    private final AssetManager assetManager;
    private final DestriliteGame game;

    private boolean assetsLoaded;

    private TextureAtlas tileAtlas;
    private Texture bufferBlockTexture, bufferWallTexture;
    private ShaderProgram defaultShader;
    private FrameBuffer blockBuffer, wallBuffer;
    private Matrix4 bufferMatrix;
    private ShaderProgram outlineShader;

    private float offsetX,offsetY;


    private World world;

    public GameScreen(DestriliteGame game, World world){
        float w = 48*.75f, h = 27*.75f;
        camera = new OrthographicCamera();
        worldViewport = new ExtendViewport(w,h, w*2,h*2, camera);
        batch = game.getBatch();
        assetManager = new AssetManager();
        this.game = game;
        this.world = world;
        game.getPacketManager().addListener(this);

        assetsLoaded = false;

        assetManager.load("atlas/tiles.atlas", TextureAtlas.class);

        if(!game.isClient() && this.world == null)this.world = new World(WorldGenerator.GRASSLANDS, MathUtils.random.nextLong(), 200, 100);
        if(game.isServer())game.getServer().sendToAll(WorldCreatePacket.create(this.world), true);

        bufferViewMatrix = new Matrix4();

        //TODO convert to strings
        ShaderProgram.pedantic = false;
    }

    private void loadAssets(){
        tileAtlas = assetManager.get("atlas/tiles.atlas");

        for(BlockType b : BlockType.getTypes())b.createSprite(tileAtlas);
        for(WallType w : WallType.getTypes())w.createSprite(tileAtlas);

        blockBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, this.world.getWidth() * 8, this.world.getHeight() * 8, false);
        wallBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, this.world.getWidth() * 8, this.world.getHeight() * 8, false);
        offsetX = 1f/blockBuffer.getWidth();
        offsetY = 1f/blockBuffer.getHeight();
        bufferMatrix = new Matrix4();
        bufferMatrix.setToOrtho(0, blockBuffer.getWidth(), blockBuffer.getHeight(),0,0,1);
        outlineShader = new ShaderProgram(Gdx.files.internal("shader/passthrough.vsh"), Gdx.files.internal("shader/outline.fsh"));

        assetsLoaded = true;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        if(assetManager.isFinished()) {
            if(!assetsLoaded)loadAssets();


            if(Gdx.input.isTouched()){
                Vector3 pos = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                camera.position.x -= (camera.position.x - pos.x) * delta * 2.0f;
                camera.position.y -= (camera.position.y - pos.y) * delta * 2.0f;
            }
            ScreenUtils.clear(1f, 1f, 1f, 1f);
            worldViewport.apply();
            camera.update();

            if(world != null) {
                world.update(delta);

                world.setBounds(camera);

                batch.setProjectionMatrix(bufferMatrix);
                batch.setTransformMatrix(bufferViewMatrix);

                //wall render
                wallBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 0);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                batch.begin();
                world.render(batch, delta, true);
                batch.end();
                wallBuffer.end();

                bufferWallTexture = wallBuffer.getColorBufferTexture();
                bufferWallTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

                //block render
                blockBuffer.begin();
                Gdx.gl.glClearColor(0, 0, 0, 0);
                Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

                batch.begin();
                world.render(batch, delta, false);
                batch.end();
                blockBuffer.end();

                bufferBlockTexture = blockBuffer.getColorBufferTexture();
                bufferBlockTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

                //render those

                batch.setProjectionMatrix(camera.projection);
                batch.setTransformMatrix(camera.view);

                defaultShader = batch.getShader();

                outlineShader.bind();
                outlineShader.setUniformf("u_offsetX", offsetX);
                outlineShader.setUniformf("u_offsetY", offsetY);
                outlineShader.setUniformf("u_outlineColor", 0f, 0f, 0f, 1f);
                batch.setShader(outlineShader);
                batch.begin();

                batch.draw(bufferWallTexture, 0, 0, bufferBlockTexture.getWidth() / 8.0f, bufferBlockTexture.getHeight() / 8.0f);
                batch.draw(bufferBlockTexture, 0, 0, bufferBlockTexture.getWidth() / 8.0f, bufferBlockTexture.getHeight() / 8.0f);

                batch.end();
                batch.setShader(defaultShader);
            }
        }else assetManager.finishLoading();


    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width,height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

        assetManager.dispose();
        world.dispose();
        blockBuffer.dispose();
        bufferBlockTexture.dispose();
        bufferWallTexture.dispose();
        outlineShader.dispose();

        game.getPacketManager().removeListener(this);

    }

    @Override
    public AssetManager getManager() {
        return assetManager;
    }


    public Viewport getViewport() {
        return worldViewport;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world){
        this.world = world;
        blockBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, world.getWidth() * 8, world.getHeight() * 8, false);
        wallBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, world.getWidth() * 8, world.getHeight() * 8, false);
        offsetX = 1f/blockBuffer.getWidth();
        offsetY = 1f/blockBuffer.getHeight();

        bufferMatrix.setToOrtho(0, blockBuffer.getWidth(), blockBuffer.getHeight(),0,0,1);
    }
}
