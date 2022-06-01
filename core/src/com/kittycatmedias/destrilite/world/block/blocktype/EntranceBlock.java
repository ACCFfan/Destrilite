package com.kittycatmedias.destrilite.world.block.blocktype;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;

public class EntranceBlock extends BlockType {
    private Sprite sprite2;

    public EntranceBlock() {
        super("entrance", 0, true, false, false, false, false);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        sprite = atlas.createSprite("blocks/entrance_bottom");
        sprite2 = atlas.createSprite("blocks/entrance_top");
    }

    @Override
    public void onMetaChange(BlockState state) {
        if(state.getMeta("left") == 0)state.setFlips(true, false, 0);
    }

    @Override
    public Sprite getSprite(BlockState state) {
        if(state.getMeta("half") == 0)return sprite;
        return sprite2;
    }
}
