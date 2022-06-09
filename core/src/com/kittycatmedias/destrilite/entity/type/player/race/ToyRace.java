package com.kittycatmedias.destrilite.entity.type.player.race;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kittycatmedias.destrilite.entity.type.player.Player;
import com.kittycatmedias.destrilite.entity.type.player.Race;

public class ToyRace extends Race {
    public ToyRace() {
        super("toy", 0.75f, 7/8f, 0.5f, 3/8f, 0,-0.25f,3/8f,2.5f, 2.5f, 0.5f, 3.5f, 3f, 0.5f);
    }

    @Override
    public void render(Player player, SpriteBatch batch, float delta) {

    }
}
