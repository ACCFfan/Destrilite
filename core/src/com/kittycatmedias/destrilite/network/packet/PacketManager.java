package com.kittycatmedias.destrilite.network.packet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Method;


public class PacketManager {

    private final Array<PacketListener> listeners = new Array<>();
    private final ObjectMap<Class<?>, Array<PacketListener>> listenerTypes = new ObjectMap<>();
    private final ObjectMap<Class<? extends PacketListener>, PacketEntry> listenerContents = new ObjectMap<>();

    public boolean addListener(PacketListener listener) {
        if(!listeners.contains(listener, true)) {
            listeners.add(listener);


            //Check if class has entry
            PacketEntry e;
            if(!listenerContents.containsKey(listener.getClass())) {
                listenerContents.put(listener.getClass(), (e = new PacketEntry(listener.getClass())));
            }else e = listenerContents.get(listener.getClass());

            //Run through entry and register listener to event
            e.m.keys().forEach(c-> {
                if(!listenerTypes.containsKey(c))listenerTypes.put(c, new Array<>());
                listenerTypes.get(c).add(listener);
            });
            return true;
        }
        return false;
    }

    public boolean isListening(PacketListener listener) {
        return listeners.contains(listener, true);
    }

    public boolean removeListener(PacketListener listener) {
        if(listeners.contains(listener, true)) {
            listeners.removeValue(listener, true);

            //go through all entries and remove the listener
            listenerContents.get(listener.getClass()).m.keys()
                    .forEach(c-> listenerTypes.get(c).removeValue(listener, true));

            return true;
        }
        return false;
    }

    public Array<PacketListener> getListeners(){
        Array<PacketListener> l2 = new Array<>();
        for(PacketListener l : listeners)l2.add(l);
        return l2;
    }

    public void callPacket(Object packet) {


        Class<?> c = packet.getClass();

        if(listenerTypes.containsKey(c))listenerTypes.get(packet.getClass()).forEach(l-> {

            PacketEntry e = listenerContents.get(l.getClass());
            try {
                e.m.get(c).invoke(l, packet);
            }catch(Exception ex) {
                Gdx.app.log("Destrilite", "Error invoking " + packet + " in " + l + "!");
                ex.printStackTrace();
            }

        });
    }

    private static class PacketEntry {

        public Class<? extends PacketListener> l;

        public ObjectMap<Class<?>, Method> m = new ObjectMap<>();

        public PacketEntry(Class<? extends PacketListener> l) {
            this.l = l;
            for(Method me : l.getDeclaredMethods())if(me.isAnnotationPresent(PacketHandler.class)) {
                Class<?> ev = me.getParameterTypes()[0];
                m.put(ev, me);
            }
        }

    }

}
