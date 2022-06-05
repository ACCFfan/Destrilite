package com.kittycatmedias.destrilite.entity.type.player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.kittycatmedias.destrilite.entity.EntityType;
import com.kittycatmedias.destrilite.entity.type.player.race.HumanRace;
import com.kittycatmedias.destrilite.entity.type.player.race.ToyRace;

public abstract class Race {

    protected final String name;

    protected final float headWidth, headHeight, bodyWidth, bodyHeight, health, dex, str, mag, mana, def;
    protected final int id;

    protected Sprite headSprite, bodySprite, leftHandSprite, rightHandSprite, leftFootSprite, rightFootSprite;

    private static int nextID;

    private static final Array<Race> races = new Array<>();

    public static final Race HUMAN = registerRace(new HumanRace());
    public static final Race TOY = registerRace(new ToyRace());

    public static Race registerRace(Race race){
        if(!races.contains(race, true))races.add(race);
        return race;
    }

    public Race(String name, float headWidth, float headHeight, float bodyWidth, float bodyHeight, float health, float dex, float str, float mag, float mana, float def){
        this.name = name;
        this.headWidth = headWidth;
        this.headHeight = headHeight;
        this.bodyHeight = bodyHeight;
        this.bodyWidth = bodyWidth;
        this.health = health;
        this.dex = dex;
        this.str = str;
        this.mag = mag;
        this.mana = mana;
        this.def = def;
        id = nextID++;
    }

    public void generateSprites(TextureAtlas atlas) {
        headSprite = atlas.createSprite("race/"+name+"/head");
        bodySprite = atlas.createSprite("race/"+name+"/body");
        leftFootSprite = atlas.createSprite("race/"+name+"/left_foot");
        rightFootSprite = atlas.createSprite("race/"+name+"/right_foot");
        leftHandSprite = atlas.createSprite("race/"+name+"/left_hand");
        rightHandSprite = atlas.createSprite("race/"+name+"/right_hand");
    }

    public abstract void render(Player player, SpriteBatch batch,float delta);

    public void update(Player player, float delta){

    }

    public String getName() {
        return name;
    }

    public float getBodyHeight() {
        return bodyHeight;
    }

    public float getBodyWidth() {
        return bodyWidth;
    }

    public float getHeadHeight() {
        return headHeight;
    }

    public float getHeadWidth() {
        return headWidth;
    }

    public static Race getRace(int id) {
        return races.get(id);
    }

    public int getID() {
        return id;
    }

    public float getDef() {
        return def;
    }

    public float getDex() {
        return dex;
    }

    public float getHealth() {
        return health;
    }

    public float getMag() {
        return mag;
    }

    public float getMana() {
        return mana;
    }

    public float getStr() {
        return str;
    }

    public Sprite getBodySprite() {
        return bodySprite;
    }

    public Sprite getHeadSprite() {
        return headSprite;
    }

    public Sprite getLeftFootSprite() {
        return leftFootSprite;
    }

    public Sprite getLeftHandSprite() {
        return leftHandSprite;
    }

    public Sprite getRightFootSprite() {
        return rightFootSprite;
    }

    public Sprite getRightHandSprite() {
        return rightHandSprite;
    }

    public static Array<Race> getRaces(){
        return races;
    }
}
