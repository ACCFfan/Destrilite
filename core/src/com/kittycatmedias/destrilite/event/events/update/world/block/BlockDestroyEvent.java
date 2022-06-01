package com.kittycatmedias.destrilite.event.events.update.world.block;

import com.kittycatmedias.destrilite.event.Event;
import com.kittycatmedias.destrilite.world.block.BlockState;

public class BlockDestroyEvent extends Event {
    private final BlockState state;

    public BlockDestroyEvent(BlockState state) {
        super(true);
        this.state = state;
    }

    public BlockState getState() {
        return state;
    }
}
