package com.kittycatmedias.destrilite.world.block.blocktype;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;

public class LargeRootBlock extends BlockType {
    private Sprite sprite2;

    public LargeRootBlock() {
        super("large_root", 1, false, false, false, false, false);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        sprite = atlas.createSprite("blocks/large_root_top");
        sprite2 = atlas.createSprite("blocks/large_root_bottom");
    }

    @Override
    public void onMetaChange(BlockState state) {
        if(state.hasMeta("flip") && (boolean)state.getMeta("flip"))state.setFlips(true, false, 0);
    }

    @Override
    public Sprite getSprite(BlockState state) {
        if((boolean)state.getMeta("top"))return sprite;
        return sprite2;
    }
}
