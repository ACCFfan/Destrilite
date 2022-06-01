package com.kittycatmedias.destrilite.network.packet.packets;

import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.block.BlockState;
import com.kittycatmedias.destrilite.world.block.BlockType;
import com.kittycatmedias.destrilite.world.block.WallType;

public class WorldBlockInfoPacket {
    public String[][][] metaKeys;
    public int[][][] metaData;
    public int[][] tileData;
    public int[][] wallData;

    public static WorldBlockInfoPacket create(World world){
        WorldBlockInfoPacket packet = new WorldBlockInfoPacket();
        BlockState state;
        BlockState[][] blocks = world.getBlocks();
        Array<String> keys;
        packet.tileData = new int[world.getWidth()][world.getHeight()];
        packet.wallData = new int[world.getWidth()][world.getHeight()];
        packet.metaKeys = new String[world.getWidth()][world.getHeight()][];
        packet.metaData = new int[world.getWidth()][world.getHeight()][];
        for(int x = 0; x < blocks.length; x++)for(int y = 0; y < blocks[x].length; y++){
            state = blocks[x][y];
            keys = state.getMeta();
            packet.tileData[x][y] = state.getType().getId();
            packet.wallData[x][y] = state.getWall().getId();
            packet.metaKeys[x][y] = new String[keys.size];
            packet.metaData[x][y] = new int[keys.size];
            for(int i = 0; i < keys.size; i++){
                packet.metaKeys[x][y][i] = keys.get(i);
                packet.metaData[x][y][i] = state.getMeta(keys.get(i));
            }
        }
        return packet;
    }

    public static BlockState[][] decode(WorldBlockInfoPacket packet){
        BlockState[][] blocks = new BlockState[packet.tileData.length][packet.tileData[0].length];

        BlockState state;
        for(int x = 0; x < packet.tileData.length; x++)for(int y = 0; y < packet.tileData[x].length; y++){
            state = new BlockState(x,y,BlockType.getType(packet.tileData[x][y]), WallType.getType(packet.wallData[x][y]));
            for(int i = 0; i < packet.metaKeys[x][y].length; i++)state.setMeta(packet.metaKeys[x][y][i], packet.metaData[x][y][i]);
            blocks[x][y] = state;
        }

        return blocks;
    }

}
