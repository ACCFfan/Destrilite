package com.kittycatmedias.destrilite.world.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.world.World;
import com.kittycatmedias.destrilite.world.particle.particletype.PuffParticle;

import java.util.Random;

public abstract class ParticleType {

    private final float width, height;

    private final int ID;

    private static int nextID = 0;

    private static Array<ParticleType> particles = new Array<>();

    public static ParticleType PUFF = registerParticle(new PuffParticle());

    private static ParticleType registerParticle(ParticleType particle){
        particles.add(particle);
        return particle;
    }

    public ParticleType(float width, float height){
        ID = nextID++;
        this.width = width;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public static Array<ParticleType> getTypes() {
        return particles;
    }

    public static ParticleType getParticle(int type){
        return particles.get(type);
    }

    public int getID() {
        return ID;
    }

    public abstract void createSprite(TextureAtlas atlas);

    public abstract Sprite getSprite(Particle particle, float delta);

    public void render(SpriteBatch batch, Particle particle, float delta) {
        Sprite sprite = getSprite(particle, delta);
        if(particle.getColor() != null)batch.setColor(particle.getColor());
        sprite.flip(particle.isFlippedX(), particle.isFlippedY());
        int rotation = particle.getRotation();
        if(rotation != 0){
            sprite.setOrigin(sprite.getWidth() / 2, sprite.getHeight() / 2);
            sprite.rotate(rotation * 90);
        }
        batch.draw(sprite, particle.getLocation().getX()-particle.getWidth()/2, particle.getLocation().getY()-particle.getHeight()/2, particle.getScale() * width, particle.getScale() * height);
        if(rotation != 0)sprite.rotate((4-rotation) * 90);
        sprite.flip(particle.isFlippedX(), particle.isFlippedY());
        if(particle.getColor() != null)batch.setColor(Color.WHITE);
    }

    public void onCreate(Particle particle){
        Random random = World.getCurrentWorld().getRandom();
        particle.setScale(random.nextFloat()+0.5f);
        particle.setFlipX(random.nextBoolean());
        particle.setFlipY(random.nextBoolean());
        particle.setRotation(random.nextInt(4));
    }

    public void update(Particle particle, float delta){
        particle.getLocation().move(delta);
    }
}
