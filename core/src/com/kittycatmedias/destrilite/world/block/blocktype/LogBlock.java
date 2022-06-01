package com.kittycatmedias.destrilite.world.block.blocktype;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    public void onMetaChange(BlockState state) {
        if(state.getMeta("flip") == 1)state.setFlips(true, false, 0);
    }

    @Override
    public Sprite getSprite(BlockState state) {
        if(state.getMeta("half") == 0){
            if(state.getMeta("mush") == 0)return sprite;
            else return sprite3;
        }
        if(state.getMeta("mush") == 0)return sprite2;
        return sprite4;
    }
}
