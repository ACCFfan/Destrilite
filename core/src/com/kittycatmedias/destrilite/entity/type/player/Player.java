package com.kittycatmedias.destrilite.entity.type.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kittycatmedias.destrilite.client.DestriliteGame;
import com.kittycatmedias.destrilite.client.GameScreen;
import com.kittycatmedias.destrilite.entity.Entity;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.world.Location;
import com.kittycatmedias.destrilite.world.particle.Particle;
import com.kittycatmedias.destrilite.world.particle.ParticleType;

public class Player {

    private float health, dex, str, mag, mana, def, scale, deg, pixel, maxSpeed, jumpHeight, dashSpeed, dashTimer, lastDash, walk;
    private final int ID;
    private int jumps, maxJumps;

    private boolean flip;

    private static int nextID;

    private Race race;

    private Entity entity;

    private final Location limbLocation;

    private static final Array<Player> players = new Array<>();

    public static Player registerPlayer(Player player){
        if(!players.contains(player, true))players.add(player);
        return player;
    }

    public Player(Race race, int id){
        this.race = race;
        if(id == -1) this.ID = nextID++;
        else this.ID = id;
        jumps = 0;
        maxJumps = 1;
        scale = 1f;
        dashTimer = 0.5f;
        lastDash = 0;
        pixel = 0.125f * scale;
        this.health = race.getHealth();
        this.dex = race.getDex();
        this.str = race.getStr();
        this.mag = race.getMag();
        this.mana = race.getMana();
        this.def = race.getDef();
        maxSpeed = 10;
        jumpHeight = 10f;
        dashSpeed = 25;
        ObjectMap<String, Object> meta = new ObjectMap<>();
        meta.put("race",race.getID());
        entity = new Entity(new Location(null, 0, 0), EntityType.PLAYER, meta, id+100);
        entity.setWalksUp(true);
        entity.setWidth(Math.max(race.getHeadWidth(), race.getBodyWidth()) * scale);
        entity.setHeight((race.getHeadHeight()/2 + race.getBodyHeight() + 0.375f) * scale);
        limbLocation = new Location(null, 0, 0);
    }

