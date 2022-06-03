package com.kittycatmedias.destrilite.network.packet.packets;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.World;

public class EntityCreatePacket {
    public int id, world, type;
    public float x, y;
    public int[] metaValues;
    public String[] metaKeys;

    public static EntityCreatePacket create(Entity entity){
        EntityCreatePacket packet = new EntityCreatePacket();
        packet.x = entity.getLocation().getX();
        packet.y = entity.getLocation().getY();
        packet.id = entity.getID();
        packet.world = entity.getLocation().getWorld().getID();
        packet.type = entity.getType().getID();
        Array<String> meta = entity.getMeta();
        int[] metaValues = new int[meta.size];
        String[] metaKeys = new String[meta.size];
        for(int i = 0; i < meta.size; i++){
            metaKeys[i] = meta.get(i);
            metaValues[i] = entity.getMeta(metaKeys[i]);
        }
        packet.metaValues = metaValues;
        packet.metaKeys = metaKeys;
        return packet;
    }

    public static Entity decode(EntityCreatePacket packet){
        World world = World.getWorld(packet.world);
        Location location = new Location(world, packet.x, packet.y);
        EntityType type = EntityType.getType(packet.type);
        ObjectMap<String, Integer> map = new ObjectMap<>();
        for(int i = 0; i < packet.metaKeys.length; i++)map.put(packet.metaKeys[i], packet.metaValues[i]);
        Entity entity = new Entity(location, type, map, packet.id);
        return entity;
    }

}