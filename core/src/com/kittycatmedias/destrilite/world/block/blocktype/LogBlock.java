package com.kittycatmedias.destrilite.world.block.blocktype;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;

public class LogBlock extends BlockType {
    private Sprite sprite2, sprite3, sprite4;

    public LogBlock() {
        super("log", 3, false, false, false, false, false);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        sprite = atlas.createSprite("blocks/log_left");
        sprite2 = atlas.createSprite("blocks/log_right");
        sprite3 = atlas.createSprite("blocks/log_left_mush");
        sprite4 = atlas.createSprite("blocks/log_right_mush");
    }

    @Override
    public void onWorldLoad(BlockState state) {
        super.onWorldLoad(state);
        if(!DestriliteGame.getInstance().isClient()){
            ObjectMap<String, Object> meta = new ObjectMap<>();
            meta.put("direction", state.getWorld().getRandom().nextBoolean() ? -1 : 1);
            meta.put("speed", state.getWorld().getRandom().nextFloat() / 2 + 0.25f);
            state.getWorld().createEntity(new Entity(new Location(state.getWorld(), state.getX(), state.getY()), EntityType.WORM, meta));
        }
    }

    @Override
    public void onMetaChange(BlockState state) {
        if(state.hasMeta("flip") && (boolean)state.getMeta("flip"))state.setFlips(true, false, 0);
    }

    @Override
    public Sprite getSprite(BlockState state) {
        if(!((boolean)state.getMeta("half"))){
            if(!((boolean)state.getMeta("mush")))return sprite;
            else return sprite3;
        }
        if(!((boolean)state.getMeta("mush")))return sprite2;
        return sprite4;
    }
}
