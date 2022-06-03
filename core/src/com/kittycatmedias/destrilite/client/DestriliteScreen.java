package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kittycatmedias.destrilite.event.EventListener;
import com.kittycatmedias.destrilite.network.packet.PacketListener;

public abstract class DestriliteScreen implements Screen, EventListener, PacketListener {
    protected final AssetManager assetManager;
    protected final DestriliteGame game;
    protected final SpriteBatch batch;
    protected final ShapeRenderer shapeRenderer;
    protected final Viewport viewport;
    protected final InputMultiplexer multiplexer;

    protected Stage stage;

    private boolean assetsLoaded;

    public DestriliteScreen(DestriliteGame game, Viewport viewport){
        assetManager = new AssetManager();
        this.game = game;
        this.viewport = viewport;
        multiplexer = new InputMultiplexer();
        batch = game.getBatch();
        shapeRenderer = game.getShapeRenderer();

        game.getEventManager().addListener(this);
        game.getPacketManager().addListener(this);
        assetsLoaded = false;
    }

    public void loadAssets(){
        assetsLoaded = true;

        stage = new Stage(viewport);;
        multiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.0f,0.5f,0.8f,1);
        viewport.apply();
        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().projection);
        batch.setTransformMatrix(viewport.getCamera().view);
        stage.act(delta);

        if(!assetManager.isFinished())assetManager.finishLoading();
        else if(!assetsLoaded)loadAssets();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
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
        game.getEventManager().removeListener(this);
        game.getPacketManager().removeListener(this);
        stage.dispose();
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }

    public DestriliteGame getGame() {
        return game;
    }

    public boolean isAssetsLoaded() {
        return assetsLoaded;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public Stage getStage() {
        return stage;
    }
}
