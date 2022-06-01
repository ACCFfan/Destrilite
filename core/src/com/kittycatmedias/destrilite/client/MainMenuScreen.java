package com.kittycatmedias.destrilite.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kittycatmedias.destrilite.event.EventListener;
import com.kittycatmedias.destrilite.network.packet.PacketHandler;
import com.kittycatmedias.destrilite.network.packet.PacketListener;
import com.kittycatmedias.destrilite.network.packet.packets.WorldCreatePacket;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.block.BlockState;

public class MainMenuScreen implements DestriliteScreen, PacketListener, EventListener {
    private final Viewport viewport;
    private final Batch batch;
    private final AssetManager assetManager;

    private final DestriliteGame game;

    public MainMenuScreen(DestriliteGame game){
        viewport = new ScreenViewport();
        batch = game.getBatch();
        assetManager = new AssetManager();

        this.game = game;

        game.getEventManager().addListener(this);
        game.getPacketManager().addListener(this);
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0.0f,1);
        viewport.apply();
        viewport.getCamera().update();
        batch.setProjectionMatrix(viewport.getCamera().projection);
        batch.setTransformMatrix(viewport.getCamera().view);

        if(assetManager.isFinished()) {
            batch.begin();

            String text = "sas";
            game.getFont().draw(batch, text, game.getTextOffset(text), 100);
            if(Gdx.input.isTouched()){
                if(Gdx.input.getY() > viewport.getScreenHeight() / 2 && !game.isClient()) {

                    game.changeScreen(new GameScreen(game, null));
                }else if(Gdx.input.getX() < viewport.getScreenWidth() / 2){

                    game.createServer();
                }else{
                    game.createClient("localhost");
                }
            }

            batch.end();
        }else assetManager.finishLoading();
    }

    @PacketHandler
    public void onWorldBlockInfo(WorldCreatePacket packet){
        if(game.isClient())game.changeScreen(new GameScreen(game, WorldCreatePacket.decode(packet)));
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

    }

    @Override
    public AssetManager getManager() {
        return assetManager;
    }
}
