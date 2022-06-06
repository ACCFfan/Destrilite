package com.kittycatmedias.destrilite.entity.type.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
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

    private float health, dex, str, mag, mana, def, scale, deg, pixel, maxSpeed, jumpHeight, dashSpeed;
    private int id;

    private boolean flip;

    private static int nextID;

    private Race race;

    private Entity entity;

    private Location leftFootLocation, rightFootLocation, leftHandLocation, rightHandLocation;

    private static final Array<Player> players = new Array<>();

    public static Player registerPlayer(Player player){
        if(!players.contains(player, true))players.add(player);
        return player;
    }

    public Player(Race race, int id){
        this.race = race;
        if(id == -1) this.id = nextID++;
        else this.id = id;
        scale = 1f;
        pixel = 0.125f * scale;
        this.health = race.getHealth();
        this.dex = race.getDex();
        this.str = race.getStr();
        this.mag = race.getMag();
        this.mana = race.getMana();
        this.def = race.getDef();
        maxSpeed = 10;
        jumpHeight = 7.5f;
        dashSpeed = 25;
        ObjectMap<String, Object> meta = new ObjectMap<>();
        meta.put("race",race.getID());
        entity = new Entity(new Location(null, 0, 0), EntityType.PLAYER, meta, id+100);
        entity.setWidth(Math.max(race.getHeadWidth(), race.getBodyWidth()) * scale);
        entity.setHeight((race.getHeadHeight() + race.getBodyHeight() + 0.375f) * scale);
    }

    public static Player getPlayer(int id){
        for(Player player : players)if(player.getID() == id)return player;
        return null;
    }

    public int getID() {
        return id;
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

    public void update(float delta){
        Screen sc = DestriliteGame.getInstance().getScreen();
        if(sc instanceof GameScreen) {
            GameScreen screen = (GameScreen) sc;
            if (screen.getPlayer() == this) {
                Vector3 v = entity.getLocation().getVelocity();
                if(Gdx.input.isKeyPressed(DestriliteGame.getInstance().LEFT_KEY))v.x = v.x < -maxSpeed ? v.x : Math.max(v.x-maxSpeed*delta*7, -maxSpeed);
                if(Gdx.input.isKeyPressed(DestriliteGame.getInstance().RIGHT_KEY))v.x = v.x > maxSpeed ? v.x : Math.min(v.x+maxSpeed*delta*7, maxSpeed);

                if(Gdx.input.isKeyJustPressed(DestriliteGame.getInstance().JUMP_KEY)){
                    v.y= Math.min(v.y+jumpHeight, jumpHeight);
                    for(int i = 0; i < 6; i++) {
                        Location l = new Location(entity.getLocation().getWorld(), entity.getLocation().getX()+ entity.getWidth()/2, entity.getLocation().getY());
                        Vector3 vel = l.getVelocity();
                        vel.x = -v.x/3 + l.getWorld().getRandom().nextFloat()*4 - 2f;
                        vel.y = -v.y/3 + l.getWorld().getRandom().nextFloat()*4 - 2f;
                        entity.getLocation().getWorld().spawnParticle(new Particle(ParticleType.PUFF, l), true);
                    }
                }

                if(v.y >= 0 && (Gdx.input.isKeyPressed(DestriliteGame.getInstance().JUMP_KEY) || Gdx.input.isKeyPressed(DestriliteGame.getInstance().UP_KEY)))v.y= Math.min(v.y+jumpHeight*delta*1.5f, jumpHeight);
                if(Gdx.input.isKeyPressed(DestriliteGame.getInstance().DOWN_KEY))v.y-= jumpHeight*delta*1.75f;


                if(Gdx.input.isKeyJustPressed(DestriliteGame.getInstance().DASH_LEFT)){
                    v.x = v.x < -dashSpeed ? v.x : Math.max(v.x-dashSpeed, -dashSpeed);
                    for(int i = 0; i < 6; i++) {
                        Location l = new Location(entity.getLocation().getWorld(), entity.getLocation().getX()+ entity.getWidth()/2, entity.getLocation().getY());
                        Vector3 vel = l.getVelocity();
                        vel.x = -v.x/3 + l.getWorld().getRandom().nextFloat()*4 - 2f;
                        vel.y = -v.y/3 + l.getWorld().getRandom().nextFloat()*4 - 2f;
                        entity.getLocation().getWorld().spawnParticle(new Particle(ParticleType.PUFF, l), true);
                    }
                }
                if(Gdx.input.isKeyJustPressed(DestriliteGame.getInstance().DASH_RIGHT)){
                    v.x = v.x > dashSpeed ? v.x : Math.min(v.x+dashSpeed, dashSpeed);
                    for(int i = 0; i < 6; i++) {
                        Location l = new Location(entity.getLocation().getWorld(), entity.getLocation().getX()+ entity.getWidth()/2, entity.getLocation().getY());
                        Vector3 vel = l.getVelocity();
                        vel.x = -v.x/3 + l.getWorld().getRandom().nextFloat()*4 - 2f;
                        vel.y = -v.y/3 + l.getWorld().getRandom().nextFloat()*4 - 2f;
                        entity.getLocation().getWorld().spawnParticle(new Particle(ParticleType.PUFF, l), true);
                    }
                }
            }
        }
    }

    public void render(SpriteBatch batch, float delta){
        Screen sc = DestriliteGame.getInstance().getScreen();
        if(sc instanceof GameScreen) {
            GameScreen screen = (GameScreen) sc;
            if(screen.getPlayer() == this) {
                Vector2 proj = screen.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
                float x2 = proj.x, y2 = proj.y;
                flip = x2 < entity.getLocation().getX() + entity.getWidth() / 2;
                deg = MathUtils.radiansToDegrees * MathUtils.atan((y2 - (entity.getLocation().getY() + entity.getHeight() - race.getHeadHeight()/2)) / (x2 - (entity.getLocation().getX() + entity.getWidth() / 2)));
            }

            float pixel = scale * 0.125f, x = entity.getLocation().getX(), y = entity.getLocation().getY(), height = entity.getHeight(), width = entity.getWidth();

            Sprite headSprite = race.getHeadSprite(), bodySprite = race.getBodySprite(), leftHandSprite = race.getLeftHandSprite(), rightHandSprite = race.getRightHandSprite(), leftFootSprite = race.getLeftFootSprite(), rightFootSprite = race.getRightFootSprite();


            if(flip){
                headSprite.flip(true,false);
                bodySprite.flip(true, false);
                leftFootSprite.flip(true, false);
                leftHandSprite.flip(true, false);
                rightFootSprite.flip(true, false);
                rightHandSprite.flip(true, false);
            }
            batch.draw(bodySprite, x+entity.getWidth()/2-race.getBodyWidth()*scale/2-pixel, y + 2*pixel,race.bodyWidth*scale+2*pixel, race.getBodyHeight()*scale+pixel*2);
            batch.draw(headSprite, x+entity.getWidth()/2-race.getHeadWidth()*scale/2-pixel, y + height - race.headHeight*scale, race.getHeadWidth()*scale/2+pixel,0,race.getHeadWidth()*scale+pixel*2, race.getHeadHeight()*scale+pixel*2,1,1,deg);
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

    public Entity getEntity() {
        return entity;
    }
}
