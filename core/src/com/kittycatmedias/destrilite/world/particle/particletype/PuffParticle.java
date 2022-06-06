package com.kittycatmedias.destrilite.world.particle.particletype;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.kittycatmedias.destrilite.world.particle.Particle;
import com.kittycatmedias.destrilite.world.particle.ParticleType;

public class PuffParticle extends ParticleType {
    Sprite s0, s1, s2, s3, s4;

    public PuffParticle() {
        super(7/8f, 7/8f);
    }

    @Override
    public void createSprite(TextureAtlas atlas) {
        s0 = atlas.createSprite("puff/0");
        s1 = atlas.createSprite("puff/1");
        s2 = atlas.createSprite("puff/2");
        s3 = atlas.createSprite("puff/3");
        s4 = atlas.createSprite("puff/4");
    }

    @Override
    public Sprite getSprite(Particle particle, float delta) {
        int i = (int) (particle.getTicks() / 0.25f);
        if(i == 0)return s0;
        if(i == 1)return s1;
        if(i == 2)return s2;
        if(i == 3)return s3;
        return s4;
    }

    @Override
    public void update(Particle particle, float delta) {
        particle.getLocation().getVelocity().y+=delta*4;
        if(particle.getTicks()/0.25f>=5)particle.dispose();
        super.update(particle, delta);
    }
}
