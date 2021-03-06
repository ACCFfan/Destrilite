package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.entity.type.player.Player;
import com.kittycatmedias.destrilite.entity.type.player.Race;
import com.kittycatmedias.destrilite.event.EventListener;
import com.kittycatmedias.destrilite.network.packet.PacketListener;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.generator.WorldGenerator;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;
import com.kittycatmedias.destrilite.world.particle.ParticleType;

public class GameScreen extends DestriliteScreen implements EventListener, PacketListener {
    private final OrthographicCamera camera;
    private final Matrix4 bufferViewMatrix, bufferMatrix;

    private TextureAtlas tileAtlas, entityAtlas, playerAtlas, particleAtlas;
    private Texture bufferBlockTexture, bufferWallTexture;
    private ShaderProgram defaultShader;
    private FrameBuffer blockBuffer, wallBuffer;
    private ShaderProgram outlineShader;

    private float offsetX,offsetY;

    private final Player player;
    private final Array<Player> players;

    private World world;

    public GameScreen(DestriliteGame game, World world, Player player){
        super(game, new ExtendViewport(48*0.6f,27*0.6f, 48*2*0.6f,27*2*0.6f));

        camera = new OrthographicCamera();
        viewport.setCamera(camera);

        this.world = world;
        players = new Array<>();
        this.player = player;
        players.add(player);

        assetManager.load("atlas/tiles.atlas", TextureAtlas.class);
        assetManager.load("atlas/entities.atlas", TextureAtlas.class);
        assetManager.load("atlas/player.atlas", TextureAtlas.class);
        assetManager.load("atlas/particle.atlas", TextureAtlas.class);

        if(!game.isClient() && this.world == null)this.world = new World(WorldGenerator.GRASSLANDS, MathUtils.random.nextLong(), -1);

        World.setCurrentWorld(this.world);

        player.getEntity().setLocation(new Location(this.world, 30, 80), true);
        this.world.createEntity(player.getEntity());

        bufferViewMatrix = new Matrix4();
        bufferMatrix = new Matrix4();

        offsetX = 1f/(this.world.getWidth()*8);
        offsetY = 1f/(this.world.getHeight()*8);

        game.getEventManager().addListener(this);
        game.getPacketManager().addListener(this);
    }

    @Override
    public void loadAssets(){
        super.loadAssets();
        tileAtlas = assetManager.get("atlas/tiles.atlas");
        entityAtlas = assetManager.get("atlas/entities.atlas");
        playerAtlas = assetManager.get("atlas/player.atlas");
        particleAtlas = assetManager.get("atlas/particle.atlas");

        for(BlockType b : BlockType.getTypes())b.createSprite(tileAtlas);
        for(WallType w : WallType.getTypes())w.createSprite(tileAtlas);
        for(EntityType e : EntityType.getTypes())e.createSprite(entityAtlas);
        for(ParticleType p : ParticleType.getTypes())p.createSprite(particleAtlas);

        blockBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, this.world.getWidth() * 8, this.world.getHeight() * 8, false);
        wallBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, this.world.getWidth() * 8, this.world.getHeight() * 8, false);
        bufferMatrix.setToOrtho(0, blockBuffer.getWidth(), blockBuffer.getHeight(),0,0,1);

        //TODO convert to strings
        ShaderProgram.pedantic = false;
        outlineShader = new ShaderProgram(Gdx.files.internal("shader/passthrough.vsh"), Gdx.files.internal("shader/outline.fsh"));
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(world != null) {
            world.update(Math.min(0.05f, delta));
            for(Player player : players)player.update(Math.min(0.05f, delta));

            if(camera.position.x - camera.viewportWidth / 2 < 0)camera.position.x = 0 + camera.viewportWidth / 2;
            else if(camera.position.x + camera.viewportWidth / 2 > world.getWidth())camera.position.x = world.getWidth() - camera.viewportWidth / 2;
            if(camera.position.y - camera.viewportHeight / 2 < 0)camera.position.y = 0 + camera.viewportHeight / 2;
            else if(camera.position.y + camera.viewportHeight / 2 > world.getHeight())camera.position.y = world.getHeight() - camera.viewportHeight / 2;

            viewport.getCamera().update();

            world.setBounds(camera);

            batch.setProjectionMatrix(bufferMatrix);
            batch.setTransformMatrix(bufferViewMatrix);

            //wall render
            wallBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            world.renderBlocks(batch, delta, true);
            batch.end();
            wallBuffer.end();

            bufferWallTexture = wallBuffer.getColorBufferTexture();
            bufferWallTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

            //block render
            blockBuffer.begin();
            Gdx.gl.glClearColor(0, 0, 0, 0);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            batch.begin();
            world.renderBlocks(batch, delta, false);
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
            world.render(batch, delta);

            for(Player p : players)p.render(batch, delta);

            batch.end();
            batch.setShader(defaultShader);
        }


    }

    @Override
    public void dispose() {
        super.dispose();
        world.dispose();
        blockBuffer.dispose();
        bufferBlockTexture.dispose();
        bufferWallTexture.dispose();
        outlineShader.dispose();
        game.getPacketManager().removeListener(this);
        game.getEventManager().removeListener(this);

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

    public void addPlayer(Player player){
        if(!players.contains(player,true)){
            players.add(player);

            player.getEntity().setLocation(new Location(this.world, 30, 80), true);
            this.world.createEntity(player.getEntity());
        }
    }

    public Player getPlayer() {
        return player;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public static GameScreen getScreen(){
        if(DestriliteGame.getInstance().getScreen() instanceof GameScreen) return (GameScreen)DestriliteGame.getInstance().getScreen();
        return null;
    }

    public Array<Player> getPlayers(){
        return players;
    }
}
