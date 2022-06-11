package com.kittycatmedias.destrilite.network.packet.packets;

import com.kittycatmedias.destrilite.entity.Entity;

public class EntityMovePacket {
    public long id;
    public float x, y, velX, velY;

    public static EntityMovePacket create(Entity entity){
        EntityMovePacket packet = new EntityMovePacket();
        packet.id = entity.getID();
        packet.x = entity.getLocation().getX();
        packet.y = entity.getLocation().getY();
        packet.velX = entity.getLocation().getVelocity().x;
        packet.velY = entity.getLocation().getVelocity().y;
        return packet;
    }

    public static Entity decode(EntityMovePacket packet){
        return Entity.getEntity(packet.id);
    }
}
