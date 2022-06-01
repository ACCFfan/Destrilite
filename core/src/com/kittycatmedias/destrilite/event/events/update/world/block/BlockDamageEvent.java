package com.kittycatmedias.destrilite.event.events.update.world.block;

import com.kittycatmedias.destrilite.event.Event;
import com.kittycatmedias.destrilite.world.block.BlockState;

public class BlockDamageEvent extends Event {
    private final BlockState state;
    private int damage;

    public BlockDamageEvent(BlockState state, int damage) {
        super(true);
        this.state = state;
        this.damage = damage;
    }

    public BlockState getState() {
        return state;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}
