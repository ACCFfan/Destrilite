package com.kittycatmedias.destrilite.world;

import com.badlogic.gdx.math.Vector3;

import java.awt.*;

public class Location {
    private World world;

    private float x, y;
    private Vector3 velocity;

    public Location(World world, float x, float y) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.velocity = new Vector3(0, 0, 0.99f);
    }

    public Location(World world, float x, float y, Vector3 velocity) {
        this.x = x;
        this.y = y;
        this.world = world;
        this.velocity = velocity;
    }


    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public World getWorld() {
        return world;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public Location getTargetLocation(float delta) {
        if(velocity.x != 0 || velocity.y != 0)
            return add(velocity.x * delta, velocity.y * delta);
        return copy();
    }

    public boolean move(float delta) {
        float x1 = x, y1 = y;
        x += velocity.x * delta;
        y += velocity.y * delta;
        return x1 != x || y1 != y;
    }

    public boolean isSamePlace(Location location) {
        return world == location.world && x == location.x && y == location.y;
    }

    public boolean isSameExact(Location location){
        return world == location.world && x == location.x && y == location.y && location.velocity.equals(velocity);
    }

    public Location add(float x, float y) {
        return new Location(world, this.x + x, this.y + y, velocity.cpy());
    }

    public Location add(Location location) {
        Vector3 v = velocity.cpy().add(location.velocity);
        v.z = velocity.z;
        return new Location(world, this.x + location.x, this.y + location.y, v);
    }

    public Location difference(float x, float y) {
        return new Location(world, this.x - x, this.y - y, velocity.cpy());
    }

    public Location difference(Location location) {
        Vector3 v = velocity.cpy().sub(location.velocity);
        v.z = velocity.z;
        return new Location(world, x - location.x, y - location.y, v);
    }

    public Location multiply(float mult){
        return new Location(world, x * mult, y * mult,velocity.cpy().set(velocity.x*mult,velocity.y*mult,velocity.z));
    }

    public Location copy() {
        return new Location(world, x, y, velocity.cpy());
    }

    public Point getBlockLocation(){
        return new Point((int)x, (int)y);
    }

    public void setWorld(World world) {
        this.world = world;
    }
}
