package com.kittycatmedias.destrilite.entity.type.player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.world.Location;

public class PlayerEntity extends EntityType {

    public PlayerEntity() {
        super("player", 1, false, true, 1, 1, true, false);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {

    }

    @Override
    public void render(SpriteBatch batch, Entity entity, float delta) {

    }
}
