package com.kittycatmedias.destrilite.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.event.events.network.ConnectionDisconnectedEvent;
import com.kittycatmedias.destrilite.event.events.network.ConnectionMadeEvent;

public class DestriliteClient extends Listener {
    private final Client client;
    private final DestriliteGame game;

    private final String IP;
    private final int UDP_PORT, TCP_PORT;

    public DestriliteClient(DestriliteGame game, String ip) {
        client = new Client();
        UDP_PORT = DestriliteServer.DEFAULT_PORT;
        TCP_PORT = DestriliteServer.DEFAULT_PORT+1;
        IP = ip;

        this.game = game;

        init();
    }

    public DestriliteClient(DestriliteGame game, String ip, int udp, int tcp){
        client = new Client();
        UDP_PORT = udp;
        TCP_PORT = tcp;
        IP = ip;

        this.game = game;

        init();
    }

    private void init(){
        DestriliteServer.registerPackets(client.getKryo());
        client.addListener(this);
    }

    public void start(){
        try{
            client.start();
            client.connect(5000, IP, TCP_PORT, UDP_PORT);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stop(){
        client.stop();
    }

    public Client getClient() {
        return client;
    }

    @Override
    public void connected(Connection connection) {
        game.getEventManager().callEvent(new ConnectionMadeEvent(connection));
    }

    @Override
    public void disconnected(Connection connection) {
        game.getEventManager().callEvent(new ConnectionDisconnectedEvent(connection));
    }

    @Override
    public void received(Connection connection, Object p) {
        game.getPacketManager().callPacket(p);
    }

}
