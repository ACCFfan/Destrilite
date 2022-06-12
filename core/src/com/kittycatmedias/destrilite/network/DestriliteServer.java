package com.kittycatmedias.destrilite.network;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Collections;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.event.events.network.ConnectionDisconnectedEvent;
import com.kittycatmedias.destrilite.event.events.network.ConnectionMadeEvent;
import com.kittycatmedias.destrilite.network.packet.packets.*;
import com.sun.tools.jdi.Packet;

public class DestriliteServer extends Listener {
    public static final int DEFAULT_PORT = 37189;

    private final Server server;
    private final DestriliteGame game;

    private final Array<Connection> connections;

    private boolean allowing;

    private final int UDP_PORT, TCP_PORT;

    private boolean ready;

    public DestriliteServer(DestriliteGame game) {
        server = new Server(131072,131072);
        UDP_PORT = DEFAULT_PORT;
        TCP_PORT = DEFAULT_PORT+1;

        connections = new Array<>();

        this.game = game;

        ready = false;

        init();
    }

    public DestriliteServer(DestriliteGame game, int udp, int tcp){
        server = new Server();
        UDP_PORT = udp;
        TCP_PORT = tcp;

        connections = new Array<>();

        this.game = game;

        ready = false;

        init();
    }

    private void init(){
        registerPackets(server.getKryo());
        try{
            server.addListener(this);
            server.bind(TCP_PORT, UDP_PORT);


            ready = true;
            allowing = true;
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void start(){
        if(ready)server.start();
    }

    public void stop(){
        server.stop();
    }

    public void sendToAll(Object packet, boolean tcp){
        connections.forEach(connection -> {
            if(tcp)connection.sendTCP(packet);
            else connection.sendUDP(packet);
        });
    }

    public void sendToAllExcept(Object packet, boolean tcp, Connection connection){
        connections.forEach(conn -> {
            if(conn != connection) {
                if (tcp) conn.sendTCP(packet);
                else conn.sendUDP(packet);
            }
        });
    }

    public Server getServer() {
        return server;
    }

    public Array<Connection> getConnections() {
        return connections;
    }

    public int getTCPPort() {
        return TCP_PORT;
    }

    public int getUDPPort() {
        return UDP_PORT;
    }

    public boolean isAllowing() {
        return allowing;
    }

    public void setAllowing(boolean allowing) {
        this.allowing = allowing;
    }

    @Override
    public void connected(Connection connection) {
        connections.add(connection);
        game.getEventManager().callEvent(new ConnectionMadeEvent(connection));
        if(!allowing)connection.close();
    }

    @Override
    public void disconnected(Connection connection) {
        connections.removeValue(connection, true);
        game.getEventManager().callEvent(new ConnectionDisconnectedEvent(connection));
    }

    @Override
    public void received(Connection connection, Object p) {
        game.getPacketManager().callPacket(p, connection);
    }

    public static void registerPackets(Kryo kryo){
        kryo.register(WorldCreatePacket.class);
        kryo.register(EntityCreatePacket.class);
        kryo.register(EntityMovePacket.class);
        kryo.register(PlayerConnectPacket.class);
        kryo.register(PlayerDisconnectPacket.class);
        kryo.register(ServerClosePacket.class);
        kryo.register(SetWorldPacket.class);
        kryo.register(EntityChangeWorldPacket.class);
        kryo.register(PlayerHeadMovePacket.class);
        kryo.register(ParticleCreatePacket.class);

        kryo.register(String[].class);
        kryo.register(Object[].class);
    }

}
