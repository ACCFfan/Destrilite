package com.kittycatmedias.destrilite.network.packet.packets;

import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.generator.WorldGenerator;

public class WorldCreatePacket {
    public long seed;
    public int generator, id;

    public static WorldCreatePacket create(World world){
        WorldCreatePacket packet = new WorldCreatePacket();
        packet.seed = world.getSeed();
        packet.generator = world.getGenerator().getID();
        packet.id = world.getID();
        return packet;
    }

    public static World decode(WorldCreatePacket packet){
        return new World(WorldGenerator.getGenerator(packet.generator), packet.seed, packet.id);
    }

}
