package com.kittycatmedias.destrilite.world.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.kittycatmedias.destrilite.world.Location;

public class Particle {


    private final ParticleType type;
    private float scale, ticks;
    private Location location;

    private boolean flipX, flipY, dispose;
    private int rotation;
    private final int id;
    private Color color;

    private static int nextID = 0;


    public Particle(ParticleType type, Location location){
        this.type = type;
        this.location = location;
        this.scale = 1;
        flipX = MathUtils.randomBoolean();
        flipY = MathUtils.randomBoolean();
        rotation = MathUtils.random(4);
        id = nextID++;
        this.color = null;

        type.onCreate(this);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public ParticleType getType() {
        return type;
    }

    public void update(float delta){
        type.update(this, delta);
        ticks+=delta;
    }

    public void render(SpriteBatch batch, float delta){
        type.render(batch, this, delta);
    }

    public float getScale() {
        return scale;
    }

    public boolean isFlippedX() {
        return flipX;
    }

    public boolean isFlippedY() {
        return flipY;
    }

    public int getRotation() {
        return rotation;
    }

    public float getWidth(){
        return type.getWidth() * scale;
    }

    public float getHeight(){
        return type.getHeight() * scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public int getID() {
        return id;
    }

    public void dispose(){
        dispose = true;
    }

    public boolean shouldDispose(){
        return dispose;
    }

    public float getTicks() {
        return ticks;
    }

    public void setFlipX(boolean flipX) {
        this.flipX = flipX;
    }

    public void setFlipY(boolean flipY) {
        this.flipY = flipY;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color){
        this.color = color;
    }
}

