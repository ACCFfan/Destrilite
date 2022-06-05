package com.kittycatmedias.destrilite.entity.type.player.race;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.client.DestriliteScreen;
import com.kittycatmedias.destrilite.client.GameScreen;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.type.player.Player;
import com.kittycatmedias.destrilite.entity.type.player.Race;
import com.kittycatmedias.destrilite.world.Location;

public class HumanRace extends Race {
    public HumanRace() {
        super("human", 7/8f, 7/8f, 5/8f, 0.5f, 3, 1.5f, 2.5f, 0.5f, 1, 1);
    }

    @Override
    public void render(Player player, SpriteBatch batch, float delta) {

    }
}
