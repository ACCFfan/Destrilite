package com.kittycatmedias.destrilite.event.events.network;

import com.esotericsoftware.kryonet.Connection;
import com.kittycatmedias.destrilite.event.Event;

public class ConnectionMadeEvent extends Event {
    private final Connection conncetion;

    public ConnectionMadeEvent(Connection connection) {
        super(false);

        this.conncetion = connection;
    }

    public Connection getConncetion() {
        return conncetion;
    }
}
