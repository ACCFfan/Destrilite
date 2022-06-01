package com.kittycatmedias.destrilite.event;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.lang.reflect.Method;


public class EventManager {

    private final Array<EventListener> listeners = new Array<>();
    private final ObjectMap<Class<? extends Event>, Array<EventListener>> listenerTypes = new ObjectMap<>();
    private final ObjectMap<Class<? extends EventListener>, EventEntry> listenerContents = new ObjectMap<>();

    public boolean addListener(EventListener listener) {
        if(!listeners.contains(listener, true)) {
            listeners.add(listener);


            //Check if class has entry
            EventEntry e;
            if(!listenerContents.containsKey(listener.getClass())) {
                listenerContents.put(listener.getClass(), (e = new EventEntry(listener.getClass())));
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

    public boolean isListening(EventListener listener) {
        return listeners.contains(listener, true);
    }

    public boolean removeListener(EventListener listener) {
        if(listeners.contains(listener, true)) {
            listeners.removeValue(listener, true);

            //go through all entries and remove the listener
            listenerContents.get(listener.getClass()).m.keys()
                    .forEach(c-> listenerTypes.get(c).removeValue(listener, true));

            return true;
        }
        return false;
    }

    public Array<EventListener> getListeners(){
        Array<EventListener> l2 = new Array<>();
        for(EventListener l : listeners)l2.add(l);
        return l2;
    }

    public boolean callEvent(Event event) {


        Class<? extends Event> c = event.getClass();

        if(listenerTypes.containsKey(c))listenerTypes.get(event.getClass()).forEach(l-> {

            EventEntry e = listenerContents.get(l.getClass());
            try {
                e.m.get(c).invoke(l, event);
            }catch(Exception ex) {
                Gdx.app.log("Destrilite", "Error invoking " + event + " in " + l + "!");
                ex.printStackTrace();
            }

        });
        return !event.isCancelled();

    }

    private static class EventEntry {

        public Class<? extends EventListener> l;

        public ObjectMap<Class<? extends Event>, Method> m = new ObjectMap<>();

        @SuppressWarnings("unchecked")
        public EventEntry(Class<? extends EventListener> l) {
            this.l = l;
            for(Method me : l.getDeclaredMethods())if(me.isAnnotationPresent(EventHandler.class)) {
                Class<? extends Event> ev = (Class<? extends Event>) me.getParameterTypes()[0];
                m.put(ev, me);
            }
        }

    }

}
