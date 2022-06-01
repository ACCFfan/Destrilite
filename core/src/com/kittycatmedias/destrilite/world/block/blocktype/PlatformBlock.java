package com.kittycatmedias.destrilite.world.block.blocktype;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;

public class PlatformBlock extends BlockType {
    private Sprite sprite2;

    public PlatformBlock() {
        super("platform", 3, false, true, false, false, false);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        sprite = atlas.createSprite("blocks/platform");
        sprite2 = atlas.createSprite("blocks/platform_intersection");
    }

    @Override
    public Sprite getSprite(BlockState state) {
        if(state.getMeta("beam") == 1)return sprite2;
        return sprite;
    }
}
