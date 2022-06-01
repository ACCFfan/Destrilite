package com.kittycatmedias.destrilite.event;

import com.badlogic.gdx.Gdx;

public class Event {
    protected final boolean cancellable;
    protected boolean cancelled = false;

    public Event(boolean cancellable) {
        this.cancellable = cancellable;
    }

    public boolean isCancellable(){return cancellable;}

    public boolean setCancelled(boolean cancelled){
        if(cancellable){
            this.cancelled = cancelled;
            return true;
        }
        Gdx.app.log("Destrilite", "Event " + this + " failed to cancel!");
        return false;
    }

    public boolean isCancelled(){return cancelled;}

}
