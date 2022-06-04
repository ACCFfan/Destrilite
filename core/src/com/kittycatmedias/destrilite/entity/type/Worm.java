package com.kittycatmedias.destrilite.entity.type;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.world.block.BlockState;

public class Worm extends EntityType {
    private Sprite up, down;

    public Worm() {
        super("worm", 1, false, true, 0.625f, 0.25f, true, false);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        up = atlas.createSprite("worm_up");
        down = atlas.createSprite("worm_down");
    }

    @Override
    public void update(Entity entity, float delta) {
        super.update(entity, delta);

        if(!DestriliteGame.getInstance().isClient())entity.getLocation().getVelocity().x = (int) entity.getMeta("direction");
    }

    @Override
    public void onCollide(Entity entity, BlockState state, int from) {
        super.onCollide(entity, state, from);
        if(from == Entity.FROM_LEFT || from == Entity.FROM_RIGHT)entity.setMeta("direction", (int) entity.getMeta("direction") * -1);
    }

    @Override
    public void render(SpriteBatch batch, Entity entity, float delta) {
        boolean flip = (int) entity.getMeta("direction") == 1;
        Sprite sprite = up;
        if(flip)sprite.flip(true, false);
        batch.draw(sprite, entity.getLocation().getX()-0.125f, entity.getLocation().getY()-0.125f, entity.getWidth()+0.25f, entity.getHeight()+0.25f);
        if(flip)sprite.flip(true, false);
    }
}
