package com.kittycatmedias.destrilite.network.packet.packets;

import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.particle.Particle;
import com.kittycatmedias.destrilite.world.particle.ParticleType;

public class ParticleCreatePacket {
    public int id, type, world;
    public float x, y, velX, velY, scale;

    public static ParticleCreatePacket encode(Particle particle){
        ParticleCreatePacket packet = new ParticleCreatePacket();
        packet.id = particle.getID();
        Location location = particle.getLocation();
        packet.x = location.getX();
        packet.y = location.getY();
        packet.velX = location.getVelocity().x;
        packet.velY = location.getVelocity().y;
        packet.type = particle.getType().getID();
        packet.scale = particle.getScale();

        return packet;
    }

    public static Particle decode(ParticleCreatePacket packet){
        Location location = new Location(World.getWorld(packet.world), packet.x, packet.y);
        location.getVelocity().x = packet.velX;
        location.getVelocity().y = packet.velY;
        Particle particle = new Particle(ParticleType.getParticle(packet.type), location);
        particle.setScale(packet.scale);
        return particle;
    }

}
