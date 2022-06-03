package com.kittycatmedias.destrilite.entity.type;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.EntityType;

public class Worm extends EntityType {
    private Sprite up, down;

    public Worm() {
        super("worm", 1, false, true);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        up = atlas.createSprite("worm_up");
        down = atlas.createSprite("worm_down");
    }

    @Override
    public void render(SpriteBatch batch, Entity entity, float delta) {
        batch.draw(up, entity.getLocation().getX(), entity.getLocation().getY(), 1, 0.75f);
    }
}
