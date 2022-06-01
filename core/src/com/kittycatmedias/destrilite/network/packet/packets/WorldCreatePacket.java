package com.kittycatmedias.destrilite.network.packet.packets;

import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.WorldGenerator;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;

public class WorldCreatePacket {
    public long seed;
    public int width, height, generator;

    public static WorldCreatePacket create(World world){
        WorldCreatePacket packet = new WorldCreatePacket();
        packet.seed = world.getSeed();
        packet.generator = world.getGenerator().getID();
        packet.height = world.getHeight();
        packet.width = world.getWidth();
        return packet;
    }

    public static World decode(WorldCreatePacket packet){
        return new World(WorldGenerator.getGenerator(packet.generator), packet.seed, packet.width, packet.height);
    }

}