    public void update(float delta){
        Screen sc = DestriliteGame.getInstance().getScreen();
        if(entity.getLocation().getWorld() != limbLocation.getWorld()){
            limbLocation.setWorld(entity.getLocation().getWorld());
            limbLocation.setX(entity.getLocation().getX());
            limbLocation.setY(entity.getLocation().getY());
        }else {
            limbLocation.setX(limbLocation.getX() + (entity.getLocation().getX() - limbLocation.getX()) * delta * 30);
            limbLocation.setY(limbLocation.getY() + (entity.getLocation().getY() - limbLocation.getY()) * delta * 30);
        }
        if(entity.isGrounded())walk=(walk+entity.getLocation().getVelocity().x/scale)%360;
        if(sc instanceof GameScreen) {
            GameScreen screen = (GameScreen) sc;
            if (screen.getPlayer() == this) {
                Camera camera = screen.getViewport().getCamera();
                Vector3 pos = camera.position;
                pos.x+=(entity.getLocation().getX()-pos.x)*delta*3;
                pos.y+=(entity.getLocation().getY()-pos.y)*delta*3;

                if(Gdx.input.isButtonPressed(DestriliteGame.getInstance().ZOOM)){
                    Vector3 pos2 = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
                    pos.x -= (pos.x - pos2.x) * delta * 2.0f;
                    pos.y -= (pos.y - pos2.y) * delta * 2.0f;
                }

                camera.update();

                if(entity.isGrounded())jumps = 0;

                Vector3 v = entity.getLocation().getVelocity();
                if(Gdx.input.isKeyPressed(DestriliteGame.getInstance().LEFT_KEY))v.x = v.x < -maxSpeed*scale ? v.x : Math.max(v.x-maxSpeed*delta*7*scale, -maxSpeed*scale);
                if(Gdx.input.isKeyPressed(DestriliteGame.getInstance().RIGHT_KEY))v.x = v.x > maxSpeed*scale ? v.x : Math.min(v.x+maxSpeed*delta*7*scale, maxSpeed*scale);

                if(Gdx.input.isKeyJustPressed(DestriliteGame.getInstance().JUMP_KEY) && (entity.isGrounded() || jumps < maxJumps)){
                    //v.y= Math.min(v.y+jumpHeight*scale, jumpHeight*scale);
                    v.y = jumpHeight*scale;
                    if(!entity.isGrounded()) {
                        jumps++;
                        for (int i = 0; i < 6; i++) {
                            Location l = new Location(entity.getLocation().getWorld(), entity.getLocation().getX() + entity.getWidth() / 2, entity.getLocation().getY());
                            Vector3 vel = l.getVelocity();
                            vel.x = -v.x / 3 + l.getWorld().getRandom().nextFloat() * 4*scale - 2f*scale;
                            vel.y = -v.y / 3 + l.getWorld().getRandom().nextFloat() * 4*scale - 2f*scale;
                            Particle particle = new Particle(ParticleType.PUFF, l);
                            particle.setScale(scale);
                            entity.getLocation().getWorld().spawnParticle(particle, true);
                        }
                    }
                }

                if(v.y > 0 && (Gdx.input.isKeyPressed(DestriliteGame.getInstance().JUMP_KEY) || Gdx.input.isKeyPressed(DestriliteGame.getInstance().UP_KEY)))v.y= Math.min(v.y+jumpHeight*scale*delta*1.5f, jumpHeight*scale);
                if(Gdx.input.isKeyPressed(DestriliteGame.getInstance().DOWN_KEY))v.y-= jumpHeight*scale*delta*1.75f;


                if(Gdx.input.isKeyJustPressed(DestriliteGame.getInstance().DASH_LEFT) && entity.getTotalFrames()-lastDash>=dashTimer){
                    lastDash = entity.getTotalFrames();
                    v.x = v.x < -dashSpeed*scale ? v.x : Math.max(v.x-dashSpeed*scale, -dashSpeed*scale);
                    //v.x -= dashSpeed*scale;
                    for(int i = 0; i < 6; i++) {
                        Location l = new Location(entity.getLocation().getWorld(), entity.getLocation().getX()+ entity.getWidth()/2, entity.getLocation().getY());
                        Vector3 vel = l.getVelocity();
                        vel.x = -v.x/3 + l.getWorld().getRandom().nextFloat()*4*scale - 2f*scale;
                        vel.y = -v.y/3 + l.getWorld().getRandom().nextFloat()*4*scale - 2f*scale;
                        Particle particle = new Particle(ParticleType.PUFF, l);
                        particle.setScale(scale);
                        entity.getLocation().getWorld().spawnParticle(particle, true);
                    }
                }
                if(Gdx.input.isKeyJustPressed(DestriliteGame.getInstance().DASH_RIGHT) && entity.getTotalFrames()-lastDash>=dashTimer){
                    lastDash = entity.getTotalFrames();
                    v.x = v.x > dashSpeed*scale ? v.x : Math.min(v.x+dashSpeed*scale, dashSpeed*scale);
                    //v.x += dashSpeed*scale;
                    for(int i = 0; i < 6; i++) {
                        Location l = new Location(entity.getLocation().getWorld(), entity.getLocation().getX()+ entity.getWidth()/2, entity.getLocation().getY());
                        Vector3 vel = l.getVelocity();
                        vel.x = -v.x/3 + l.getWorld().getRandom().nextFloat()*4*scale - 2f*scale;
                        vel.y = -v.y/3 + l.getWorld().getRandom().nextFloat()*4*scale - 2f*scale;
                        Particle particle = new Particle(ParticleType.PUFF, l);
                        particle.setScale(scale);
                        entity.getLocation().getWorld().spawnParticle(particle, true);
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch, float delta){
        Screen sc = DestriliteGame.getInstance().getScreen();
        if(sc instanceof GameScreen) {
            GameScreen screen = (GameScreen) sc;
            float bob = MathUtils.sinDeg((entity.getTotalFrames()%1)*360) * pixel * 3 / 8;
            if(screen.getPlayer() == this) {
                Vector2 proj = screen.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                float x2 = proj.x, y2 = proj.y;
                flip = x2 < entity.getLocation().getX() + entity.getWidth() / 2;
                deg = MathUtils.radiansToDegrees * MathUtils.atan((y2 - (entity.getLocation().getY() + entity.getHeight() + bob)) / (x2 - (entity.getLocation().getX() + entity.getWidth() / 2)));
            }

            float pixel = scale * 0.125f, x = entity.getLocation().getX(), y = entity.getLocation().getY(), height = entity.getHeight(), width = entity.getWidth(),
                    x2 = limbLocation.getX(), y2 = limbLocation.getY(),limbSize = pixel * 3,
                    fW = (width - race.getFootOffset()*scale*2 - limbSize), rightFoot = MathUtils.sinDeg(walk-90) / 2f * fW,leftFoot = MathUtils.cosDeg(walk) / 2f * fW;

            Sprite headSprite = race.getHeadSprite(), bodySprite = race.getBodySprite(), leftHandSprite = race.getLeftHandSprite(), rightHandSprite = race.getRightHandSprite(), leftFootSprite = race.getLeftFootSprite(), rightFootSprite = race.getRightFootSprite();


            if(flip){
                headSprite.flip(true,false);
                bodySprite.flip(true, false);
                leftFootSprite.flip(true, false);
                leftHandSprite.flip(true, false);
                rightFootSprite.flip(true, false);
                rightHandSprite.flip(true, false);
            }

            batch.draw(flip ? rightFootSprite : leftFootSprite, x2+leftFoot+fW/2+race.getFootOffset()*scale, y2, limbSize,limbSize);
            batch.draw(flip ? rightHandSprite : leftHandSprite, x2+width-race.getHandOffsetX()*scale-limbSize, y2+race.getHandOffsetY()*scale, limbSize,limbSize);
            batch.draw(flip ? leftFootSprite : rightFootSprite, x2+rightFoot+fW/2+race.getFootOffset()*scale, y2, limbSize,limbSize);
            batch.draw(bodySprite, x+entity.getWidth()/2-race.getBodyWidth()*scale/2-pixel, y + 2*pixel + bob,race.bodyWidth*scale+2*pixel, race.getBodyHeight()*scale+pixel*2);
            batch.draw(flip ? leftHandSprite : rightHandSprite, x2+race.getHandOffsetX()*scale, y2+race.getHandOffsetY()*scale, limbSize,limbSize);
            batch.draw(headSprite, x+entity.getWidth()/2-race.getHeadWidth()*scale/2-pixel, y + height - race.headHeight*scale/2 + bob, race.getHeadWidth()*scale/2+pixel,0,race.getHeadWidth()*scale+pixel*2, race.getHeadHeight()*scale+pixel*2,1,1,deg);

            if(flip){
                headSprite.flip(true,false);
                bodySprite.flip(true, false);
                leftFootSprite.flip(true, false);
                leftHandSprite.flip(true, false);
                rightFootSprite.flip(true, false);
                rightHandSprite.flip(true, false);
            }


            race.render(this, batch, delta);
        }
    }








    //GETS

    public Entity getEntity() {
        return entity;
    }

    public static Player getPlayer(int id){
        for(Player player : players)if(player.getID() == id)return player;
        return null;
    }

    public int getID() {
        return ID;
    }

    public float getStr() {
        return str;
    }

    public float getMana() {
        return mana;
    }

    public float getMag() {
        return mag;
    }

    public float getHealth() {
        return health;
    }

    public float getDex() {
        return dex;
    }

    public float getDef() {
        return def;
    }

    public float getScale(){return scale;}

    public float getDeg() {
        return deg;
    }

    public boolean isFlipped() {
        return flip;
    }

    public Race getRace() {
        return race;
    }
}
